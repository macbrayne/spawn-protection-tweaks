package de.macbrayne.fabric.spawnprotectiontweaks;

import de.macbrayne.fabric.spawnprotectiontweaks.utils.ModConfig;

public class Reference {
    private static ModConfig config;

    public static ModConfig getConfig() {
        return config;
    }

    public static void setConfig(ModConfig config) {
        Reference.config = config;
    }
}