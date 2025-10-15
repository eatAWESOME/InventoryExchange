package eatawesome.inventoryexchange.network.packets.to_server;

import io.netty.buffer.ByteBuf;
import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.container.InfiniteContainer;
import eatawesome.inventoryexchange.gameObjs.container.PortableCraftingMenu;
import eatawesome.inventoryexchange.gameObjs.container.PortableFurnaceMenu;
import eatawesome.inventoryexchange.network.packets.IInventoryExchangePacket;
import eatawesome.inventoryexchange.utils.FeedToggle;
import eatawesome.inventoryexchange.utils.InfiniteContainerHelper;
import eatawesome.inventoryexchange.utils.InventoryExchangeKeybind;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
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
		if (key == InventoryExchangeKeybind.INFINITE_CONTAINER) {
			if (!player.level().isClientSide) {
				player.openMenu(new MenuProvider() {
                    @Override
                    public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
                        IItemHandlerModifiable infiniteInventory = InfiniteContainerHelper.getInventory(player);
                        return new InfiniteContainer(windowId, inv, infiniteInventory);
                    }

                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("container.inventoryexchange.infinite_container");
                    }
                }, buf -> {});
			}
		    return;
		} else if (key == InventoryExchangeKeybind.TRANSMUTATION) {
			if (!player.level().isClientSide) {
				player.openMenu(new MenuProvider() {
					@Override
					public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
						return new TransmutationContainer(windowId, inv);
					}

					@Override
					public Component getDisplayName() {
						return Component.translatable("container.inventoryexchange.transmutation");
					}
				}, buf -> buf.writeBoolean(false));
			}
		    return;
		} else if (key == InventoryExchangeKeybind.CRAFTING) {
			if (!player.level().isClientSide) {
				player.openMenu(new net.minecraft.world.MenuProvider() {
		            @Override
		            public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int windowId, Inventory inv, Player p) {
		                return new PortableCraftingMenu(windowId, inv);
		            }
		            
		            @Override
		            public net.minecraft.network.chat.Component getDisplayName() {
		                return net.minecraft.network.chat.Component.translatable("container.crafting");
		            }
		        });
			}
		    return;
		} else if (key == InventoryExchangeKeybind.FURNACE) {
			if (!player.level().isClientSide) {
				player.openMenu(new MenuProvider() {
					@Override
					public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player p) {
						return new PortableFurnaceMenu(windowId, inv);
					}
					@Override
					public Component getDisplayName() { return Component.translatable("container.furnace"); }
				});
			}
			return;
		} else if (key == InventoryExchangeKeybind.FEED_TOGGLE) {
			if (player instanceof ServerPlayer serverPlayer) {
				FeedToggle.toggleValue(serverPlayer);
			}
			return;
		}
	}
}