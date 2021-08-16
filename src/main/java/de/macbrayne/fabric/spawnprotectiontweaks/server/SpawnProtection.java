package de.macbrayne.fabric.spawnprotectiontweaks.server;

import de.macbrayne.fabric.spawnprotectiontweaks.Reference;
import de.macbrayne.fabric.spawnprotectiontweaks.utils.LanguageHelper;
import de.macbrayne.fabric.spawnprotectiontweaks.utils.ModConfig;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

public class SpawnProtection {
    public static void isSpawnProtected(ServerWorld world, BlockPos pos, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if(Reference.getConfig().enabled) {
            String worldKey = world.getRegistryKey().getValue().toString();
            HashMap<String, ModConfig.DimensionConfig> dimensions = Reference.getConfig().dimensions;
            if(dimensions.keySet().parallelStream().noneMatch(worldKey::equals)) {
                cir.setReturnValue(false);
                return;
            }
            ModConfig.DimensionConfig dimensionConfig = dimensions.get(worldKey);
            if(dimensionConfig.radius <= 0) {
                cir.setReturnValue(false);
                return;
            }
            if(Permissions.check(player, "spawnprotectiontweaks.override", 1)) {
                cir.setReturnValue(false);
                return;
            }

            BlockPos spawnPosition = world.getSpawnPos();
            int relativeX = MathHelper.abs(pos.getX() - spawnPosition.getX());
            int relativeY = MathHelper.abs(pos.getZ() - spawnPosition.getZ());
            boolean isSpawnProtected = Math.max(relativeX, relativeY) <= dimensionConfig.radius;
            if(isSpawnProtected && dimensionConfig.actionBar) {
                player.sendMessage(LanguageHelper.getOptionalTranslation(player, "commands.spawnprotectiontweaks.actionbar.message"), true);
            }
            cir.setReturnValue(isSpawnProtected);
        }
    }
}
