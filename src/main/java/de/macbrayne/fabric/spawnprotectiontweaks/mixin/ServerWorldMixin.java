package de.macbrayne.fabric.spawnprotectiontweaks.mixin;

import de.macbrayne.fabric.spawnprotectiontweaks.Reference;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Inject(at = @At(value = "HEAD"), method = "Lnet/minecraft/server/world/ServerWorld;getSpawnPos()Lnet/minecraft/util/math/BlockPos;", cancellable = true)
    public void adjustSpawnPos(CallbackInfoReturnable<BlockPos> cir) {
        Identifier worldIdentifier = ((WorldAccessor) this).getRegistryKey().getValue();
        if(Reference.getConfig().dimensions.containsKey(worldIdentifier.toString())) {
            if(Reference.getConfig().dimensions.get(worldIdentifier.toString()).centre == null) {
                return;
            }
            cir.setReturnValue(Reference.getConfig().getDimension(worldIdentifier).centre);
        } else if(!worldIdentifier.equals(World.OVERWORLD.getValue())) {
            cir.setReturnValue(Reference.getConfig().defaultConfig.centre);
        }
    }
}
