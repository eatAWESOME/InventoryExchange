package moze_intel.projecte.gameObjs.container;

import java.math.BigInteger;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotConsume;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotOutput;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import moze_intel.projecte.network.packets.to_server.SearchUpdatePKT;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import eatawesome.inventoryexchange.gameObjs.items.Tome;

public class TransmutationContainer extends PEHandContainer {

	public final TransmutationInventory transmutationInventory;

	public static TransmutationContainer fromNetwork(int windowId, Inventory playerInv, FriendlyByteBuf buf) {
		if (buf.readBoolean()) {
			return new TransmutationContainer(windowId, playerInv, buf.readEnum(InteractionHand.class), buf.readByte());
		}
		return new TransmutationContainer(windowId, playerInv);
	}

	public TransmutationContainer(int windowId, Inventory playerInv) {
		super(PEContainerTypes.TRANSMUTATION_CONTAINER, windowId, playerInv, null, 0);
		//Hand is technically null safe
		this.transmutationInventory = new TransmutationInventory(this.playerInv.player);
		initSlots();
	}

	public TransmutationContainer(int windowId, Inventory playerInv, InteractionHand hand, int selected) {
		super(PEContainerTypes.TRANSMUTATION_CONTAINER, windowId, playerInv, hand, selected);
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

	@NotNull
	@Override
	protected Slot addSlot(@NotNull Slot slot) {
		return super.addSlot(slot);
	}

	@Override
	public void removed(@NotNull Player player) {
		super.removed(player);
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
				}
			}
		} else if (slotIndex >= 30) {
			ItemStack slotStack = currentSlot.getItem();
			ItemStack stackToInsert = slotStack;
			long emc = IEMCProxy.INSTANCE.getSellValue(stackToInsert);
			if (emc > 0 || stackToInsert.getItem() instanceof Tome) {
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
	public void clickPostValidate(int slotIndex, int dragType, @NotNull ClickType clickType, @NotNull Player player) {
		Slot slot = tryGetSlot(slotIndex);
		if (player.level().isClientSide && slot != null) {
			int combinedIndex = slot.getSlotIndex(); // global index within CombinedInvWrapper
			if (transmutationInventory.getHandlerForSlot(combinedIndex) == transmutationInventory.outputs) {
				int outputsLocalIndex = transmutationInventory.getIndexFromSlot(combinedIndex);
				PacketDistributor.sendToServer(new SearchUpdatePKT(outputsLocalIndex, slot.getItem()));
			}
		}
		super.clickPostValidate(slotIndex, dragType, clickType, player);
	}

	@Override
	public boolean canDragTo(@NotNull Slot slot) {
		return !(slot instanceof SlotConsume || slot instanceof SlotOutput);
	}
}