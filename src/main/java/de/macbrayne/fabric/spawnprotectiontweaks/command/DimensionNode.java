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


        LiteralCommandNode<ServerCommandSource> dimensionsListNode = CommandManager
                .literal("list")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.spawnprotection.dimensions.list", 2))
                .executes(context -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    Reference.getConfig().dimensions.keySet().stream()
                            .sorted()
                            .forEach(dimensionKey -> {
                                if (Reference.getConfig().dimensions.get(dimensionKey).radius == 0) {
                                    return;
                                }
                                stringBuilder
                                        .append("\n")
                                        .append(LanguageHelper
                                                .format("commands.spawnprotectiontweaks.dimensions.list.format",
                                                        dimensionKey,
                                                        Reference.getConfig().dimensions.get(dimensionKey).radius));
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
                                String worldKey = dimensionKey.getValue().toString();
                                boolean containsKey = Reference.getConfig().dimensions.containsKey(worldKey);
                                stringBuilder
                                        .append("\n")
                                        .append(LanguageHelper
                                                .format("commands.spawnprotectiontweaks.dimensions.list.format",
                                                        worldKey,
                                                        containsKey ? Reference.getConfig().dimensions.get(worldKey).radius : "0"));
                            });
                    context.getSource().sendFeedback(LanguageHelper.getOptionalTranslation(context.getSource(), "commands.spawnprotectiontweaks.dimensions.list", stringBuilder.toString()), false);
                    return context.getSource().getWorldKeys().size();
                })
                .build();

        LiteralCommandNode<ServerCommandSource> dimensionsRadiusNode = CommandManager
                .literal("radius")
                .build();

        LiteralCommandNode<ServerCommandSource> dimensionsRadiusSetNode = CommandManager
                .literal("set")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.spawnprotection.dimensions.radius.set", 2))
                .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                        .then(CommandManager.argument("value", FloatArgumentType.floatArg(0)).executes(context -> {
                            DimensionArgumentType.getDimensionArgument(context, "dimension");
                            String argument = context.getArgument("dimension", Identifier.class).toString();
                            Reference.getConfig().dimensions.putIfAbsent(argument, new ModConfig.DimensionConfig());
                            Reference.getConfig().dimensions.get(argument).radius =
                                    context.getArgument("value", Float.class);
                            ServerLifecycle.saveConfig();
                            context.getSource().sendFeedback(
                                    LanguageHelper.getOptionalTranslation(context.getSource(),
                                            "commands.spawnprotectiontweaks.dimensions.radius.set",
                                            argument, Reference.getConfig().dimensions.get(argument).radius),
                                    true);
                            return 1;
                        }))
                )
                .build();

        LiteralCommandNode<ServerCommandSource> dimensionsRadiusQueryNode = CommandManager
                .literal("query")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.spawnprotection.radius.query", 2))
                .executes(context -> {
                    String worldKey = context.getSource().getWorld().getRegistryKey().getValue().toString();
                    Reference.getConfig().dimensions.putIfAbsent(worldKey, new ModConfig.DimensionConfig());
                    announceRadius(context, worldKey);
                    return 1;
                })
                .then(CommandManager.argument("dimension", DimensionArgumentType.dimension()).executes(context -> {
                    DimensionArgumentType.getDimensionArgument(context, "dimension");
                    String argument = context.getArgument("dimension", Identifier.class).toString();
                    Reference.getConfig().dimensions.putIfAbsent(argument, new ModConfig.DimensionConfig());
                    announceRadius(context, argument);
                    return 1;
                }))
                .build();



        LiteralCommandNode<ServerCommandSource> dimensionsActionbarNode = CommandManager
                .literal("actionbar")
                .build();

        LiteralCommandNode<ServerCommandSource> dimensionsActionBarSetNode = CommandManager
                .literal("set")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.spawnprotection.alert.set", 2))
                .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                        .then(CommandManager.argument("value", BoolArgumentType.bool()).executes(context -> {
                            DimensionArgumentType.getDimensionArgument(context, "dimension");
                            String argument = context.getArgument("dimension", Identifier.class).toString();
                            boolean value = context.getArgument("value", Boolean.class);
                            Reference.getConfig().dimensions.get(argument).actionBar = value;
                            ServerLifecycle.saveConfig();
                            BaseText action = LanguageHelper.getOptionalTranslation(context.getSource(),
                                    "commands.spawnprotectiontweaks.dimensions.actionbar." + (value ? "enable" : "disable"));
                            context.getSource().sendFeedback(action, true);
                            return 1;
                        })))
                .build();

        LiteralCommandNode<ServerCommandSource> dimensionsActionBarQueryNode = CommandManager
                .literal("query")
                .requires(source -> Permissions.check(source, "spawnprotectiontweaks.spawnprotection.alert.query", 2))
                .executes(context -> {
                    String worldKey = context.getSource().getWorld().getRegistryKey().getValue().toString();
                    Reference.getConfig().dimensions.putIfAbsent(worldKey, new ModConfig.DimensionConfig());
                    announceActionBarStatus(context, worldKey);
                    return 1;
                })
                .then(CommandManager.argument("dimension", DimensionArgumentType.dimension()).executes(context -> {
                    DimensionArgumentType.getDimensionArgument(context, "dimension");
                    String argument = context.getArgument("dimension", Identifier.class).toString();
                    Reference.getConfig().dimensions.putIfAbsent(argument, new ModConfig.DimensionConfig());
                    announceActionBarStatus(context, argument);
                    return 1;
                }))
                .build();

        dimensionsActionbarNode.addChild(dimensionsActionBarQueryNode);
        dimensionsActionbarNode.addChild(dimensionsActionBarSetNode);

        dimensionsRadiusNode.addChild(dimensionsRadiusQueryNode);
        dimensionsRadiusNode.addChild(dimensionsRadiusSetNode);

        dimensionsListNode.addChild(dimensionsListAllNode);

        dimensionsNode.addChild(dimensionsActionbarNode);
        dimensionsNode.addChild(dimensionsRadiusNode);
        dimensionsNode.addChild(dimensionsListNode);
        return dimensionsNode;
    }



    private static void announceRadius(CommandContext<ServerCommandSource> context, String worldKey) {
        context.getSource().sendFeedback(
                LanguageHelper.getOptionalTranslation(context.getSource(), "commands.spawnprotectiontweaks.dimensions.radius.query", worldKey, Reference.getConfig().dimensions.get(worldKey).radius),
                false);
    }

    private static void announceActionBarStatus(CommandContext<ServerCommandSource> context, String worldKey) {
        context.getSource().sendFeedback(LanguageHelper.getOptionalTranslation(context.getSource(), "commands.spawnprotectiontweaks.dimensions.actionbar.status." + (Reference.getConfig().dimensions.get(worldKey).actionBar ? "enabled" : "disabled")), false);
    }
}
