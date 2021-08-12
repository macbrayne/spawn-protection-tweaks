package de.macbrayne.fabric.spawnenhancements.server;

import de.macbrayne.fabric.spawnenhancements.Reference;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class SpawnProtection {
    public static void isSpawnProtected(ServerWorld world, BlockPos pos, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if(Reference.getConfig().enabled) {
            String worldKey = world.getRegistryKey().getValue().toString();
            if(Reference.getConfig().whitelist.parallelStream().noneMatch(worldKey::equals)) {
                cir.setReturnValue(false);
                return;
            }
            if(Reference.getConfig().radius <= 0) {
                cir.setReturnValue(false);
                return;
            }
            if(Permissions.check(player, "spawnenhancements.override", 1)) {
                cir.setReturnValue(false);
                return;
            }

            BlockPos spawnPosition = world.getSpawnPos();
            int relativeX = MathHelper.abs(pos.getX() - spawnPosition.getX());
            int relativeY = MathHelper.abs(pos.getZ() - spawnPosition.getZ());
            boolean isSpawnProtected = Math.max(relativeX, relativeY) <= Reference.getConfig().radius;
            if(isSpawnProtected && Reference.getConfig().actionBarMessage) {
                player.sendMessage(Text.of("This block is protected by spawn protection"), true);
            }
            cir.setReturnValue(isSpawnProtected);
        }
    }
}
