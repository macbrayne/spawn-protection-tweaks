package de.macbrayne.fabric.spawnenhancements.server;

import de.macbrayne.fabric.spawnenhancements.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class SpawnProtection {
    public static void isSpawnProtected(ServerWorld world, BlockPos pos, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if(Reference.getConfig().enabled) {
            cir.setReturnValue(true);
        }
    }
}
