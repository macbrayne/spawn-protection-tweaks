package de.macbrayne.fabric.spawnprotectiontweaks.server;

import de.macbrayne.fabric.spawnprotectiontweaks.Reference;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PlayerEvents {
    @SuppressWarnings("unused")
    public static ActionResult onAttack(PlayerEntity playerEntity, World world, Hand hand, BlockPos blockPos, Direction direction) {
        return common(playerEntity, world, blockPos, PlayerPermissions.ATTACK_BLOCK);
    }

    @SuppressWarnings("unused")
    public static ActionResult onAttackEntity(PlayerEntity playerEntity, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        return common(playerEntity, world, entity.getBlockPos(), PlayerPermissions.ATTACK_ENTITY);
    }

    @SuppressWarnings("unused")
    public static ActionResult onUse(PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult) {
        return common(playerEntity, world, blockHitResult.getBlockPos(), PlayerPermissions.USE_BLOCK);
    }

    @SuppressWarnings("unused")
    public static ActionResult onUseEntity(PlayerEntity playerEntity, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        return common(playerEntity, world, entity.getBlockPos(), PlayerPermissions.USE_ENTITY);
    }

    private static ActionResult common(PlayerEntity playerEntity, World world, BlockPos target, PlayerPermissions intent) {
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) playerEntity;
        ServerWorld serverWorld = (ServerWorld) world;
        if(Permissions.check(playerEntity, intent.get(world))) {
            return ActionResult.PASS;
        }
        Optional<Boolean> protectionStatus = SpawnProtection.isProtected(serverWorld, target, serverPlayer);
        if(protectionStatus.isPresent() && protectionStatus.get()) {
            if(Reference.getConfig().getOrDefault(serverWorld).actionBar) {
                serverPlayer.sendMessage(new TranslatableText(intent.getTranslationKey()), true);
            }
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

    // region Permissions
    private enum PlayerPermissions {
        ATTACK_BLOCK(".attack.block"), ATTACK_ENTITY(".attack.entity"),
        USE_BLOCK(".use.block"), USE_ENTITY(".use.entity");

        private static final String MODULE = Reference.MOD_ID + ".interaction";
        private final String value;
        private final String translationKey;

        PlayerPermissions(final String value) {
            this.value = value;
            this.translationKey = "actionbar." + MODULE + value;
        }

        public String get(final World world) {
            return MODULE + getWorldId(world) + value;
        }

        public String getTranslationKey() {
            return translationKey;
        }

        private String getWorldId(final World world) {
            return world.getRegistryKey().getValue().toString();
        }
    }
    // endregion
}
