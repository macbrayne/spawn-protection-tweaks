package de.macbrayne.fabric.spawnprotectiontweaks.events;

import de.macbrayne.fabric.spawnprotectiontweaks.Reference;
import de.macbrayne.fabric.spawnprotectiontweaks.server.SpawnProtection;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class PlayerEvents {
    @SuppressWarnings("unused")
    public static ActionResult onAttackBlock(PlayerEntity playerEntity, World world, Hand hand, BlockPos blockPos, Direction direction) {
        return getAllowedAtAsActionResult(playerEntity, world, blockPos, PlayerPermissions.ATTACK_BLOCK);
    }

    @SuppressWarnings("unused")
    public static ActionResult onAttackEntity(PlayerEntity playerEntity, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        return getAllowedAtAsActionResult(playerEntity, world, entity.getBlockPos(), PlayerPermissions.ATTACK_ENTITY);
    }

    @SuppressWarnings("unused")
    public static ActionResult onUseBlock(PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult) {
        return getAllowedAtAsActionResult(playerEntity, world, blockHitResult.getBlockPos(), PlayerPermissions.USE_BLOCK);
    }

    @SuppressWarnings("unused")
    public static ActionResult onUseEntity(PlayerEntity playerEntity, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        return getAllowedAtAsActionResult(playerEntity, world, entity.getBlockPos(), PlayerPermissions.USE_ENTITY);
    }

    @SuppressWarnings("unused")
    public static boolean beforeBreakBlock(World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
        return isAllowedAt(playerEntity, world, blockPos, PlayerPermissions.BREAK_BLOCK);
    }

    public static TypedActionResult<ItemStack> onUseItem(PlayerEntity playerEntity, World world, Hand hand) {
        return isAllowedAt(playerEntity, world, playerEntity.getBlockPos(), PlayerPermissions.USE_ITEM) ?
                TypedActionResult.pass(playerEntity.getStackInHand(hand)) :
                TypedActionResult.fail(playerEntity.getStackInHand(hand));
    }

    private static boolean isAllowedAt(PlayerEntity playerEntity, World world, BlockPos target, PlayerPermissions intent) {
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) playerEntity;
        ServerWorld serverWorld = (ServerWorld) world;
        if(!Reference.getConfig().enabled || !intent.isEnabled(serverWorld) || Permissions.check(playerEntity, intent.get(world))) {
            return true;
        }
        Optional<Boolean> protectionStatus = SpawnProtection.isProtected(serverWorld, target, serverPlayer);
        if(protectionStatus.isPresent() && protectionStatus.get()) {
            if(Reference.getConfig().getOrDefault(serverWorld).actionBar) {
                serverPlayer.sendMessage(new TranslatableText(intent.getTranslationKey()), true);
            }
            return false;
        }
        return true;
    }

    private static ActionResult getAllowedAtAsActionResult(PlayerEntity playerEntity, World world, BlockPos target, PlayerPermissions intent) {
        return isAllowedAt(playerEntity, world, target, intent) ? ActionResult.PASS : ActionResult.FAIL;
    }

    // region Permissions
    private enum PlayerPermissions {
        ATTACK_BLOCK(".attack.block", (world) -> Reference.getConfig().getDimension(world).advancedEventConfig.preventAttackingBlocks),
        ATTACK_ENTITY(".attack.entity", (world) -> Reference.getConfig().getDimension(world).advancedEventConfig.preventAttackingEntities),
        USE_BLOCK(".use.block", (world) -> Reference.getConfig().getDimension(world).advancedEventConfig.preventUsingBlocks),
        USE_ENTITY(".use.entity", (world) -> Reference.getConfig().getDimension(world).advancedEventConfig.preventUsingEntities),
        USE_ITEM(".use.item", (world) -> Reference.getConfig().getDimension(world).advancedEventConfig.preventUsingItems),
        BREAK_BLOCK(".break.block", (world) -> Reference.getConfig().getDimension(world).advancedEventConfig.preventBreakingBlocks);

        private static final String MODULE = Reference.MOD_ID + ".interaction";
        private final Function<ServerWorld, Boolean> configSupplier;
        private final String value;
        private final String translationKey;

        PlayerPermissions(final String value, final Function<ServerWorld, Boolean> configSupplier) {
            this.value = value;
            this.configSupplier = configSupplier;
            this.translationKey = "actionbar." + MODULE + value;
        }

        public String get(final World world) {
            return MODULE + "." + getWorldId(world) + "." + value;
        }

        public String getTranslationKey() {
            return translationKey;
        }

        public boolean isEnabled(ServerWorld world) {
            return configSupplier.apply(world);
        }

        private String getWorldId(final World world) {
            return world.getRegistryKey().getValue().toString();
        }
    }
    // endregion
}
