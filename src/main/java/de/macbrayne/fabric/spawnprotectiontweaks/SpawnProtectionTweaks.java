package de.macbrayne.fabric.spawnprotectiontweaks;

import de.macbrayne.fabric.spawnprotectiontweaks.command.CommandRegistry;
import de.macbrayne.fabric.spawnprotectiontweaks.utils.ServerLifecycle;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

@Environment(EnvType.SERVER)
public class SpawnProtectionTweaks implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycle::onStart);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(ServerLifecycle::onReloadCommand);
        CommandRegistrationCallback.EVENT.register(CommandRegistry::register);
    }
}
