package eatawesome.inventoryexchange.utils;

import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeAttachmentTypes;
import eatawesome.inventoryexchange.utils.text.InventoryExchangeLang;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class RepairingToggle {
	private RepairingToggle() {}

	public static boolean isEnabled(Player player) {
		return player.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).repairingEnabled;
	}

	public static void setValue(ServerPlayer serverPlayer, boolean value) {
		serverPlayer.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).repairingEnabled = value;
		serverPlayer.sendSystemMessage(value
			? InventoryExchangeLang.REPAIRING_ENABLED.translate().withStyle(ChatFormatting.GREEN)
			: InventoryExchangeLang.REPAIRING_DISABLED.translate().withStyle(ChatFormatting.RED)
		);
	}

	public static void setComplete(ServerPlayer serverPlayer) {
		serverPlayer.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).repairingEnabled = false;
		serverPlayer.sendSystemMessage(InventoryExchangeLang.REPAIRING_COMPLETED.translate().withStyle(ChatFormatting.RED));
	}

	public static void toggleValue(ServerPlayer serverPlayer) {
		setValue(serverPlayer, !isEnabled(serverPlayer));
	}
	
	public static boolean isLimited(Player player) {
		return player.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).feedingLimited;
	}

	public static void setLimitValue(ServerPlayer serverPlayer, boolean value) {
		serverPlayer.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).feedingLimited = value;
	}
}