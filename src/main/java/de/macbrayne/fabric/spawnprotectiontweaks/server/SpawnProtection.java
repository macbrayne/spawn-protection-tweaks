package de.macbrayne.fabric.spawnprotectiontweaks.server;

import de.macbrayne.fabric.spawnprotectiontweaks.Reference;
import de.macbrayne.fabric.spawnprotectiontweaks.utils.ModConfig;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

public class SpawnProtection {
    public static void isSpawnProtected(ServerWorld world, BlockPos pos, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        Optional<Boolean> protectionStatus = isProtected(world, pos, player);
        protectionStatus.ifPresent(result -> {
            if(result && Reference.getConfig().getOrDefault(world).actionBar) {
                player.sendMessage(new TranslatableText("actionbar.spawnprotectiontweaks.interaction.attack.block"), true);
            }
            cir.setReturnValue(result);
        });
    }

    public static Optional<Boolean> isProtected(ServerWorld world, BlockPos pos, PlayerEntity player) {
        if(Reference.getConfig().enabled) {
            ModConfig.DimensionConfig dimensionConfig = Reference.getConfig().getOrDefault(world);
            if(dimensionConfig.radius <= 0) {
                return Optional.of(false);
            }
            if(Permissions.check(player, "spawnprotectiontweaks.override", 1)) {
                return Optional.of(false);
            }

            BlockPos spawnPosition = world.getSpawnPos();
            int relativeX = MathHelper.abs(pos.getX() - spawnPosition.getX());
            int relativeY = MathHelper.abs(pos.getZ() - spawnPosition.getZ());
            boolean isSpawnProtected = Math.max(relativeX, relativeY) <= dimensionConfig.radius;
            return Optional.of(isSpawnProtected);
        }
        return Optional.empty();
    }
}
