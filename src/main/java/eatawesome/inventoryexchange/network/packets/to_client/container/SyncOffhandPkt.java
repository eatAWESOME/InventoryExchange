package eatawesome.inventoryexchange.network.packets.to_client.container;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.container.TransmutationContainerMenu;
import eatawesome.inventoryexchange.network.packets.IInventoryExchangePacket;

public record SyncOffhandPkt(short windowId, ItemStack stack) implements IInventoryExchangePacket {

	public static final Type<SyncOffhandPkt> TYPE = new Type<>(InventoryExchange.rl("sync_offhand"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncOffhandPkt> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.SHORT, SyncOffhandPkt::windowId,
			ItemStack.OPTIONAL_STREAM_CODEC, SyncOffhandPkt::stack,
			SyncOffhandPkt::new
	);

	@NotNull
	@Override
	public CustomPacketPayload.Type<SyncOffhandPkt> type() {
		return TYPE;
	}

	@Override
	public void handle(IPayloadContext context) {
		Player player = context.player();
		if (player.containerMenu instanceof TransmutationContainerMenu container && container.containerId == windowId) {
			player.setItemInHand(InteractionHand.OFF_HAND, stack);
		}
	}
}