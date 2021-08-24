package de.macbrayne.fabric.spawnprotectiontweaks.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.macbrayne.fabric.spawnprotectiontweaks.Reference;
import de.macbrayne.fabric.spawnprotectiontweaks.server.ServerLifecycle;
import de.macbrayne.fabric.spawnprotectiontweaks.utils.LanguageHelper;
import de.macbrayne.fabric.spawnprotectiontweaks.utils.ModConfig;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.BaseText;
import net.minecraft.util.Identifier;

import java.util.Comparator;

public class DimensionNode {
    static LiteralCommandNode<ServerCommandSource> get() {
        return CommandManager
                .literal("dimensions")
                .then(getListNode())
                .then(getRadiusNode())
                .then(getActionBarNode())
                .build();
    }

    // region /spt dimensions list <all>
    private static LiteralArgumentBuilder<ServerCommandSource> getListNode() {
        return CommandManager
                .literal("list")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.spawnprotection.dimensions.list", 2))
                .executes(context -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    Reference.getConfig().dimensions.keySet().stream()
                            .sorted()
                            .forEach(dimensionKey -> {
                                ModConfig.DimensionConfig dimensionConfig = Reference.getConfig().dimensions.get(dimensionKey);
                                if (dimensionConfig.radius == Reference.getConfig().defaultConfig.radius
                                        && dimensionConfig.actionBar == Reference.getConfig().defaultConfig.actionBar) {
                                    return;
                                }
                                stringBuilder
                                        .append("\n")
                                        .append(LanguageHelper
                                                .format("commands.spawnprotectiontweaks.dimensions.list.format",
                                                        dimensionKey,
                                                        Reference.getConfig().dimensions.get(dimensionKey).radius,
                                                        Reference.getConfig().dimensions.get(dimensionKey).actionBar));
                            });
                    context.getSource().sendFeedback(LanguageHelper.getOptionalTranslation(context.getSource(), "commands.spawnprotectiontweaks.dimensions.list", stringBuilder.toString()), false);
                    return Reference.getConfig().dimensions.size();
                })
                .then(getListAllNode());
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getListAllNode() {
        return CommandManager
                .literal("all")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.spawnprotection.dimensions.list.all", 2))
                .executes(context -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    context.getSource().getWorldKeys().stream().sorted(Comparator.comparing(o -> o.getValue().toString()))
                            .forEach(dimensionKey -> {
                                Identifier worldKey = dimensionKey.getValue();
                                boolean containsKey = Reference.getConfig().dimensions.containsKey(worldKey.toString());
                                stringBuilder
                                        .append("\n")
                                        .append(LanguageHelper
                                                .format("commands.spawnprotectiontweaks.dimensions.list.format",
                                                        worldKey,
                                                        containsKey ? Reference.getConfig().getDimension(worldKey).radius :
                                                                Reference.getConfig().defaultConfig.radius,
                                                        containsKey ? Reference.getConfig().getDimension(worldKey).actionBar :
                                                                Reference.getConfig().defaultConfig.actionBar));
                            });
                    context.getSource().sendFeedback(LanguageHelper.getOptionalTranslation(context.getSource(), "commands.spawnprotectiontweaks.dimensions.list", stringBuilder.toString()), false);
                    return context.getSource().getWorldKeys().size();
                });
    }

    // endregion


    // region /spt dimensions radius [set/query] [dimension] <value>
    private static LiteralArgumentBuilder<ServerCommandSource> getRadiusNode() {
        return CommandManager
                .literal("radius")
                .then(getRadiusQueryNode())
                .then(getRadiusSetNode());
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getRadiusSetNode() {
        return CommandManager
                .literal("set")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.spawnprotection.dimensions.radius.set", 2))
                .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                        .then(CommandManager.argument("value", FloatArgumentType.floatArg(0)).executes(context -> {
                            DimensionArgumentType.getDimensionArgument(context, "dimension");
                            Identifier argument = context.getArgument("dimension", Identifier.class);
                            Reference.getConfig().addDimension(argument);
                            Reference.getConfig().getDimension(argument).radius =
                                    context.getArgument("value", Float.class);
                            ServerLifecycle.saveConfig();

                            boolean isDefault = Reference.getConfig().getDimension(argument).radius == Reference.getConfig().defaultConfig.radius;
                            context.getSource().sendFeedback(
                                    LanguageHelper.getOptionalTranslation(context.getSource(),
                                            "commands.spawnprotectiontweaks.dimensions.radius.set" +
                                                    getSuffix(isDefault),
                                            argument, Reference.getConfig().getDimension(argument).radius),
                                    true);
                            return Command.SINGLE_SUCCESS;
                        }))
                );
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getRadiusQueryNode() {
        return CommandManager
                .literal("query")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.spawnprotection.radius.query", 2))
                .executes(context -> {
                    Identifier worldKey = context.getSource().getWorld().getRegistryKey().getValue();
                    return Math.round(announceRadius(context, worldKey));
                })
                .then(CommandManager.argument("dimension", DimensionArgumentType.dimension()).executes(context -> {
                    Identifier argument = DimensionArgumentType.getDimensionArgument(context, "dimension").getRegistryKey().getValue();
                    return Math.round(announceRadius(context, argument));
                }));
    }
    // endregion


    // region /spt dimensions actionbar [set/query] [dimension] <value>
    private static LiteralArgumentBuilder<ServerCommandSource> getActionBarNode() {
        return CommandManager
                .literal("actionbar")
                .then(getActionBarSetNode())
                .then(getActionBarQueryNode());
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getActionBarSetNode() {
        return CommandManager
                .literal("set")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.spawnprotection.alert.set", 2))
                .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                        .then(CommandManager.argument("value", BoolArgumentType.bool()).executes(context -> {
                            Identifier argument =  DimensionArgumentType.getDimensionArgument(context, "dimension").getRegistryKey().getValue();
                            boolean value = context.getArgument("value", Boolean.class);
                            Reference.getConfig().getDimension(argument).actionBar = value;
                            ServerLifecycle.saveConfig();

                            boolean isDefault = Reference.getConfig().getDimension(argument).actionBar == Reference.getConfig().defaultConfig.actionBar;
                            BaseText action = LanguageHelper.getOptionalTranslation(context.getSource(),
                                    "commands.spawnprotectiontweaks.dimensions.actionbar." +
                                            (value ? "enable" : "disable") +
                                            getSuffix(isDefault),
                                    argument.toString());
                            context.getSource().sendFeedback(action, true);
                            return Command.SINGLE_SUCCESS;
                        })));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getActionBarQueryNode() {
        return CommandManager
                .literal("query")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.spawnprotection.alert.query", 2))
                .executes(context -> {
                    Identifier worldKey = context.getSource().getWorld().getRegistryKey().getValue();
                    return announceActionBarStatus(context, worldKey) ? 1 : 0;
                })
                .then(CommandManager.argument("dimension", DimensionArgumentType.dimension()).executes(context -> {
                    Identifier argument = DimensionArgumentType.getDimensionArgument(context, "dimension").getRegistryKey().getValue();
                    return announceActionBarStatus(context, argument) ? 1 : 0;
                }));
    }
    //endregion


    // region Utility

    private static float announceRadius(CommandContext<ServerCommandSource> context, Identifier worldKey) {
        Reference.getConfig().addDimension(worldKey);
        ModConfig modConfig = Reference.getConfig();
        String translationKey = "commands.spawnprotectiontweaks.dimensions.radius.query" +
                getSuffix(modConfig.getDimension(worldKey).radius == modConfig.defaultConfig.radius);
        context.getSource().sendFeedback(
                LanguageHelper.getOptionalTranslation(context.getSource(), translationKey, worldKey, Reference.getConfig().getDimension(worldKey).radius),
                false);
        return modConfig.getDimension(worldKey).radius;
    }

    private static boolean announceActionBarStatus(CommandContext<ServerCommandSource> context, Identifier worldKey) {
        Reference.getConfig().addDimension(worldKey);
        ModConfig modConfig = Reference.getConfig();
        String translationKey = "commands.spawnprotectiontweaks.dimensions.actionbar.status" +
                (Reference.getConfig().getDimension(worldKey).actionBar ? ".enabled" : ".disabled") +
                getSuffix(modConfig.getDimension(worldKey).actionBar == modConfig.defaultConfig.actionBar);
        context.getSource().sendFeedback(LanguageHelper.getOptionalTranslation(context.getSource(), translationKey, worldKey.toString()), false);
        return modConfig.getDimension(worldKey).actionBar;
    }

    private static String getSuffix(boolean isDefault) {
        return isDefault ? ".default" : "";
    }
    // endregion
}
