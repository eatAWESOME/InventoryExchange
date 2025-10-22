package eatawesome.inventoryexchange.utils;

import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeAttachmentTypes;
import eatawesome.inventoryexchange.utils.text.InventoryExchangeLang;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class UltimaToolMode {
	private UltimaToolMode() {}

	public static int getMode(Player player) {
		return player.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).ultimaToolMode;
	}

	public static void setValue(ServerPlayer serverPlayer, int value) {
		if (value >= 4) {
			value = 0;
		}
		serverPlayer.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).ultimaToolMode = value;
		if (value == 0) {
			serverPlayer.sendSystemMessage(InventoryExchangeLang.ULTIMA_TOOL_SINGLE.translate());
		} else if (value == 1) {
			serverPlayer.sendSystemMessage(InventoryExchangeLang.ULTIMA_TOOL_WIDE.translate());
		} else if (value == 2) {
			serverPlayer.sendSystemMessage(InventoryExchangeLang.ULTIMA_TOOL_TALL.translate());
		} else if (value == 3) {
			serverPlayer.sendSystemMessage(InventoryExchangeLang.ULTIMA_TOOL_DEEP.translate());
		}
	}
	
	public static void nextValue(ServerPlayer serverPlayer) {
		setValue(serverPlayer, getMode(serverPlayer) + 1);
	}
}