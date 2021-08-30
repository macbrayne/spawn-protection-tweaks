package de.macbrayne.fabric.spawnprotectiontweaks.utils;

import de.macbrayne.fabric.spawnprotectiontweaks.Reference;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class ModConfig {
    public boolean enabled = false;
    public String alias = "spt";
    public DimensionConfig defaultConfig = getDefaultDefaultConfig();
    public final HashMap<String, DimensionConfig> dimensions = new HashMap<>();

    public static class DimensionConfig {
        DimensionConfig() {
        }

        public DimensionConfig(BlockPos centre, float radius, boolean actionBar) {
            this.centre = centre;
            this.radius = radius;
            this.actionBar = actionBar;
        }

        public BlockPos centre;
        public float radius;
        public boolean actionBar;
    }

    public void addDimension(Identifier identifier) {
        DimensionConfig config = new DimensionConfig();
        DimensionConfig defaultConfig = Reference.getConfig().defaultConfig;

        config.centre = defaultConfig.centre;
        config.actionBar = defaultConfig.actionBar;
        config.radius = defaultConfig.radius;

        if(identifier.equals(World.OVERWORLD.getValue())) {
            config.centre = null;
        }

        dimensions.putIfAbsent(identifier.toString(), config);
    }

    public DimensionConfig getDimension(ServerWorld world) {
        return getDimension(world.getRegistryKey().getValue());
    }

    public DimensionConfig getDimension(Identifier identifier) {
        return dimensions.get(identifier.toString());
    }

    public static DimensionConfig getDefaultDefaultConfig() {
        return new DimensionConfig(new BlockPos(0, 60, 0), 0, true);
    }
}
