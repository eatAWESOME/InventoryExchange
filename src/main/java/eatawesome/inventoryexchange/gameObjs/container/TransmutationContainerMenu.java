package eatawesome.inventoryexchange.gameObjs.container;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import eatawesome.inventoryexchange.api.proxy.IEMCProxy;
import eatawesome.inventoryexchange.gameObjs.container.inventory.TransmutationInventory;
import eatawesome.inventoryexchange.gameObjs.container.slots.HotBarSlot;
import eatawesome.inventoryexchange.gameObjs.container.slots.IInventoryContainerSlot;
import eatawesome.inventoryexchange.gameObjs.container.slots.MainInventorySlot;
import eatawesome.inventoryexchange.gameObjs.container.slots.transmutation.SlotConsume;
import eatawesome.inventoryexchange.gameObjs.container.slots.transmutation.SlotOutput;
import eatawesome.inventoryexchange.gameObjs.items.TomeOfKnowledge;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeContainerTypes;
import eatawesome.inventoryexchange.network.packets.IInventoryExchangePacket;
import eatawesome.inventoryexchange.network.packets.to_client.container.SyncOffhandPkt;
import eatawesome.inventoryexchange.network.packets.to_client.container.UpdateWindowLongPKT;
import eatawesome.inventoryexchange.network.packets.to_server.SearchUpdatePKT;
import eatawesome.inventoryexchange.utils.ItemHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TransmutationContainerMenu extends AbstractContainerMenu {

	private final List<IInventoryContainerSlot> inventoryContainerSlots = new ArrayList<>();
	private final List<MainInventorySlot> mainInventorySlots = new ArrayList<>();
	private final List<HotBarSlot> hotBarSlots = new ArrayList<>();
	private final List<BoxedLong> longFields = new ArrayList<>();
	private final Inventory playerInv;

	public final TransmutationInventory transmutationInventory;
	public final InteractionHand hand;
	private final int selected;
	protected final ItemStack stack;
	private ItemStack remoteOffHand = ItemStack.EMPTY;

	public static TransmutationContainerMenu fromNetwork(int windowId, Inventory playerInv, FriendlyByteBuf buf) {
		if (buf.readBoolean()) {
			return new TransmutationContainerMenu(windowId, playerInv, buf.readEnum(InteractionHand.class), buf.readByte());
		}
		return new TransmutationContainerMenu(windowId, playerInv);
	}

	public TransmutationContainerMenu(int windowId, Inventory playerInv) {
		super(InventoryExchangeContainerTypes.TRANSMUTATION_CONTAINER.get(), windowId);
		this.playerInv = playerInv;
		this.hand = null;
		this.selected = 0;
		this.stack = getStack();
		this.transmutationInventory = new TransmutationInventory(this.playerInv.player);
		initSlots();
	}

	public TransmutationContainerMenu(int windowId, Inventory playerInv, InteractionHand hand, int selected) {
		super(InventoryExchangeContainerTypes.TRANSMUTATION_CONTAINER.get(), windowId);
		this.playerInv = playerInv;
		this.hand = hand;
		this.selected = selected;
		this.stack = getStack();
		this.transmutationInventory = new TransmutationInventory(this.playerInv.player);
		initSlots();
	}

	private void initSlots() {
		// Transmutation Inventory
		//Consume
		this.addSlot(new SlotConsume(transmutationInventory, 9, 107, 97));
		
		// Left Outputs (Minerals)
		this.addSlot(new SlotOutput(transmutationInventory, 11, 34, 41)); // Center NW
		this.addSlot(new SlotOutput(transmutationInventory, 12, 52, 41)); // Center NE
		this.addSlot(new SlotOutput(transmutationInventory, 13, 52, 59)); // Center SE
		this.addSlot(new SlotOutput(transmutationInventory, 14, 34, 59)); // Center SW
		this.addSlot(new SlotOutput(transmutationInventory, 15, 43, 23)); // N
		this.addSlot(new SlotOutput(transmutationInventory, 16, 65, 30)); // NE
		this.addSlot(new SlotOutput(transmutationInventory, 17, 70, 50)); // E
		this.addSlot(new SlotOutput(transmutationInventory, 18, 65, 70)); // SE
		this.addSlot(new SlotOutput(transmutationInventory, 19, 43, 77)); // S
		this.addSlot(new SlotOutput(transmutationInventory, 20, 21, 70)); // SW
		this.addSlot(new SlotOutput(transmutationInventory, 21, 16, 50)); // W
		this.addSlot(new SlotOutput(transmutationInventory, 22, 21, 30)); // NW
		
		// Right Outputs (Matter)
		this.addSlot(new SlotOutput(transmutationInventory, 23, 158, 50)); // C
		this.addSlot(new SlotOutput(transmutationInventory, 24, 158, 31)); // Center N
		this.addSlot(new SlotOutput(transmutationInventory, 25, 177, 50)); // Center E
		this.addSlot(new SlotOutput(transmutationInventory, 26, 158, 69)); // Center S
		this.addSlot(new SlotOutput(transmutationInventory, 27, 139, 50)); // Center W
		this.addSlot(new SlotOutput(transmutationInventory, 28, 158, 9)); // N
		this.addSlot(new SlotOutput(transmutationInventory, 29, 176, 13)); // NNE
		this.addSlot(new SlotOutput(transmutationInventory, 30, 193, 30)); // ENE
		this.addSlot(new SlotOutput(transmutationInventory, 31, 199, 50)); // E
		this.addSlot(new SlotOutput(transmutationInventory, 32, 193, 70)); // ESE
		this.addSlot(new SlotOutput(transmutationInventory, 33, 176, 87)); // SSE
		this.addSlot(new SlotOutput(transmutationInventory, 34, 158, 91)); // S
		this.addSlot(new SlotOutput(transmutationInventory, 35, 140, 87)); // SSW
		this.addSlot(new SlotOutput(transmutationInventory, 36, 123, 70)); // WSW
		this.addSlot(new SlotOutput(transmutationInventory, 37, 116, 50)); // W
		this.addSlot(new SlotOutput(transmutationInventory, 38, 123, 30)); // WNW
		this.addSlot(new SlotOutput(transmutationInventory, 39, 140, 13)); // NNW
		addPlayerInventory(35, 117);
	}

	@Override
	protected @NotNull Slot addSlot(@NotNull Slot slot) {
		super.addSlot(slot);
		if (slot instanceof IInventoryContainerSlot containerSlot) {
			inventoryContainerSlots.add(containerSlot);
		} else if (slot instanceof MainInventorySlot inventorySlot) {
			mainInventorySlots.add(inventorySlot);
		} else if (slot instanceof HotBarSlot hotBarSlot) {
			hotBarSlots.add(hotBarSlot);
		}
		return slot;
	}

	private void addPlayerInventory(int xStart, int yStart) {
		int s = 18;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlot(createMainInventorySlot(playerInv, j + i * 9 + 9, xStart + j * s, yStart + i * s));
			}
		}
		yStart = yStart + s * 3 + 4;
		for (int i = 0; i < Inventory.getSelectionSize(); i++) {
			addSlot(createHotBarSlot(playerInv, i, xStart + i * s, yStart));
		}
	}

	protected MainInventorySlot createMainInventorySlot(@NotNull Inventory inv, int index, int x, int y) {
		return new MainInventorySlot(inv, index, x, y);
	}

	protected HotBarSlot createHotBarSlot(@NotNull Inventory inv, int index, int x, int y) {
		if (hand == InteractionHand.MAIN_HAND && index == selected) {
			return new HotBarSlot(inv, index, x, y) {
				@Override
				public boolean mayPickup(@NotNull Player player) {
					return false;
				}
			};
		}
		return new HotBarSlot(inv, index, x, y);
	}

	@Nullable
	public Slot tryGetSlot(int slotId) {
		if (slotId >= 0 && slotId < slots.size()) {
			return getSlot(slotId);
		}
		return null;
	}

	protected ItemStack getStack() {
		return getStack(this.playerInv, this.hand, this.selected);
	}

	public static ItemStack getStack(Inventory playerInv, InteractionHand hand, int selected) {
		if (hand == null) {
			return ItemStack.EMPTY;
		}
		return hand == InteractionHand.OFF_HAND ? playerInv.player.getOffhandItem() : playerInv.getItem(selected);
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return this.hand == null || !this.stack.isEmpty() && player.getItemInHand(this.hand).is(this.stack.getItem());
	}

	@Override
	public void clicked(int slotId, int dragType, @NotNull ClickType clickType, @NotNull Player player) {
		if (clickType == ClickType.SWAP) {
			if (hand == InteractionHand.OFF_HAND && dragType == 40) {
				return;
			} else if (hand == InteractionHand.MAIN_HAND && dragType >= 0 && dragType < Inventory.getSelectionSize()) {
				if (!hotBarSlots.get(dragType).mayPickup(player)) {
					return;
				}
			}
		}
		clickPostValidate(slotId, dragType, clickType, player);
	}

	public void clickPostValidate(int slotIndex, int dragType, @NotNull ClickType clickType, @NotNull Player player) {
		Slot slot = tryGetSlot(slotIndex);
		if (player.level().isClientSide && slot != null) {
			int combinedIndex = slot.getSlotIndex();
			if (transmutationInventory.getHandlerForSlot(combinedIndex) == transmutationInventory.outputs) {
				int outputsLocalIndex = transmutationInventory.getIndexFromSlot(combinedIndex);
				PacketDistributor.sendToServer(new SearchUpdatePKT(outputsLocalIndex, slot.getItem()));
			}
		}
		super.clicked(slotIndex, dragType, clickType, player);
	}

	@NotNull
	@Override
	public ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
		Slot currentSlot = tryGetSlot(slotIndex);
		if (currentSlot == null || !currentSlot.hasItem()) {
			return ItemStack.EMPTY;
		}
		if (slotIndex >= 1 && slotIndex <= 29) {
			ItemStack stack = currentSlot.getItem().copy();
			long itemEmc = IEMCProxy.INSTANCE.getValue(stack);
			if (itemEmc > 0) {
				stack.setCount(stack.getMaxStackSize());
				int itemsRoomFor = stack.getCount() - ItemHelper.simulateFit(player.getInventory().items, stack);
				if (itemsRoomFor == 1) {
					long availableEMC = transmutationInventory.getAvailableEmcAsLong();
					if (itemEmc > availableEMC) {
						return ItemStack.EMPTY;
					}
					if (transmutationInventory.isServer()) {
						transmutationInventory.removeEmc(BigInteger.valueOf(itemEmc));
					}
					stack.setCount(1);
					ItemHandlerHelper.insertItemStacked(player.getCapability(ItemHandler.ENTITY), stack, false);
					if (player instanceof ServerPlayer serverPlayer && stack.getCount() > 0) {
						serverPlayer.awardStat(Stats.ITEM_CRAFTED.get(stack.getItem()), stack.getCount());
					}
				} else if (itemsRoomFor > 1) {
					BigInteger availableEMC = transmutationInventory.getAvailableEmc();
					BigInteger emc = BigInteger.valueOf(itemEmc);
					BigInteger totalEmc = emc.multiply(BigInteger.valueOf(itemsRoomFor));
					if (totalEmc.compareTo(availableEMC) > 0) {
						BigInteger numOperations = availableEMC.divide(emc);
						itemsRoomFor = numOperations.intValue();
						totalEmc = emc.multiply(numOperations);
						if (itemsRoomFor <= 0) {
							return ItemStack.EMPTY;
						}
					}
					if (transmutationInventory.isServer()) {
						transmutationInventory.removeEmc(totalEmc);
					}
					stack.setCount(itemsRoomFor);
					ItemHandlerHelper.insertItemStacked(player.getCapability(ItemHandler.ENTITY), stack, false);
					if (player instanceof ServerPlayer serverPlayer && stack.getCount() > 0) {
						serverPlayer.awardStat(Stats.ITEM_CRAFTED.get(stack.getItem()), stack.getCount());
					}
				}
			}
		} else if (slotIndex >= 30) {
			ItemStack slotStack = currentSlot.getItem();
			ItemStack stackToInsert = slotStack;
			long emc = IEMCProxy.INSTANCE.getSellValue(stackToInsert);
			if (emc > 0 || stackToInsert.getItem() instanceof TomeOfKnowledge) {
				if (transmutationInventory.isServer()) {
					BigInteger emcBigInt = BigInteger.valueOf(emc);
					transmutationInventory.handleKnowledge(stackToInsert);
					transmutationInventory.addEmc(emcBigInt.multiply(BigInteger.valueOf(stackToInsert.getCount())));
				}
				currentSlot.set(ItemStack.EMPTY);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canDragTo(@NotNull Slot slot) {
		return !(slot instanceof SlotConsume || slot instanceof SlotOutput);
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		broadcastPE(false);
	}

	@Override
	public void sendAllDataToRemote() {
		super.sendAllDataToRemote();
		broadcastPE(true);
	}

	protected void broadcastPE(boolean all) {
		for (int i = 0; i < longFields.size(); i++) {
			BoxedLong boxedLong = longFields.get(i);
			if (boxedLong.isDirty() || all) {
				syncDataChange(new UpdateWindowLongPKT((short) containerId, (short) i, boxedLong.get()));
			}
		}
		if (hand == InteractionHand.OFF_HAND) {
			ItemStack offhandStack = getStack();
			if (!ItemStack.matches(offhandStack, remoteOffHand)) {
				remoteOffHand = offhandStack.copy();
				syncDataChange(new SyncOffhandPkt((short) containerId, remoteOffHand));
			}
		}
	}

	protected void syncDataChange(IInventoryExchangePacket packet) {
		if (this.playerInv.player instanceof net.minecraft.server.level.ServerPlayer sp) {
			PacketDistributor.sendToPlayer(sp, packet);
		}
	}

	public void updateProgressBarLong(short idx, long data) {
		updateProgressBarLong(Short.toUnsignedInt(idx), data);
	}

	public void updateProgressBarLong(int idx, long data) {
		if (idx < 0) return;
		while (longFields.size() <= idx) {
			longFields.add(new BoxedLong());
		}
		longFields.get(idx).set(data);
	}

	public static class BoxedLong {
		private long inner;
		private boolean dirty = false;
		public long get() { return inner; }
		public void set(long v) { if (v != inner) { inner = v; dirty = true; } }
		public boolean isDirty() { boolean ret = dirty; dirty = false; return ret; }
	}
}