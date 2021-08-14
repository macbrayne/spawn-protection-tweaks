package de.macbrayne.fabric.spawnprotectiontweaks.client;

import de.macbrayne.fabric.spawnprotectiontweaks.Reference;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SpawnProtectionTweaksClient implements ClientModInitializer {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(Reference.LANGUAGE_PACKET_IDENTIFIER,
                (minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) ->
                        LOGGER.debug("Client shouldn't receive packets on " + Reference.LANGUAGE_PACKET_IDENTIFIER));
    }
}
