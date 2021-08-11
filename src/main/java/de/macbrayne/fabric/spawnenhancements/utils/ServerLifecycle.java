package de.macbrayne.fabric.spawnenhancements.utils;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import de.macbrayne.fabric.spawnenhancements.Reference;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.IOException;

public class ServerLifecycle {
    private static final File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "spawnenhancements.toml");

    @SuppressWarnings("unused")
    public static void onStart(MinecraftServer minecraftServer) {
        reloadConfig();
    }

    @SuppressWarnings("unused")
    public static void onReloadCommand(MinecraftServer minecraftServer, ServerResourceManager serverResourceManager, boolean b) {
        reloadConfig();
    }

    static void reloadConfig() {
        ModConfig config = new ModConfig();
        try {
            if (configFile.exists()) {
                config = new Toml().read(configFile).to(ModConfig.class);
            } else {
                config = new ModConfig();
            }
            new TomlWriter().write(config, configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Reference.setConfig(config);
    }

    static void saveConfig() {
        try {
            new TomlWriter().write(Reference.getConfig(), configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
