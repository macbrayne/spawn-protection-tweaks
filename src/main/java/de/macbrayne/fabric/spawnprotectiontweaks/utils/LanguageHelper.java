package de.macbrayne.fabric.spawnprotectiontweaks.utils;

import de.macbrayne.fabric.spawnprotectiontweaks.Reference;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;

public class LanguageHelper {
    public static BaseText getOptionalTranslation(final ServerCommandSource source, final String translationKey, final Object... args) {
        return getOptionalTranslation(source.getEntity(), translationKey, args);
    }

    public static BaseText getOptionalTranslation(final Entity source, final String translationKey, final Object... args) {
        if (!acceptsTranslations(source)) {
            return new LiteralText(format(translationKey, args));
        }
        return new TranslatableText(translationKey, args);
    }

    public static String format(final String translationKey, final Object... args) {
        return String.format(Language.getInstance().get(translationKey), args);
    }

    private static boolean acceptsTranslations(@Nullable Entity entity) {
        if (entity instanceof ServerPlayerEntity serverPlayer) {
            return ServerPlayNetworking.canSend(serverPlayer, Reference.LANGUAGE_PACKET_IDENTIFIER);
        }
        return false;
    }
}