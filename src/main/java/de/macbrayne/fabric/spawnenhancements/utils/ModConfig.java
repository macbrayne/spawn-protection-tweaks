package de.macbrayne.fabric.spawnenhancements.utils;

import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModConfig {
    public boolean enabled = false;
    public float radius = 15;
    public boolean actionBarMessage = true;
    public Set<String> whitelist = new HashSet<>(List.of(World.OVERWORLD.getValue().toString()));
}
