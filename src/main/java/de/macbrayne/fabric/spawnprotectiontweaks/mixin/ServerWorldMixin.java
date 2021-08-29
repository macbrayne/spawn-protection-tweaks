package de.macbrayne.fabric.spawnprotectiontweaks.mixin;

import de.macbrayne.fabric.spawnprotectiontweaks.Reference;
import de.macbrayne.fabric.spawnprotectiontweaks.utils.ModConfig;
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
    @Inject(at = @At(value = "HEAD"), method = "getSpawnPos()Lnet/minecraft/util/math/BlockPos;", cancellable = true)
    public void adjustSpawnPos(CallbackInfoReturnable<BlockPos> cir) {
        Identifier worldIdentifier = ((WorldAccessor) this).getRegistryKey().getValue();
        if(worldIdentifier.equals(World.OVERWORLD.getValue())) {
            return;
        }

        ModConfig config = Reference.getConfig();
        if(config.dimensions.containsKey(worldIdentifier.toString()) &&
                config.dimensions.get(worldIdentifier.toString()).centre != null) {
            cir.setReturnValue(config.getDimension(worldIdentifier).centre);
        } else {
            cir.setReturnValue(config.defaultConfig.centre);
        }
    }
}
