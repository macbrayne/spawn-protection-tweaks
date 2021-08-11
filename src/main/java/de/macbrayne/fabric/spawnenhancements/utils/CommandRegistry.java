package de.macbrayne.fabric.spawnenhancements.utils;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.macbrayne.fabric.spawnenhancements.Reference;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.network.MessageType;
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
                    source.getPlayer().sendMessage(Text.of("Non Functional Command"), MessageType.CHAT, source.getPlayer().getUuid());
                    return 1;
                }).build();

        LiteralCommandNode<ServerCommandSource> enabledNode = CommandManager
                .literal("enable")
                .requires(source -> Permissions.check(source, "spawnenhancements.enable", 2))
                .executes(context -> {
                    Reference.getConfig().enabled = true;
                    ServerLifecycle.saveConfig();
                    return 1;
                }).build();

        dispatcher.getRoot().addChild(spawnEnhancementsNode);
        spawnEnhancementsNode.addChild(enabledNode);
    }
}
