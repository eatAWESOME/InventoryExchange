package eatawesome.inventoryexchange.utils;

import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeAttachmentTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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
			? Component.translatable("message.inventoryexchange.repair_enabled").withStyle(ChatFormatting.GREEN)
			: Component.translatable("message.inventoryexchange.repair_disabled").withStyle(ChatFormatting.RED)
		);
	}

	public static void toggleValue(ServerPlayer serverPlayer) {
		setValue(serverPlayer, !isEnabled(serverPlayer));
	}
}