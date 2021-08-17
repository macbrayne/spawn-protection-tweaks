package de.macbrayne.fabric.spawnprotectiontweaks.mixin;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(World.class)
public interface WorldAccessor {
    @Accessor
    RegistryKey<World> getRegistryKey();
}
