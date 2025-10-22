package eatawesome.inventoryexchange.utils;

import java.math.BigInteger;
import java.util.List;
import java.util.function.BiPredicate;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeItems;
import moze_intel.projecte.PECore;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.ReadOnlyScoreCriteria;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.BlockEvent.EntityMultiPlaceEvent;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * Helper class for player-related methods. Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class PlayerHelper {

	public final static ObjectiveCriteria SCOREBOARD_EMC = new ReadOnlyScoreCriteria(PECore.PEID + ":emc_score");
	public final static ObjectiveCriteria SCOREBOARD_ABILITY_EMC = new ReadOnlyScoreCriteria(InventoryExchange.MODID + ":ability_emc_score");

	/**
	 * Tries placing a block and fires an event for it.
	 *
	 * @return Whether the block was successfully placed
	 */
	public static boolean checkedPlaceBlock(Player player, Level level, BlockPos pos, BlockState state) {
		return hasEditPermission(player, level, pos) && partiallyCheckedPlaceBlock(player, level, pos, state);
	}

	private static boolean partiallyCheckedPlaceBlock(Player player, Level level, BlockPos pos, BlockState state) {
		SignBlockEntity oldSign = null;
		if (state.getBlock() instanceof SignBlock && level.getBlockEntity(pos) instanceof SignBlockEntity sign) {
			oldSign = sign;
		}
		level.captureBlockSnapshots = true;
		level.setBlockAndUpdate(pos, state);
		level.captureBlockSnapshots = false;

		@SuppressWarnings("unchecked")
		List<BlockSnapshot> blockSnapshots = (List<BlockSnapshot>) level.capturedBlockSnapshots.clone();
		level.capturedBlockSnapshots.clear();

		boolean eventResult = false;
		if (blockSnapshots.size() > 1) {
			eventResult = NeoForge.EVENT_BUS.post(new EntityMultiPlaceEvent(blockSnapshots, Blocks.AIR.defaultBlockState(), player)).isCanceled();
		} else if (blockSnapshots.size() == 1) {
			eventResult = NeoForge.EVENT_BUS.post(new BlockEvent.EntityPlaceEvent(blockSnapshots.getFirst(), Blocks.AIR.defaultBlockState(), player)).isCanceled();
		}

		if (eventResult) {
			level.restoringBlockSnapshots = true;
			for (BlockSnapshot snapshot : blockSnapshots.reversed()) {
				snapshot.restore(snapshot.getFlags() | Block.UPDATE_CLIENTS);
			}
			level.restoringBlockSnapshots = false;
		} else {
			for (BlockSnapshot snap : blockSnapshots) {
				BlockState oldBlock = snap.getState();
				BlockPos snapPos = snap.getPos();
				BlockState newBlock = level.getBlockState(snapPos);
				newBlock.onPlace(level, snapPos, oldBlock, false);
				level.markAndNotifyBlock(snapPos, level.getChunkAt(snapPos), oldBlock, newBlock, snap.getFlags(), Block.UPDATE_LIMIT);
				if (oldSign != null && snapPos.equals(pos) && newBlock.hasBlockEntity()) {
					WorldHelper.copySignData(level, pos, oldSign);
				}
			}
		}
		level.capturedBlockSnapshots.clear();
		return !eventResult;
	}

	public static boolean checkedReplaceBlock(ServerPlayer player, Level level, BlockPos pos, BlockState state) {
		return hasBreakPermission(player, level, pos) && partiallyCheckedPlaceBlock(player, level, pos, state);
	}

	public static ItemStack findFirstItem(Player player, Holder<Item> consumeFrom) {
		for (ItemStack s : player.getInventory().items) {
			if (!s.isEmpty() && s.is(consumeFrom)) {
				return s;
			}
		}
		return ItemStack.EMPTY;
	}

	public static boolean checkHotbar(Player player, BiPredicate<Player, ItemStack> checker) {
		for (int i = 0; i < Inventory.getSelectionSize(); i++) {
			ItemStack stack = player.getInventory().getItem(i);
			if (!stack.isEmpty() && checker.test(player, stack)) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkHotbarCurios(Player player, BiPredicate<Player, ItemStack> checker) {
		if (checkHotbar(player, checker)) {
			return true;
		}
		ItemStack offhand = player.getOffhandItem();
		if (!offhand.isEmpty() && checker.test(player, offhand)) {
			return true;
		}
		IItemHandler curios = player.getCapability(IntegrationHelper.CURIO_ITEM_HANDLER);
		if (curios != null) {
			for (int i = 0, slots = curios.getSlots(); i < slots; i++) {
				ItemStack stack = curios.getStackInSlot(i);
				if (!stack.isEmpty() && checker.test(player, stack)) {
					return true;
				}
			}
		}
		return false;
	}

	public static BlockHitResult getBlockLookingAt(Player player, double maxDistance) {
		return (BlockHitResult) player.pick(maxDistance, 1.0F, false);
	}

	/**
	 * Returns a vec representing where the player is looking, capped at maxDistance away.
	 */
	public static Vec3 getLookTarget(Player player, double maxDistance) {
		Vec3 lookAngle = player.getLookAngle();
		return player.getEyePosition().add(lookAngle.x * maxDistance, lookAngle.y * maxDistance, lookAngle.z * maxDistance);
	}

	public static boolean hasBreakPermission(ServerPlayer player, Level level, BlockPos pos) {
		return hasEditPermission(player, level, pos) && checkBreakPermission(player, level, pos);
	}

	public static boolean checkBreakPermission(ServerPlayer player, Level level, BlockPos pos) {
		return !CommonHooks.fireBlockBreak(level, player.gameMode.getGameModeForPlayer(), player, pos, level.getBlockState(pos)).isCanceled();
	}

	public static boolean hasEditPermission(Player player, Level level, BlockPos pos) {
		if (!player.mayInteract(level, pos)) {
			return false;
		}
		for (Direction e : Constants.DIRECTIONS) {
			if (!player.mayUseItemAt(pos, e, ItemStack.EMPTY)) {
				return false;
			}
		}
		return true;
	}

	public static void updateScore(ServerPlayer player, ObjectiveCriteria objective, BigInteger value) {
		updateScore(player, objective, MathUtils.clampToInt(value));
	}

	public static void updateScore(ServerPlayer player, ObjectiveCriteria objective, int value) {
		player.getScoreboard().forAllObjectives(objective, player, score -> score.set(value));
	}
	
	public static boolean hasFullUltimaSet(Player player) {
		return player.getItemBySlot(EquipmentSlot.HEAD).is(InventoryExchangeItems.ULTIMA_HELMET.get())
			&& player.getItemBySlot(EquipmentSlot.CHEST).is(InventoryExchangeItems.ULTIMA_CHESTPLATE.get())
			&& player.getItemBySlot(EquipmentSlot.LEGS).is(InventoryExchangeItems.ULTIMA_LEGGINGS.get())
			&& player.getItemBySlot(EquipmentSlot.FEET).is(InventoryExchangeItems.ULTIMA_BOOTS.get());
	}
	
	public static void updateUltimaEffects(Player player) {
		updateEffect(player, MobEffects.NIGHT_VISION, 1200, 0);
		updateEffect(player, MobEffects.WATER_BREATHING, 1200, 0);
		updateEffect(player, MobEffects.FIRE_RESISTANCE, 1200, 0);
		updateEffect(player, MobEffects.MOVEMENT_SPEED, 1200, 2);
	}
	
	public static void updateEffect(Player player, Holder<MobEffect> effect, int duration, int amplifier) {
		MobEffectInstance cur = player.getEffect(effect);
		if (cur == null || cur.getDuration() <= 1180 || cur.getAmplifier() != amplifier) {
			player.addEffect(new MobEffectInstance(effect, duration, amplifier, true, false, true));
		}
	}
	
	public static void removeUltimaEffects(Player player) {
		player.removeEffect(MobEffects.NIGHT_VISION);
		player.removeEffect(MobEffects.WATER_BREATHING);
		player.removeEffect(MobEffects.FIRE_RESISTANCE);
		player.removeEffect(MobEffects.MOVEMENT_SPEED);
	}
}