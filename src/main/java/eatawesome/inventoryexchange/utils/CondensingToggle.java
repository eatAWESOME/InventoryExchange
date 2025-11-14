package eatawesome.inventoryexchange.utils;

import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeAttachmentTypes;
import eatawesome.inventoryexchange.utils.text.InventoryExchangeLang;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class CondensingToggle {
	private CondensingToggle() {}

	public static boolean isEnabled(Player player) {
		return player.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).condensingEnabled;
	}

	public static void setValue(ServerPlayer serverPlayer, boolean value) {
		serverPlayer.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).condensingEnabled = value;
		serverPlayer.sendSystemMessage(value
				? InventoryExchangeLang.CONDENSING_ENABLED.translate().withStyle(ChatFormatting.GREEN)
				: InventoryExchangeLang.CONDENSING_DISABLED.translate().withStyle(ChatFormatting.RED)
			);
	}

	public static void setComplete(ServerPlayer serverPlayer) {
		serverPlayer.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).condensingEnabled = false;
		serverPlayer.sendSystemMessage(InventoryExchangeLang.CONDENSING_COMPLETED.translate().withStyle(ChatFormatting.RED));
	}

	public static void toggleValue(ServerPlayer serverPlayer) {
		boolean nextValue = !isEnabled(serverPlayer);
		setValue(serverPlayer, nextValue);
	}
	
	public static boolean isLimited(Player player) {
		return player.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).condensingLimited;
	}

	public static void setLimitValue(ServerPlayer serverPlayer, boolean value) {
		serverPlayer.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).condensingLimited = value;
	}
}