package eatawesome.inventoryexchange.utils;

import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeAttachmentTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class FeedToggle {
	private FeedToggle() {}

	public static boolean isEnabled(Player player) {
		return player.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).feedEnabled;
	}

	public static void setValue(ServerPlayer serverPlayer, boolean value) {
		serverPlayer.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).feedEnabled = value;
		serverPlayer.sendSystemMessage(value
				? Component.translatable("message.inventoryexchange.feeding_enabled").withStyle(ChatFormatting.GREEN)
				: Component.translatable("message.inventoryexchange.feeding_disabled").withStyle(ChatFormatting.RED)
			);
	}

	public static void toggleValue(ServerPlayer serverPlayer) {
		boolean nextValue = !isEnabled(serverPlayer);
		setValue(serverPlayer, nextValue);
	}
}