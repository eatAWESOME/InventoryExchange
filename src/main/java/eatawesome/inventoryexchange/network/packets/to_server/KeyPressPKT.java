package eatawesome.inventoryexchange.network.packets.to_server;

import io.netty.buffer.ByteBuf;
import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.container.InfiniteContainerMenu;
import eatawesome.inventoryexchange.gameObjs.container.PortableCraftingMenu;
import eatawesome.inventoryexchange.gameObjs.container.PortableFurnaceMenu;
import eatawesome.inventoryexchange.gameObjs.container.TransmutationContainerMenu;
import eatawesome.inventoryexchange.network.packets.IInventoryExchangePacket;
import eatawesome.inventoryexchange.utils.CondensingToggle;
import eatawesome.inventoryexchange.utils.FeedingToggle;
import eatawesome.inventoryexchange.utils.InfiniteContainerHelper;
import eatawesome.inventoryexchange.utils.InventoryExchangeKeybind;
import eatawesome.inventoryexchange.utils.PlayerHelper;
import eatawesome.inventoryexchange.utils.RepairingToggle;
import eatawesome.inventoryexchange.utils.SmeltingToggle;
import eatawesome.inventoryexchange.utils.UltimaToolMode;
import eatawesome.inventoryexchange.utils.text.InventoryExchangeLang;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
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
		if (context.player() instanceof ServerPlayer serverPlayer && !context.player().isSpectator()) {
			if (key == InventoryExchangeKeybind.CRAFTING) {
				serverPlayer.openMenu(new net.minecraft.world.MenuProvider() {
		            @Override
		            public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		                return new PortableCraftingMenu(windowId, inv);
		            }
		            
		            @Override
		            public net.minecraft.network.chat.Component getDisplayName() {
		                return net.minecraft.network.chat.Component.translatable("container.crafting");
		            }
		        });
			    return;
			} else if (key == InventoryExchangeKeybind.CONDENSE_TOGGLE) {
				CondensingToggle.toggleValue(serverPlayer);
				return;
			} else if (key == InventoryExchangeKeybind.FEED_TOGGLE) {
				FeedingToggle.toggleValue(serverPlayer);
				return;
			} else if (key == InventoryExchangeKeybind.FURNACE) {
				serverPlayer.openMenu(new MenuProvider() {
					@Override
					public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
						return new PortableFurnaceMenu(windowId, inv);
					}
					@Override
					public Component getDisplayName() { return Component.translatable("container.furnace"); }
				});
				return;
			} else if (key == InventoryExchangeKeybind.INFINITE_CONTAINER) {
				serverPlayer.openMenu(new MenuProvider() {
                    @Override
                    public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
                        IItemHandlerModifiable infiniteInventory = InfiniteContainerHelper.getInventory(player);
                        return new InfiniteContainerMenu(windowId, inv, infiniteInventory);
                    }

                    @Override
                    public Component getDisplayName() {
                        return InventoryExchangeLang.INFINITE_CONTAINER_CONTAINER.translate();
                    }
                }, buf -> {});
			    return;
			} else if (key == InventoryExchangeKeybind.PICK_ITEM) {
				PlayerHelper.pickBlockWithEMC(serverPlayer);
				return;
			} else if (key == InventoryExchangeKeybind.REPAIR_TOGGLE) {
				RepairingToggle.toggleValue(serverPlayer);
				return;
			} else if (key == InventoryExchangeKeybind.SMELT_TOGGLE) {
				SmeltingToggle.toggleValue(serverPlayer);
				return;
			} else if (key == InventoryExchangeKeybind.TRANSMUTATION) {
				serverPlayer.openMenu(new MenuProvider() {
					@Override
					public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
						return new TransmutationContainerMenu(windowId, inv);
					}

					@Override
					public Component getDisplayName() {
						return InventoryExchangeLang.TRANSMUTATION_CONTAINER.translate();
					}
				}, buf -> buf.writeBoolean(false));
			    return;
			} else if (key == InventoryExchangeKeybind.ULTIMA_TOOL_MODE) {
				UltimaToolMode.nextValue(serverPlayer);
				return;
			}
		}
	}
}