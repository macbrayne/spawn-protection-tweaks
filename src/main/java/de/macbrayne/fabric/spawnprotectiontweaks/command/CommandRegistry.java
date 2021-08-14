package de.macbrayne.fabric.spawnprotectiontweaks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.macbrayne.fabric.spawnprotectiontweaks.Reference;
import de.macbrayne.fabric.spawnprotectiontweaks.server.ServerLifecycle;
import de.macbrayne.fabric.spawnprotectiontweaks.utils.LanguageHelper;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.BaseText;
import net.minecraft.util.Identifier;

import java.util.List;

public class CommandRegistry {
    private static final Dynamic2CommandExceptionType DIMENSION_NOT_WHITELISTED =
            new Dynamic2CommandExceptionType((o1, o2) ->
                    LanguageHelper.getOptionalTranslation((ServerCommandSource) o1,
                            "commands.spawnprotectiontweaks.radius.dimension.missing", o2));
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

        LiteralCommandNode<ServerCommandSource> radiusNode = CommandManager
                .literal("radius")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.radius", 2))
                .executes(context -> {
                    announceRadius(context, getWorldKey(context));
                    return 1;
                })
                .then(CommandManager.argument("dimension", DimensionArgumentType.dimension()).executes(context -> {
                            String argument = context.getArgument("dimension", Identifier.class).toString();
                            if (!Reference.getConfig().whitelist.containsKey(argument)) {
                                throw DIMENSION_NOT_WHITELISTED.create(context.getSource(), argument);
                            }
                            announceRadius(context, argument);
                            return 1;
                        })
                        .then(CommandManager.argument("value", FloatArgumentType.floatArg(0)).executes(context -> {
                                    String argument = context.getArgument("dimension", Identifier.class).toString();
                                    if (!Reference.getConfig().whitelist.containsKey(argument)) {
                                        throw DIMENSION_NOT_WHITELISTED.create(context.getSource(), argument);
                                    }
                                    Reference.getConfig().whitelist.get(argument).radius =
                                            context.getArgument("value", Float.class);
                                    ServerLifecycle.saveConfig();
                                    context.getSource().sendFeedback(
                                            LanguageHelper.getOptionalTranslation(context.getSource(),
                                                    "commands.spawnprotectiontweaks.radius.dimension.set",
                                                    argument, Reference.getConfig().whitelist.get(argument).radius),
                                            true);
                                    return 1;
                                })
                        ))
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

        LiteralCommandNode<ServerCommandSource> alertNode = CommandManager
                .literal("alert")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.spawnprotection.alert", 2))
                .executes(context -> {
                    context.getSource().sendFeedback(LanguageHelper.getOptionalTranslation(context.getSource(), "commands.spawnprotectiontweaks.actionbar.status." + (Reference.getConfig().alert ? "enabled" : "disabled")), false);
                    return 1;
                })
                .then(CommandManager.argument("value", BoolArgumentType.bool()).executes(context -> {
                    boolean value = context.getArgument("value", Boolean.class);
                    Reference.getConfig().alert = value;
                    ServerLifecycle.saveConfig();
                    BaseText action = LanguageHelper.getOptionalTranslation(context.getSource(),
                            "commands.spawnprotectiontweaks.actionbar." + (value ? "enable" : "disable"));
                    context.getSource().sendFeedback(action, true);
                    return 1;
                }))
                .build();

        List<LiteralCommandNode<ServerCommandSource>> children = List.of(enabledNode, radiusNode, WhitelistNode.get(), reloadNode, alertNode);
        addAlias("spawnprotectiontweaks", dispatcher, children);
        if(Reference.getConfig().alias != null && !Reference.getConfig().alias.isBlank()) {
            addAlias(Reference.getConfig().alias, dispatcher, children);
        }
    }

    private static String getWorldKey(CommandContext<ServerCommandSource> context) {
        return context.getSource().getWorld().getRegistryKey().getValue().toString();
    }

    private static void announceRadius(CommandContext<ServerCommandSource> context, String worldKey) throws CommandSyntaxException {
        if (!Reference.getConfig().whitelist.containsKey(getWorldKey(context))) {
            throw DIMENSION_NOT_WHITELISTED.create(context.getSource(), worldKey);
        }
        context.getSource().sendFeedback(
                LanguageHelper.getOptionalTranslation(context.getSource(), "commands.spawnprotectiontweaks.radius.query", worldKey, Reference.getConfig().whitelist.get(worldKey).radius),
                false);
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
