package eatawesome.inventoryexchange.network.packets.to_server;

import io.netty.buffer.ByteBuf;
import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.network.packets.IInventoryExchangePacket;
import eatawesome.inventoryexchange.utils.InventoryExchangeKeybind;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record KeyPressPKT(InventoryExchangeKeybind key) implements IInventoryExchangePacket {

	public static final CustomPacketPayload.Type<KeyPressPKT> TYPE = new CustomPacketPayload.Type<>(InventoryExchange.rl("key_press"));
	public static final StreamCodec<ByteBuf, KeyPressPKT> STREAM_CODEC = InventoryExchangeKeybind.STREAM_CODEC.map(KeyPressPKT::new, KeyPressPKT::key);

	@NotNull
	@Override
	public CustomPacketPayload.Type<KeyPressPKT> type() {
		return TYPE;
	}

	@Override
	public void handle(IPayloadContext context) {
		Player player = context.player();
		if (player.isSpectator()) {
			return;
		}
		if (key == InventoryExchangeKeybind.TRANSMUTATION) {
			if (!player.level().isClientSide) {
				player.openMenu(new MenuProvider() {
					@Override
					public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
						return new TransmutationContainer(windowId, inv);
					}

					@Override
					public Component getDisplayName() {
						return Component.literal("Transmutation");
					}
				}, buf -> buf.writeBoolean(false)); // note: false = no hand context
			}
		    return;
		}
	}
}