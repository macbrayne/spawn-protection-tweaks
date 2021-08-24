package de.macbrayne.fabric.spawnprotectiontweaks.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.macbrayne.fabric.spawnprotectiontweaks.Reference;
import de.macbrayne.fabric.spawnprotectiontweaks.server.ServerLifecycle;
import de.macbrayne.fabric.spawnprotectiontweaks.utils.LanguageHelper;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class CommandRegistry {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, @SuppressWarnings("unused") boolean dedicated) {
        ServerLifecycle.reloadConfig();

        LiteralCommandNode<ServerCommandSource> rootNode = dispatcher.register(getRootNode());

        if(Reference.getConfig().alias != null && !Reference.getConfig().alias.isBlank()) {
            dispatcher.register(getAliasNode(rootNode));
        }
    }

    private static Command<ServerCommandSource> printHelp() {
        return context -> {
            ServerCommandSource source = context.getSource();
            source.sendFeedback(LanguageHelper.getOptionalTranslation
                    (source, "commands.spawnprotectiontweaks"), true); // TODO: Add help
            return Command.SINGLE_SUCCESS;
        };
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getAliasNode(LiteralCommandNode<ServerCommandSource> root) {
        return CommandManager.literal(Reference.getConfig().alias)
                .redirect(root);
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getRootNode() {
        return CommandManager
                .literal("spawnprotectiontweaks")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks", 2))
                .executes(printHelp())
                .then(getHelpNode())
                .then(getEnabledNode())
                .then(DimensionNode.build())
                .then(getReloadNode());
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getHelpNode() {
        return CommandManager
                .literal("help")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.reload", 2))
                .executes(printHelp());
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getReloadNode() {
        return CommandManager
                .literal("reload")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.reload", 2))
                .executes(context -> {
                    context.getSource().sendFeedback(LanguageHelper.getOptionalTranslation(context.getSource(), "commands.spawnprotectiontweaks.reload"), true);
                    ServerLifecycle.reloadConfig();
                    return Command.SINGLE_SUCCESS;
                });
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getEnabledNode() {
        return CommandManager
                .literal("enabled")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.spawnprotection.enabled", 2))
                .executes(context -> {
                    context.getSource().sendFeedback(LanguageHelper.getOptionalTranslation(context.getSource(), "commands.spawnprotectiontweaks.status." + (Reference.getConfig().enabled ? "enabled" : "disabled")), false);
                    return Command.SINGLE_SUCCESS;
                })
                .then(CommandManager.argument("value", BoolArgumentType.bool()).executes(context -> {
                    boolean value = context.getArgument("value", Boolean.class);
                    Reference.getConfig().enabled = value;
                    ServerLifecycle.saveConfig();
                    String action = value ? "enable" : "disable";
                    context.getSource().sendFeedback(LanguageHelper.getOptionalTranslation(context.getSource(), "commands.spawnprotectiontweaks." + action), true);
                    return Command.SINGLE_SUCCESS;
                }));
    }

}
