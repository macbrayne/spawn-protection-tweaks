package de.macbrayne.fabric.spawnenhancements;

import de.macbrayne.fabric.spawnenhancements.utils.ModConfig;

public class Reference {
    private static ModConfig config;

    public static ModConfig getConfig() {
        return config;
    }

    public static void setConfig(ModConfig config) {
        Reference.config = config;
    }
}