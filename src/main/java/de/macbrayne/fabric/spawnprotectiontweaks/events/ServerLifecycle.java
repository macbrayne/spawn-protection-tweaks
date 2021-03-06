package de.macbrayne.fabric.spawnprotectiontweaks.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.macbrayne.fabric.spawnprotectiontweaks.Reference;
import de.macbrayne.fabric.spawnprotectiontweaks.utils.ModConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ServerLifecycle {
    private static final File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "spawnprotectiontweaks.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @SuppressWarnings("unused")
    public static void onStart(MinecraftServer minecraftServer) {
        reloadConfig();
    }

    @SuppressWarnings("unused")
    public static void onReloadCommand(MinecraftServer minecraftServer, ServerResourceManager serverResourceManager, boolean b) {
        reloadConfig();
    }

    public static void reloadConfig() {
        ModConfig config = new ModConfig();
        try {
            if (configFile.exists()) {
                String jsonString = new String(Files.readAllBytes(configFile.toPath()));
                config = gson.fromJson(jsonString, ModConfig.class);
                if(config == null) {
                    config = new ModConfig();
                }
            } else {
                config = new ModConfig();
            }
            Files.writeString(configFile.toPath(), gson.toJson(config));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Reference.setConfig(config);
    }

    public static void saveConfig() {
        try {
            Files.writeString(configFile.toPath(), gson.toJson(Reference.getConfig()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
