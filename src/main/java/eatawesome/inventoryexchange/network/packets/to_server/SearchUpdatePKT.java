package eatawesome.inventoryexchange.network.packets.to_server;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.container.TransmutationContainerMenu;
import eatawesome.inventoryexchange.network.packets.IInventoryExchangePacket;

public record SearchUpdatePKT(int slot, ItemStack itemStack) implements IInventoryExchangePacket {

	public static final CustomPacketPayload.Type<SearchUpdatePKT> TYPE = new CustomPacketPayload.Type<>(InventoryExchange.rl("update_search"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SearchUpdatePKT> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, SearchUpdatePKT::slot,
			ItemStack.OPTIONAL_STREAM_CODEC, SearchUpdatePKT::itemStack,
			SearchUpdatePKT::new
	);

	@NotNull
	@Override
	public CustomPacketPayload.Type<SearchUpdatePKT> type() {
		return TYPE;
	}

	public SearchUpdatePKT {
		itemStack = itemStack.copy();
	}

	@Override
	public void handle(IPayloadContext context) {
		if (context.player().containerMenu instanceof TransmutationContainerMenu container) {
			container.transmutationInventory.writeIntoOutputSlot(slot, itemStack);
		}
	}
}