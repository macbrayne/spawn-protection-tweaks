package de.macbrayne.fabric.spawnprotectiontweaks.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.macbrayne.fabric.spawnprotectiontweaks.Reference;
import de.macbrayne.fabric.spawnprotectiontweaks.events.ServerLifecycle;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class CommandRegistry {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, @SuppressWarnings("unused") boolean dedicated) {
        ServerLifecycle.reloadConfig();

        LiteralCommandNode<ServerCommandSource> rootNode = dispatcher.register(CommandManager
                .literal("spawnprotectiontweaks")
                .requires(Permissions.require(PermissionsReference.MODULE, 2))
                .executes(printHelp())
                .then(getHelpNode())
                .then(getEnabledNode())
                .then(DimensionNode.build())
                .then(getReloadNode()));

        if(Reference.getConfig().alias != null && !Reference.getConfig().alias.isBlank()) {
            dispatcher.register(getAliasNode(rootNode));
        }
    }

    private static Command<ServerCommandSource> printHelp() {
        return context -> {
            ServerCommandSource source = context.getSource();
            source.sendFeedback(new TranslatableText("commands.spawnprotectiontweaks", Reference.MOD_VERSION, Reference.getConfig().defaultConfig.actionBar, Reference.getConfig().defaultConfig.radius), false);
            return Command.SINGLE_SUCCESS;
        };
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getAliasNode(LiteralCommandNode<ServerCommandSource> root) {
        return CommandManager.literal(Reference.getConfig().alias)
                .requires(Permissions.require(PermissionsReference.MODULE, 2))
                .executes(printHelp())
                .redirect(root);
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getHelpNode() {
        return CommandManager
                .literal("help")
                .requires(Permissions.require(PermissionsReference.MODULE, 2))
                .executes(printHelp());
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getReloadNode() {
        return CommandManager
                .literal("reload")
                .requires(Permissions.require(PermissionsReference.RELOAD, 2))
                .executes(context -> {
                    context.getSource().sendFeedback(new TranslatableText("commands.spawnprotectiontweaks.reload"), true);
                    ServerLifecycle.reloadConfig();
                    return Command.SINGLE_SUCCESS;
                });
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getEnabledNode() {
        return CommandManager
                .literal("enabled")
                .requires(Permissions.require(PermissionsReference.ENABLED.root, 2))
                .then(getEnabledSetNode())
                .then(getEnabledQueryNode());
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getEnabledSetNode() {
        return CommandManager
                .literal("set")
                .requires(Permissions.require(PermissionsReference.ENABLED.set, 2))
                .then(CommandManager.argument("value", BoolArgumentType.bool())
                        .executes(context -> {
                            boolean value = context.getArgument("value", Boolean.class);
                            Reference.getConfig().enabled = value;
                            ServerLifecycle.saveConfig();
                            String action = value ? "enable" : "disable";
                            context.getSource().sendFeedback(new TranslatableText("commands.spawnprotectiontweaks." + action), true);
                            return Command.SINGLE_SUCCESS;
                        }));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getEnabledQueryNode() {
        return CommandManager
                .literal("query")
                .requires(Permissions.require(PermissionsReference.ENABLED.query, 2))
                .executes(context -> {
                    final String translationKey = "commands.spawnprotectiontweaks.status." + (Reference.getConfig().enabled ? "enabled" : "disabled");
                    context.getSource().sendFeedback(new TranslatableText(translationKey), false);
                    return Reference.getConfig().enabled ? 1 : 0;
                });
    }

    // region Permissions
    private record PermissionsReference() {
        private static final String MODULE = Reference.MOD_ID;

        public static final String RELOAD = MODULE + ".reload";

        public static final VanillaSyntaxGroup ENABLED = new VanillaSyntaxGroup(MODULE, ".enabled");
    }
    // endregion
}
