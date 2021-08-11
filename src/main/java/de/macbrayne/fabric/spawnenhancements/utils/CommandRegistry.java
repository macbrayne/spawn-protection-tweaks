package de.macbrayne.fabric.spawnenhancements.utils;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.macbrayne.fabric.spawnenhancements.Reference;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class CommandRegistry {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        LiteralCommandNode<ServerCommandSource> spawnEnhancementsNode = CommandManager
                .literal("spawnenhancements")
                .requires(source -> Permissions.check(source, "spawnenhancements", 2))
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    source.sendFeedback(Text.of("Non Functional Command"), true);
                    return 1;
                }).build();

        LiteralCommandNode<ServerCommandSource> enabledNode = CommandManager
                .literal("enabled")
                .requires(source -> Permissions.check(source, "spawnenhancements.enabled", 2))
                .executes(context -> {
                    context.getSource().sendFeedback(Text.of("SpawnEnhancements is currently " + (Reference.getConfig().enabled ? "enabled" : "not enabled")), true);
                    return 1;
                })
                .then(CommandManager.argument("value", BoolArgumentType.bool()).executes(context -> {
                    Reference.getConfig().enabled = context.getArgument("value", Boolean.class);
                    ServerLifecycle.saveConfig();
                    String action = Reference.getConfig().enabled ? "Enabled" : "Disabled";
                    context.getSource().sendFeedback(Text.of(action + " SpawnEnhancements"), true);
                    return 1;
                }))
                .build();

        dispatcher.getRoot().addChild(spawnEnhancementsNode);
        spawnEnhancementsNode.addChild(enabledNode);
    }
}
