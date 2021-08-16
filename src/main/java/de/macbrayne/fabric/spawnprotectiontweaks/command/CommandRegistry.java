package de.macbrayne.fabric.spawnprotectiontweaks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.macbrayne.fabric.spawnprotectiontweaks.Reference;
import de.macbrayne.fabric.spawnprotectiontweaks.server.ServerLifecycle;
import de.macbrayne.fabric.spawnprotectiontweaks.utils.LanguageHelper;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

public class CommandRegistry {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, @SuppressWarnings("unused") boolean dedicated) {
        ServerLifecycle.reloadConfig();
        LiteralCommandNode<ServerCommandSource> enabledNode = CommandManager
                .literal("enabled")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.spawnprotection.enabled", 2))
                .executes(context -> {
                    context.getSource().sendFeedback(LanguageHelper.getOptionalTranslation(context.getSource(), "commands.spawnprotectiontweaks.status." + (Reference.getConfig().enabled ? "enabled" : "disabled")), false);
                    return 1;
                })
                .then(CommandManager.argument("value", BoolArgumentType.bool()).executes(context -> {
                    boolean value = context.getArgument("value", Boolean.class);
                    Reference.getConfig().enabled = value;
                    ServerLifecycle.saveConfig();
                    String action = value ? "enable" : "disable";
                    context.getSource().sendFeedback(LanguageHelper.getOptionalTranslation(context.getSource(), "commands.spawnprotectiontweaks." + action), true);
                    return 1;
                }))
                .build();

        LiteralCommandNode<ServerCommandSource> reloadNode = CommandManager
                .literal("reload")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.reload", 2))
                .executes(context -> {
                    context.getSource().sendFeedback(LanguageHelper.getOptionalTranslation(context.getSource(), "commands.spawnprotectiontweaks.reload"), true);
                    ServerLifecycle.reloadConfig();
                    return 1;
                })
                .build();

        List<LiteralCommandNode<ServerCommandSource>> children = List.of(enabledNode, DimensionNode.get(), reloadNode);
        addAlias("spawnprotectiontweaks", dispatcher, children);
        if(Reference.getConfig().alias != null && !Reference.getConfig().alias.isBlank()) {
            addAlias(Reference.getConfig().alias, dispatcher, children);
        }
    }

    private static void addAlias(String literal, CommandDispatcher<ServerCommandSource> dispatcher, List<LiteralCommandNode<ServerCommandSource>> children) {
        LiteralCommandNode<ServerCommandSource> spawnProtectionTweaksNode = CommandManager
                .literal(literal)
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks", 2))
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    source.sendFeedback(LanguageHelper.getOptionalTranslation
                            (source, "commands.spawnprotectiontweaks"), true); // TODO: Add help
                    return 1;
                }).build();

        children.forEach(spawnProtectionTweaksNode::addChild);
        dispatcher.getRoot().addChild(spawnProtectionTweaksNode);
    }
}
