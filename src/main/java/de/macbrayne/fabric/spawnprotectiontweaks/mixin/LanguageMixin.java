package de.macbrayne.fabric.spawnprotectiontweaks.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParseException;
import de.macbrayne.fabric.spawnprotectiontweaks.Reference;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Language;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;

/**
 * See https://github.com/lorddusk/HQM/blob/1.16/fabric/src/main/java/hardcorequesting/fabric/mixin/MixinLanguage.java for the original code
 * (commit 46aa7ae6ed6e65697abc5144895ee43ac389015b)
 *
 * Used with permission from shedaniel
 */
@Mixin(Language.class)
public class LanguageMixin {
    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "create",
            at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void loadDefault(CallbackInfoReturnable<Language> cir, ImmutableMap.Builder<String, String> builder, BiConsumer<String, String> biConsumer) {
        final String MOD_ID = Reference.MOD_ID;
        try {
            Path path = FabricLoader.getInstance().getModContainer(MOD_ID).get().getPath("data/" + MOD_ID + "/lang/en_us.json");
            try (InputStream inputStream = Files.newInputStream(path)) {
                Language.load(inputStream, biConsumer);
            }
        } catch (JsonParseException | IOException e) {
            LOGGER.error("Couldn't read strings from /data/" + Reference.MOD_ID + "/lang/en_us.json", e);
        }
    }
}