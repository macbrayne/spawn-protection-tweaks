package de.macbrayne.fabric.spawnprotectiontweaks.utils;

import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Language;

public class LanguageHelper {
    public static BaseText getOptionalTranslation(final String translationKey, final Object... args) {
        return new LiteralText(format(translationKey, args));
    }

    public static String format(final String translationKey, final Object... args) {
        return String.format(Language.getInstance().get(translationKey), args);
    }
}