package de.macbrayne.fabric.spawnprotectiontweaks;

import de.macbrayne.fabric.spawnprotectiontweaks.utils.ModConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class Reference {
    private static ModConfig config;
    public static final String MOD_ID = "spawnprotectiontweaks";
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static final String MOD_VERSION = FabricLoader.getInstance().getModContainer(Reference.MOD_ID).get().getMetadata().getVersion().getFriendlyString();
    public static final Identifier LANGUAGE_PACKET_IDENTIFIER = new Identifier(MOD_ID, "accepts_language_keys");

    public static ModConfig getConfig() {
        return config;
    }

    public static void setConfig(ModConfig config) {
        Reference.config = config;
    }
}