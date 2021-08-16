package de.macbrayne.fabric.spawnprotectiontweaks.utils;

import de.macbrayne.fabric.spawnprotectiontweaks.Reference;

import java.util.HashMap;

public class ModConfig {
    public boolean enabled = false;
    public String alias = "spt";
    public DimensionConfig defaultConfig = new DimensionConfig();
    public final HashMap<String, DimensionConfig> dimensions = new HashMap<>();

    public static class DimensionConfig {
        DimensionConfig() {
        }

        public float radius = 0;
        public boolean actionBar = true;

        public static DimensionConfig create() {
            DimensionConfig config = new DimensionConfig();
            config.actionBar = Reference.getConfig().defaultConfig.actionBar;
            config.radius = Reference.getConfig().defaultConfig.radius;
            return config;
        }
    }
}
