package eatawesome.inventoryexchange.utils;

import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeAttachmentTypes;
import eatawesome.inventoryexchange.utils.text.InventoryExchangeLang;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class UltimaToolMode {
	private UltimaToolMode() {}

	public static int getMode(Player player) {
		return player.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).ultimaToolMode;
	}

	public static MutableComponent getModeName(Player player) {
		int value = getMode(player);
		if (value == 1) {
			return InventoryExchangeLang.ULTIMA_TOOL_WIDE.translate();
		} else if (value == 2) {
			return InventoryExchangeLang.ULTIMA_TOOL_TALL.translate();
		} else if (value == 3) {
			return InventoryExchangeLang.ULTIMA_TOOL_DEEP.translate();
		}
		return InventoryExchangeLang.ULTIMA_TOOL_SINGLE.translate();
	}

	public static MutableComponent getModeName(int value) {
		if (value == 1) {
			return InventoryExchangeLang.ULTIMA_TOOL_WIDE.translate();
		} else if (value == 2) {
			return InventoryExchangeLang.ULTIMA_TOOL_TALL.translate();
		} else if (value == 3) {
			return InventoryExchangeLang.ULTIMA_TOOL_DEEP.translate();
		}
		return InventoryExchangeLang.ULTIMA_TOOL_SINGLE.translate();
	}
	
	public static void setValue(ServerPlayer serverPlayer, int value) {
		if (value >= 4) {
			value = 0;
		}
		serverPlayer.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).ultimaToolMode = value;
		serverPlayer.sendSystemMessage(getModeName(value));
	}
	
	public static void nextValue(ServerPlayer serverPlayer) {
		setValue(serverPlayer, getMode(serverPlayer) + 1);
	}
}