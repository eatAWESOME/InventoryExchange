package eatawesome.inventoryexchange.utils;

import java.math.BigInteger;
import java.util.function.IntSupplier;

import eatawesome.inventoryexchange.InventoryExchange;
import moze_intel.projecte.PECore;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.ReadOnlyScoreCriteria;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

/**
 * Helper class for player-related methods. Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class PlayerHelper {

	public final static ObjectiveCriteria SCOREBOARD_EMC = new ReadOnlyScoreCriteria(PECore.PEID + ":emc_score");
	public final static ObjectiveCriteria SCOREBOARD_ABILITY_EMC = new ReadOnlyScoreCriteria(InventoryExchange.MODID + ":ability_emc_score");

	public static void resetCooldown(Player player) {
		player.resetAttackStrengthTicker();
		PECore.packetHandler().resetCooldown((ServerPlayer) player);
	}

	public static void updateScore(ServerPlayer player, ObjectiveCriteria objective, BigInteger value) {
		updateScore(player, objective, MathUtils.clampToInt(value));
	}

	public static void updateScore(ServerPlayer player, ObjectiveCriteria objective, int value) {
		player.getScoreboard().forAllObjectives(objective, player, score -> score.set(value));
	}

	public static boolean checkFeedCooldown(Player player) {
		return false;//player.getFoodData().needsFood() && checkCooldown(player, PEItems.BODY_STONE.get(), () -> 0);
	}

	public static boolean checkHealCooldown(Player player) {
		return false;//player.getHealth() < player.getMaxHealth() && checkCooldown(player, PEItems.SOUL_STONE.get(), () -> 0);
	}

	public static boolean checkCooldown(Player player, Item item, IntSupplier cooldownSupplier) {
		ItemCooldowns cooldowns = player.getCooldowns();
		if (cooldowns.isOnCooldown(item)) {
			return false;
		}
		int cooldown = cooldownSupplier.getAsInt();
		if (cooldown == -1) {
			return false;
		} else if (cooldown > 0) {
			cooldowns.addCooldown(item, cooldown);
		}
		return true;
	}
}