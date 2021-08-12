package de.macbrayne.fabric.spawnenhancements.utils;

import net.minecraft.world.World;

import java.util.*;

public class ModConfig {
    public boolean enabled = false;
    public boolean actionBarMessage = true;
    public HashMap<String, DimensionConfig> whitelist = new HashMap<>(Map.of(World.OVERWORLD.getValue().toString(), new DimensionConfig()));

    public static class DimensionConfig {
        public float radius = 15;
    }
}
