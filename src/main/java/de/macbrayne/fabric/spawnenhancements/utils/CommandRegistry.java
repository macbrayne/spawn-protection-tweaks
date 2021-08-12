package de.macbrayne.fabric.spawnenhancements.utils;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.macbrayne.fabric.spawnenhancements.Reference;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CommandRegistry {
    private static final DynamicCommandExceptionType ADD_FAILED_EXCEPTION = new DynamicCommandExceptionType(o -> Text.of("Failed to add " + o));
    private static final DynamicCommandExceptionType REMOVE_FAILED_EXCEPTION = new DynamicCommandExceptionType(o -> Text.of("Failed to remove " + o));

    private static final DynamicCommandExceptionType DIMENSION_NOT_WHITELISTED = new DynamicCommandExceptionType(o -> Text.of("Dimension " + o + " not whitelisted"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, @SuppressWarnings("unused") boolean dedicated) {
        LiteralCommandNode<ServerCommandSource> spawnEnhancementsNode = CommandManager
                .literal("spawnenhancements")
                .requires(source -> Permissions.check(source, "spawnenhancements", 2))
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    source.sendFeedback(Text.of("TODO: Add help"), true); // TODO: Add help
                    return 1;
                }).build();

        LiteralCommandNode<ServerCommandSource> enabledNode = CommandManager
                .literal("enabled")
                .requires(source -> Permissions.check(source, "spawnenhancements.spawnprotection.enabled", 2))
                .executes(context -> {
                    context.getSource().sendFeedback(Text.of("SpawnEnhancements is currently " + (Reference.getConfig().enabled ? "enabled" : "not enabled")), false);
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

        LiteralCommandNode<ServerCommandSource> whitelistNode = CommandManager
                .literal("whitelist")
                .requires(source -> Permissions.check(source, "spawnenhancements.spawnprotection.whitelist", 2))
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
                .requires(source -> Permissions.check(source, "spawnenhancements.spawnprotection.whitelist.add", 2))
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
                .requires(source -> Permissions.check(source, "spawnenhancements.spawnprotection.whitelist.remove", 2))
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

        LiteralCommandNode<ServerCommandSource> radiusNode = CommandManager
                .literal("radius")
                .requires(source -> Permissions.check(source, "spawnenhancements.radius", 2))
                .executes(context -> {
                    announceRadius(context, getWorldKey(context));
                    return 1;
                })
                .then(CommandManager.argument("dimension", DimensionArgumentType.dimension()).executes(context -> {
                            String argument = context.getArgument("dimension", Identifier.class).toString();
                            announceRadius(context, argument);
                            return 1;
                        })
                        .then(CommandManager.argument("value", FloatArgumentType.floatArg(0)).executes(context -> {
                                    String argument = context.getArgument("dimension", Identifier.class).toString();
                                    if (!Reference.getConfig().whitelist.containsKey(argument)) {
                                        throw DIMENSION_NOT_WHITELISTED.create(argument);
                                    }
                                    Reference.getConfig().whitelist.get(argument).radius =
                                            context.getArgument("value", Float.class);
                                    ServerLifecycle.saveConfig();
                                    context.getSource().sendFeedback(
                                            Text.of("Set Radius in " + argument + " to " +
                                                    Reference.getConfig().whitelist.get(argument).radius),
                                            true);
                                    return 1;
                                })
                        ))
                .build();

        LiteralCommandNode<ServerCommandSource> reloadNode = CommandManager
                .literal("reload")
                .requires(source -> Permissions.check(source, "spawnenhancements.reload", 2))
                .executes(context -> {
                    ServerLifecycle.reloadConfig();
                    context.getSource().sendFeedback(Text.of("Reloaded config"), true);
                    return 1;
                })
                .build();

        LiteralCommandNode<ServerCommandSource> alertNode = CommandManager
                .literal("alert")
                .requires(source -> Permissions.check(source, "spawnenhancements.spawnprotection.alert", 2))
                .executes(context -> {
                    context.getSource().sendFeedback(Text.of("The action bar alert is currently " + (Reference.getConfig().alert ? "enabled" : "not enabled")), false);
                    return 1;
                })
                .then(CommandManager.argument("value", BoolArgumentType.bool()).executes(context -> {
                    Reference.getConfig().alert = context.getArgument("value", Boolean.class);
                    ServerLifecycle.saveConfig();
                    String action = Reference.getConfig().alert ? "Enabled" : "Disabled";
                    context.getSource().sendFeedback(Text.of(action + " the action bar alert"), true);
                    return 1;
                }))
                .build();

        whitelistNode.addChild(whitelistAddNode);
        whitelistNode.addChild(whitelistRemoveNode);
        dispatcher.getRoot().addChild(spawnEnhancementsNode);
        spawnEnhancementsNode.addChild(enabledNode);
        spawnEnhancementsNode.addChild(radiusNode);
        spawnEnhancementsNode.addChild(whitelistNode);
        spawnEnhancementsNode.addChild(reloadNode);
        spawnEnhancementsNode.addChild(alertNode);
    }

    private static String getWorldKey(CommandContext<ServerCommandSource> context) {
        return context.getSource().getWorld().getRegistryKey().getValue().toString();
    }

    private static void announceRadius(CommandContext<ServerCommandSource> context, String worldKey) throws CommandSyntaxException {
        if (!Reference.getConfig().whitelist.containsKey(getWorldKey(context))) {
            throw DIMENSION_NOT_WHITELISTED.create(worldKey);
        }
        context.getSource().sendFeedback(
                Text.of("Current spawn protection radius in " +
                        worldKey + " is " +
                        Reference.getConfig().whitelist.get(worldKey).radius),
                false);
    }
}
