package de.macbrayne.fabric.spawnprotectiontweaks;

import de.macbrayne.fabric.spawnprotectiontweaks.command.CommandRegistry;
import de.macbrayne.fabric.spawnprotectiontweaks.events.PlayerEvents;
import de.macbrayne.fabric.spawnprotectiontweaks.events.ServerLifecycle;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.*;

@Environment(EnvType.SERVER)
public class SpawnProtectionTweaks implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycle::onStart);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(ServerLifecycle::onReloadCommand);
        CommandRegistrationCallback.EVENT.register(CommandRegistry::register);

        AttackBlockCallback.EVENT.register(PlayerEvents::onAttackBlock);
        UseBlockCallback.EVENT.register(PlayerEvents::onUseBlock);
        AttackEntityCallback.EVENT.register(PlayerEvents::onAttackEntity);
        UseEntityCallback.EVENT.register(PlayerEvents::onUseEntity);
        PlayerBlockBreakEvents.BEFORE.register(PlayerEvents::beforeBreakBlock);
        UseItemCallback.EVENT.register(PlayerEvents::onUseItem);
    }
}
