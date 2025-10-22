package eatawesome.inventoryexchange.utils;

import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeAttachmentTypes;
import eatawesome.inventoryexchange.utils.text.InventoryExchangeLang;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class RepairToggle {
	private RepairToggle() {}

	public static boolean isEnabled(Player player) {
		return player.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).repairEnabled;
	}

	public static void setValue(ServerPlayer serverPlayer, boolean value) {
		serverPlayer.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).repairEnabled = value;
		serverPlayer.sendSystemMessage(value
			? InventoryExchangeLang.REPAIR_ENABLED.translate().withStyle(ChatFormatting.GREEN)
			: InventoryExchangeLang.REPAIR_DISABLED.translate().withStyle(ChatFormatting.RED)
		);
	}

	public static void toggleValue(ServerPlayer serverPlayer) {
		setValue(serverPlayer, !isEnabled(serverPlayer));
	}
}