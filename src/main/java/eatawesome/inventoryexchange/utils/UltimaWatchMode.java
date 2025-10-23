package eatawesome.inventoryexchange.utils;

import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeAttachmentTypes;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class UltimaWatchMode {
	private UltimaWatchMode() {}

	public static int getMode(Player player) {
		return player.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).ultimaWatchMode;
	}
	
	public static MutableComponent getModeName(Player player) {
		int value = getMode(player);
		if (value == 1) {
			return PELang.TIME_WATCH_FAST_FORWARD.translate();
		} else if (value == 2) {
			return PELang.TIME_WATCH_REWIND.translate();
		}
		return PELang.TIME_WATCH_OFF.translate();
	}
	
	public static MutableComponent getModeName(int value) {
		if (value == 1) {
			return PELang.TIME_WATCH_FAST_FORWARD.translate();
		} else if (value == 2) {
			return PELang.TIME_WATCH_REWIND.translate();
		}
		return PELang.TIME_WATCH_OFF.translate();
	}

	public static void setValue(ServerPlayer serverPlayer, int value) {
		if (value >= 3) {
			value = 0;
		}
		serverPlayer.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).ultimaWatchMode = value;
		serverPlayer.sendSystemMessage(PELang.TIME_WATCH_MODE_SWITCH.translate(getModeName(value)));
	}
	
	public static void nextValue(ServerPlayer serverPlayer) {
		setValue(serverPlayer, getMode(serverPlayer) + 1);
	}
}