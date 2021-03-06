package de.macbrayne.fabric.spawnprotectiontweaks.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.macbrayne.fabric.spawnprotectiontweaks.Reference;
import de.macbrayne.fabric.spawnprotectiontweaks.events.ServerLifecycle;
import de.macbrayne.fabric.spawnprotectiontweaks.utils.LanguageHelper;
import de.macbrayne.fabric.spawnprotectiontweaks.utils.ModConfig;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.BaseText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Comparator;

public class DimensionNode {
    static LiteralCommandNode<ServerCommandSource> build() {
        return CommandManager
                .literal("dimensions")
                .requires(Permissions.require(DimensionsPermissions.MODULE, 2))
                .then(getListNode())
                .then(getRadiusNode())
                .then(getActionBarNode())
                .then(getCentreNode())
                .then(getDefaultsNode())
                .build();
    }

    // region /spt dimensions list <all>
    private static LiteralArgumentBuilder<ServerCommandSource> getListNode() {
        return CommandManager
                .literal("list")
                .requires(Permissions.require(DimensionsPermissions.LIST, 2))
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
                    context.getSource().sendFeedback(LanguageHelper.getOptionalTranslation(stringBuilder.toString()), false);
                    return Reference.getConfig().dimensions.size();
                })
                .then(getListAllNode());
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getListAllNode() {
        return CommandManager
                .literal("all")
                .requires(Permissions.require(DimensionsPermissions.LIST_ALL, 2))
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
                    context.getSource().sendFeedback(LanguageHelper.getOptionalTranslation(stringBuilder.toString()), false);
                    return context.getSource().getWorldKeys().size();
                });
    }

    // endregion


    // region /spt dimensions radius [set/query] [dimension] <value>
    private static LiteralArgumentBuilder<ServerCommandSource> getRadiusNode() {
        return CommandManager
                .literal("radius")
                .requires(Permissions.require(DimensionsPermissions.RADIUS.root, 2))
                .then(getRadiusQueryNode())
                .then(getRadiusSetNode());
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getRadiusSetNode() {
        return CommandManager
                .literal("set")
                .requires(Permissions.require(DimensionsPermissions.RADIUS.set, 2))
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
                                    LanguageHelper.getOptionalTranslation("commands.spawnprotectiontweaks.dimensions.radius.set" +
                                            getSuffix(isDefault), argument, Reference.getConfig().getDimension(argument).radius),
                                    true);
                            return Command.SINGLE_SUCCESS;
                        }))
                );
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getRadiusQueryNode() {
        return CommandManager
                .literal("query")
                .requires(Permissions.require(DimensionsPermissions.RADIUS.query, 2))
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
                .requires(Permissions.require(DimensionsPermissions.ACTIONBAR.root, 2))
                .then(getActionBarSetNode())
                .then(getActionBarQueryNode());
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getActionBarSetNode() {
        return CommandManager
                .literal("set")
                .requires(Permissions.require(DimensionsPermissions.ACTIONBAR.set, 2))
                .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                        .then(CommandManager.argument("value", BoolArgumentType.bool()).executes(context -> {
                            Identifier argument =  DimensionArgumentType.getDimensionArgument(context, "dimension").getRegistryKey().getValue();
                            boolean value = context.getArgument("value", Boolean.class);
                            Reference.getConfig().getDimension(argument).actionBar = value;
                            ServerLifecycle.saveConfig();

                            boolean isDefault = Reference.getConfig().getDimension(argument).actionBar == Reference.getConfig().defaultConfig.actionBar;
                            final String translationKey = "commands.spawnprotectiontweaks.dimensions.actionbar." +
                                    (value ? "enable" : "disable") +
                                    getSuffix(isDefault);
                            BaseText action = LanguageHelper.getOptionalTranslation(argument.toString());
                            context.getSource().sendFeedback(action, true);
                            return Command.SINGLE_SUCCESS;
                        })));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getActionBarQueryNode() {
        return CommandManager
                .literal("query")
                .requires(Permissions.require(DimensionsPermissions.ACTIONBAR.query, 2))
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

    // region /spt dimensions defaults [set/query] [dimension] <value>
    private static LiteralArgumentBuilder<ServerCommandSource> getDefaultsNode() {
        return CommandManager
                .literal("defaults")
                .requires(Permissions.require(DimensionsPermissions.DEFAULTS.root, 2))
                .then(getDefaultsSetNode())
                .then(getDefaultsQueryNode())
                .then(getDefaultsResetNode());
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getDefaultsSetNode() {
        return CommandManager
                .literal("set")
                .requires(Permissions.require(DimensionsPermissions.DEFAULTS.set, 2))
                .then(CommandManager.literal("radius")
                        .then(CommandManager.argument("value", FloatArgumentType.floatArg(0)).executes(context -> {
                            Reference.getConfig().defaultConfig.radius =
                                    context.getArgument("value", Float.class);
                            ServerLifecycle.saveConfig();

                            context.getSource().sendFeedback(
                                    LanguageHelper.getOptionalTranslation(Reference.getConfig().defaultConfig.radius + ""),
                                    true);

                            return Command.SINGLE_SUCCESS;
                        })))
                .then(CommandManager.literal("actionbar")
                        .then(CommandManager.argument("value", BoolArgumentType.bool())).executes(context -> {
                            Reference.getConfig().defaultConfig.actionBar = context.getArgument("value", Boolean.class);
                            ServerLifecycle.saveConfig();

                            final String translationKey = "commands.spawnprotectiontweaks.dimensions.defaults.set.actionbar" +
                            (Reference.getConfig().defaultConfig.actionBar ? ".enabled" : ".disabled");
                            context.getSource().sendFeedback(
                                    LanguageHelper.getOptionalTranslation(translationKey),
                                    true);
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(CommandManager.literal("centre")
                        .then(CommandManager.argument("value", BlockPosArgumentType.blockPos()).executes(context -> {
                            Reference.getConfig().defaultConfig.centre = BlockPosArgumentType.getBlockPos(context, "value");
                            ServerLifecycle.saveConfig();

                            context.getSource().sendFeedback(
                                    LanguageHelper.getOptionalTranslation(Reference.getConfig().defaultConfig.centre.toShortString()),
                                    true);

                            return Command.SINGLE_SUCCESS;
                        })));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getDefaultsQueryNode() {
        return CommandManager
                .literal("query")
                .requires(Permissions.require(DimensionsPermissions.DEFAULTS.query, 2))
                .then(CommandManager.literal("radius").executes(context -> {
                    context.getSource().sendFeedback(
                            LanguageHelper.getOptionalTranslation(Reference.getConfig().defaultConfig.radius + ""),
                            false);
                    return Math.round(Reference.getConfig().defaultConfig.radius);
                }))
                .then(CommandManager.literal("actionbar").executes(context -> {
                    final String translationKey = "commands.spawnprotectiontweaks.dimensions.defaults.query.actionbar" +
                            (Reference.getConfig().defaultConfig.actionBar ? ".enabled" : ".disabled");
                    context.getSource().sendFeedback(
                            LanguageHelper.getOptionalTranslation(translationKey),
                            false);
                    return Reference.getConfig().defaultConfig.actionBar ? 1 : 0;
                }))
                .then(CommandManager.literal("centre").executes(context -> {
                    context.getSource().sendFeedback(
                            LanguageHelper.getOptionalTranslation(Reference.getConfig().defaultConfig.centre.toShortString()),
                            false);
                    return Command.SINGLE_SUCCESS;
                }));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getDefaultsResetNode() {
        return CommandManager
                .literal("reset")
                .requires(Permissions.require(DimensionsPermissions.DEFAULTS.reset, 2))
                .executes(context -> {
                    Reference.getConfig().defaultConfig = ModConfig.getDefaultDefaultConfig();
                    ServerLifecycle.saveConfig();

                    context.getSource().sendFeedback(
                            LanguageHelper.getOptionalTranslation("commands.spawnprotectiontweaks.dimensions.defaults.reset"),
                            false);
                    return Command.SINGLE_SUCCESS;
                });
    }

    // endregion

    // region /spt dimensions centre [set/query/reset] [dimension] <value>

    private static LiteralArgumentBuilder<ServerCommandSource> getCentreNode() {
        return CommandManager
                .literal("centre")
                .requires(Permissions.require(DimensionsPermissions.CENTRE.root, 2))
                .then(getCentreSetNode())
                .then(getCentreQueryNode())
                .then(getCentreResetNode());
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getCentreSetNode() {
        return CommandManager
                .literal("set")
                .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                        .then(CommandManager.argument("value", BlockPosArgumentType.blockPos())
                                .requires(Permissions.require(DimensionsPermissions.CENTRE.set, 2))
                                .executes(context -> {
                                    Identifier argument = DimensionArgumentType.getDimensionArgument(context, "dimension").getRegistryKey().getValue();
                                    BlockPos value = BlockPosArgumentType.getBlockPos(context, "value");
                                    Reference.getConfig().getDimension(argument).centre = value;
                                    ServerLifecycle.saveConfig();

                                    boolean isDefault = Reference.getConfig().getDimension(argument).centre.equals(Reference.getConfig().defaultConfig.centre);
                                    BaseText action = LanguageHelper.getOptionalTranslation("commands.spawnprotectiontweaks.dimensions.centre.set" +
                                            getSuffix(isDefault), argument.toString(), value.toShortString());
                                    context.getSource().sendFeedback(action, true);
                                    return Command.SINGLE_SUCCESS;
                                })));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getCentreQueryNode() {
        return CommandManager
                .literal("query")
                .requires(Permissions.require(DimensionsPermissions.CENTRE.query, 2))
                .executes(context -> {
                    ServerWorld world = context.getSource().getWorld();
                    announceCentrePos(context, world);
                    return Command.SINGLE_SUCCESS;
                })
                .then(CommandManager.argument("dimension", DimensionArgumentType.dimension()).executes(context -> {
                    ServerWorld argument = DimensionArgumentType.getDimensionArgument(context, "dimension");
                    announceCentrePos(context, argument);
                    return Command.SINGLE_SUCCESS;
                }));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getCentreResetNode() {
        return CommandManager
                .literal("reset")
                .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                        .requires(Permissions.require(DimensionsPermissions.CENTRE.reset, 2))
                        .executes(context -> {
                            ServerWorld serverWorld = DimensionArgumentType.getDimensionArgument(context, "dimension");
                            Identifier argument = serverWorld.getRegistryKey().getValue();

                            Reference.getConfig().getDimension(argument).centre = null;
                            ServerLifecycle.saveConfig();

                            String translationKey = "commands.spawnprotectiontweaks.dimensions.centre.reset";
                            if (argument.equals(World.OVERWORLD.getValue())) {
                                translationKey = translationKey + ".spawn";
                            }

                            BaseText action = LanguageHelper.getOptionalTranslation(translationKey, argument.toString(), serverWorld.getSpawnPos().toShortString());
                            context.getSource().sendFeedback(action, true);

                            return Command.SINGLE_SUCCESS;
                        }));
    }

    // endregion

    // region Utility

    private static float announceRadius(CommandContext<ServerCommandSource> context, Identifier worldKey) {
        Reference.getConfig().addDimension(worldKey);
        ModConfig modConfig = Reference.getConfig();
        String translationKey = "commands.spawnprotectiontweaks.dimensions.radius.query" +
                getSuffix(modConfig.getDimension(worldKey).radius == modConfig.defaultConfig.radius);
        context.getSource().sendFeedback(
                LanguageHelper.getOptionalTranslation(translationKey, worldKey, Reference.getConfig().getDimension(worldKey).radius),
                false);
        return modConfig.getDimension(worldKey).radius;
    }

    private static boolean announceActionBarStatus(CommandContext<ServerCommandSource> context, Identifier worldKey) {
        Reference.getConfig().addDimension(worldKey);
        ModConfig modConfig = Reference.getConfig();
        String translationKey = "commands.spawnprotectiontweaks.dimensions.actionbar.status" +
                (Reference.getConfig().getDimension(worldKey).actionBar ? ".enabled" : ".disabled") +
                getSuffix(modConfig.getDimension(worldKey).actionBar == modConfig.defaultConfig.actionBar);
        context.getSource().sendFeedback(LanguageHelper.getOptionalTranslation(worldKey.toString()), false);
        return modConfig.getDimension(worldKey).actionBar;
    }

    private static void announceCentrePos(CommandContext<ServerCommandSource> context, ServerWorld world) {
        Identifier worldKey = world.getRegistryKey().getValue();
        boolean isOverworld = worldKey.equals(World.OVERWORLD.getValue());

        Reference.getConfig().addDimension(worldKey);
        ModConfig modConfig = Reference.getConfig();
        BlockPos centre = world.getSpawnPos();
        String suffix = ((isOverworld && (modConfig.getDimension(worldKey).centre == null ||
                modConfig.getDimension(worldKey).centre.equals(world.getSpawnPos()))) ? ".spawn" : getSuffix(centre.equals(modConfig.defaultConfig.centre)));
        String translationKey = "commands.spawnprotectiontweaks.dimensions.centre.query" +
                suffix;
        context.getSource().sendFeedback(
                LanguageHelper.getOptionalTranslation(translationKey, worldKey, centre.toShortString()),
                false);
    }

    private static String getSuffix(boolean isDefault) {
        return isDefault ? ".default" : "";
    }
    // endregion

    // region Permissions
    private record DimensionsPermissions() {
        private static final String MODULE = Reference.MOD_ID + ".dimensions";

        public static final VanillaSyntaxGroup RADIUS = new VanillaSyntaxGroup(MODULE, ".radius");

        public static final VanillaSyntaxGroup ACTIONBAR = new VanillaSyntaxGroup(MODULE, ".actionbar");

        public static final VanillaSyntaxGroup CENTRE = new VanillaSyntaxGroup(MODULE, ".centre");

        public static final VanillaSyntaxGroup DEFAULTS = new VanillaSyntaxGroup(MODULE, ".defaults");

        public static final String LIST = MODULE + ".list";
        public static final String LIST_ALL = LIST + ".all";
    }
    // endregion
}
