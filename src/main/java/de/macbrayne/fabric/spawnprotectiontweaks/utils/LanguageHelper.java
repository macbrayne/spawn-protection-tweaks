package de.macbrayne.fabric.spawnprotectiontweaks.utils;

import net.minecraft.util.Language;

public class LanguageHelper {
    public static String format(final String translationKey, final Object... args) {
        return String.format(Language.getInstance().get(translationKey), args);
    }
}