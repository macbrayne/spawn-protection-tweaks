package de.macbrayne.fabric.spawnprotectiontweaks.server;

import de.macbrayne.fabric.spawnprotectiontweaks.command.CommandRegistry;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;

@Environment(EnvType.SERVER)
public class SpawnProtectionTweaksServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycle::onStart);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(ServerLifecycle::onReloadCommand);
        CommandRegistrationCallback.EVENT.register(CommandRegistry::register);

        AttackBlockCallback.EVENT.register(PlayerEvents::onAttack);
        UseBlockCallback.EVENT.register(PlayerEvents::onUse);
        AttackEntityCallback.EVENT.register(PlayerEvents::onAttackEntity);
        UseEntityCallback.EVENT.register(PlayerEvents::onUseEntity);
    }
}
