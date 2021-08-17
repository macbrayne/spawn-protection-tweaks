package de.macbrayne.fabric.spawnprotectiontweaks.utils;

import de.macbrayne.fabric.spawnprotectiontweaks.Reference;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

public class ModConfig {
    public boolean enabled = false;
    public String alias = "spt";
    public DimensionConfig defaultConfig = new DimensionConfig();
    public final HashMap<String, DimensionConfig> dimensions = new HashMap<>();

    public static class DimensionConfig {
        DimensionConfig() {
        }

        public BlockPos position = new BlockPos(0, 60, 0);
        public float radius = 0;
        public boolean actionBar = true;
    }

    public void addDimension(Identifier identifier) {
        DimensionConfig config = new DimensionConfig();
        DimensionConfig defaultConfig = Reference.getConfig().defaultConfig;

        config.position = defaultConfig.position;
        config.actionBar = defaultConfig.actionBar;
        config.radius = defaultConfig.radius;

        dimensions.putIfAbsent(identifier.toString(), config);
    }

    public DimensionConfig getDimension(ServerWorld world) {
        return getDimension(world.getRegistryKey().getValue());
    }

    public DimensionConfig getDimension(Identifier identifier) {
        return dimensions.get(identifier.toString());
    }
}
