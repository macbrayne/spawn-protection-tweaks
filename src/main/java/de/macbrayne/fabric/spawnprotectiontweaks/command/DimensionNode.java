package de.macbrayne.fabric.spawnprotectiontweaks.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
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
        LiteralCommandNode<ServerCommandSource> dimensionsNode = CommandManager
                .literal("dimensions")
                .build();


        // region /spt dimensions list <all>
        LiteralCommandNode<ServerCommandSource> dimensionsListNode = CommandManager
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
                .build();

        LiteralCommandNode<ServerCommandSource> dimensionsListAllNode = CommandManager
                .literal("all")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.spawnprotection.dimensions.list.all", 2))
                .executes(context -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    System.out.println(context.getSource());
                    System.out.println(context.getSource().getWorldKeys());
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
                })
                .build();
        // endregion

        // region /spt dimensions radius [set/query] [dimension] <value>
        LiteralCommandNode<ServerCommandSource> dimensionsRadiusNode = CommandManager
                .literal("radius")
                .build();

        LiteralCommandNode<ServerCommandSource> dimensionsRadiusSetNode = CommandManager
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
                            return 1;
                        }))
                )
                .build();

        LiteralCommandNode<ServerCommandSource> dimensionsRadiusQueryNode = CommandManager
                .literal("query")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.spawnprotection.radius.query", 2))
                .executes(context -> {
                    Identifier worldKey = context.getSource().getWorld().getRegistryKey().getValue();
                    Reference.getConfig().addDimension(worldKey);
                    announceRadius(context, worldKey);
                    return 1;
                })
                .then(CommandManager.argument("dimension", DimensionArgumentType.dimension()).executes(context -> {
                    DimensionArgumentType.getDimensionArgument(context, "dimension");
                    Identifier argument = context.getArgument("dimension", Identifier.class);
                    Reference.getConfig().addDimension(argument);
                    announceRadius(context, argument);
                    return 1;
                }))
                .build();
        // endregion

        // region /spt dimensions actionbar [set/query] [dimension] <value>
        LiteralCommandNode<ServerCommandSource> dimensionsActionbarNode = CommandManager
                .literal("actionbar")
                .build();

        LiteralCommandNode<ServerCommandSource> dimensionsActionBarSetNode = CommandManager
                .literal("set")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.spawnprotection.alert.set", 2))
                .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                        .then(CommandManager.argument("value", BoolArgumentType.bool()).executes(context -> {
                            DimensionArgumentType.getDimensionArgument(context, "dimension");
                            Identifier argument = context.getArgument("dimension", Identifier.class);
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
                            return 1;
                        })))
                .build();

        LiteralCommandNode<ServerCommandSource> dimensionsActionBarQueryNode = CommandManager
                .literal("query")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.spawnprotection.alert.query", 2))
                .executes(context -> {
                    Identifier worldKey = context.getSource().getWorld().getRegistryKey().getValue();
                    Reference.getConfig().addDimension(worldKey);
                    announceActionBarStatus(context, worldKey);
                    return 1;
                })
                .then(CommandManager.argument("dimension", DimensionArgumentType.dimension()).executes(context -> {
                    DimensionArgumentType.getDimensionArgument(context, "dimension");
                    Identifier argument = context.getArgument("dimension", Identifier.class);
                    Reference.getConfig().addDimension(argument);
                    announceActionBarStatus(context, argument);
                    return 1;
                }))
                .build();
        //endregion

        // region addChildren
        dimensionsActionbarNode.addChild(dimensionsActionBarQueryNode);
        dimensionsActionbarNode.addChild(dimensionsActionBarSetNode);

        dimensionsRadiusNode.addChild(dimensionsRadiusQueryNode);
        dimensionsRadiusNode.addChild(dimensionsRadiusSetNode);

        dimensionsListNode.addChild(dimensionsListAllNode);

        dimensionsNode.addChild(dimensionsActionbarNode);
        dimensionsNode.addChild(dimensionsRadiusNode);
        dimensionsNode.addChild(dimensionsListNode);
        // endregion
        return dimensionsNode;
    }


    private static void announceRadius(CommandContext<ServerCommandSource> context, Identifier worldKey) {
        ModConfig modConfig = Reference.getConfig();
        announceRadius(context, worldKey,
                modConfig.getDimension(worldKey).radius == modConfig.defaultConfig.radius);
    }

    private static void announceRadius(CommandContext<ServerCommandSource> context, Identifier worldKey, boolean isDefault) {
        String translationKey = "commands.spawnprotectiontweaks.dimensions.radius.query" +
                getSuffix(isDefault);
        context.getSource().sendFeedback(
                LanguageHelper.getOptionalTranslation(context.getSource(), translationKey, worldKey, Reference.getConfig().getDimension(worldKey).radius),
                false);
    }

    private static void announceActionBarStatus(CommandContext<ServerCommandSource> context, Identifier worldKey) {
        ModConfig modConfig = Reference.getConfig();
        announceActionBarStatus(context, worldKey,
                modConfig.getDimension(worldKey).actionBar == modConfig.defaultConfig.actionBar);
    }

    private static void announceActionBarStatus(CommandContext<ServerCommandSource> context, Identifier worldKey, boolean isDefault) {
        String translationKey = "commands.spawnprotectiontweaks.dimensions.actionbar.status" +
                (Reference.getConfig().getDimension(worldKey).actionBar ? ".enabled" : ".disabled") +
                getSuffix(isDefault);
        context.getSource().sendFeedback(LanguageHelper.getOptionalTranslation(context.getSource(), translationKey, worldKey.toString()), false);
    }

    private static String getSuffix(boolean isDefault) {
        return isDefault ? ".default" : "";
    }
}
