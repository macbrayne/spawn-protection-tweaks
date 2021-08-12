package de.macbrayne.fabric.spawnprotectiontweaks.command;

import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.macbrayne.fabric.spawnprotectiontweaks.Reference;
import de.macbrayne.fabric.spawnprotectiontweaks.utils.ModConfig;
import de.macbrayne.fabric.spawnprotectiontweaks.utils.ServerLifecycle;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class WhitelistNode {
    private static final DynamicCommandExceptionType ADD_FAILED_EXCEPTION = new DynamicCommandExceptionType(o -> Text.of("Failed to add " + o));
    private static final DynamicCommandExceptionType REMOVE_FAILED_EXCEPTION = new DynamicCommandExceptionType(o -> Text.of("Failed to remove " + o));

    static LiteralCommandNode<ServerCommandSource> get() {
        LiteralCommandNode<ServerCommandSource> whitelistNode = CommandManager
                .literal("whitelist")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.spawnprotection.whitelist", 2))
                .executes(context -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Whitelisted Dimensions:");
                    Reference.getConfig().whitelist.keySet().forEach(dimensionKey -> stringBuilder.append("\n").append(dimensionKey));
                    context.getSource().sendFeedback(Text.of(stringBuilder.toString()), false);
                    return 1;
                })
                .build();

        LiteralCommandNode<ServerCommandSource> whitelistAddNode = CommandManager
                .literal("add")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.spawnprotection.whitelist.add", 2))
                .then(CommandManager.argument("dimension", DimensionArgumentType.dimension()).executes(context -> {
                    String argument = context.getArgument("dimension", Identifier.class).toString();
                    if (Reference.getConfig().whitelist.containsKey(argument)) {
                        throw ADD_FAILED_EXCEPTION.create(argument);
                    }
                    Reference.getConfig().whitelist.put(argument, new ModConfig.DimensionConfig());
                    ServerLifecycle.saveConfig();
                    context.getSource().sendFeedback(Text.of("Added " + argument), true);
                    return 1;
                }))
                .build();

        LiteralCommandNode<ServerCommandSource> whitelistRemoveNode = CommandManager
                .literal("remove")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.spawnprotection.whitelist.remove", 2))
                .then(CommandManager.argument("dimension", DimensionArgumentType.dimension()).executes(context -> {
                    String argument = context.getArgument("dimension", Identifier.class).toString();
                    if (!Reference.getConfig().whitelist.containsKey(argument)) {
                        throw REMOVE_FAILED_EXCEPTION.create(argument);
                    }
                    Reference.getConfig().whitelist.remove(argument, new ModConfig.DimensionConfig());
                    ServerLifecycle.saveConfig();
                    context.getSource().sendFeedback(Text.of("Removed " + argument), true);
                    return 1;
                }))
                .build();


        whitelistNode.addChild(whitelistAddNode);
        whitelistNode.addChild(whitelistRemoveNode);
        return whitelistNode;
    }
}
