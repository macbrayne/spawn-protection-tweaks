package de.macbrayne.fabric.spawnprotectiontweaks;

import de.macbrayne.fabric.spawnprotectiontweaks.events.ServerLifecycle;
import de.macbrayne.fabric.spawnprotectiontweaks.utils.ModConfig;
import net.fabricmc.loader.api.FabricLoader;

public class Reference {
    private static ModConfig config;
    public static final String MOD_ID = "spawnprotectiontweaks";
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static final String MOD_VERSION = FabricLoader.getInstance().getModContainer(Reference.MOD_ID).get().getMetadata().getVersion().getFriendlyString();

    public static ModConfig getConfig() {
        if(config == null) {
            ServerLifecycle.reloadConfig();
        }
        return config;
    }

    public static void setConfig(ModConfig config) {
        Reference.config = config;
    }
}