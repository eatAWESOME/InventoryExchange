package eatawesome.inventoryexchange.gameObjs.container;

import org.jetbrains.annotations.NotNull;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeContainerTypes;
import eatawesome.inventoryexchange.utils.PlayerHelper;
import eatawesome.inventoryexchange.utils.PortableFurnaceHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.SlotItemHandler;

public class PortableFurnaceMenu extends AbstractContainerMenu {

	public static final int EMC_PER = 16;

	private final Player viewer;
	private final Player owner;
	private final IItemHandlerModifiable handler;
	private final boolean readOnly;

	public PortableFurnaceMenu(int windowId, Inventory inv) {
		this(windowId, inv, inv.player);
	}

	public PortableFurnaceMenu(int windowId, Inventory inv, Player owner) {
		super(InventoryExchangeContainerTypes.PORTABLE_FURNACE.get(), windowId);
		this.viewer = inv.player;
		this.owner = owner;
		this.readOnly = this.viewer.getUUID() != this.owner.getUUID();
		this.handler = PortableFurnaceHelper.getInventory(this.owner);

		this.addSlot(new SlotItemHandler(handler, 0, 56, 17) {
			@Override public boolean mayPlace(@NotNull ItemStack itemStack) { return !readOnly && super.mayPlace(itemStack); }
			@Override public boolean mayPickup(Player player) { return !readOnly; }
		});
		this.addSlot(new SlotItemHandler(handler, 1, 56, 53) {
			@Override public boolean mayPlace(@NotNull ItemStack itemStack) { return false; }
			@Override public boolean mayPickup(Player player) { return false; }
			@Override public boolean isHighlightable() { return false; }
			@Override public @NotNull ItemStack getItem() { return ItemStack.EMPTY; }
		});
		this.addSlot(new SlotItemHandler(handler, 2, 116, 35) {
			@Override public boolean mayPlace(@NotNull ItemStack itemStack) { return false; }
			@Override public boolean mayPickup(Player player) { return !handler.getStackInSlot(2).isEmpty() && !readOnly; }
			@Override
			public void onTake(Player player, ItemStack itemStack) {
				if (!player.level().isClientSide && !itemStack.isEmpty()) {
					NeoForge.EVENT_BUS.post(new PlayerEvent.ItemSmeltedEvent(player, itemStack.copy()));
					float exp = PortableFurnaceHelper.getSmeltingExperienceFromOutput(player.level(), itemStack);
					PlayerHelper.awardExp(player, exp);
				}
				super.onTake(player, itemStack);
			}
		});

		for (int r = 0; r < 3; ++r) for (int c = 0; c < 9; ++c) this.addSlot(new Slot(inv, c + r * 9 + 9, 8 + c * 18, 84 + r * 18));
		for (int c = 0; c < 9; ++c) this.addSlot(new Slot(inv, c, 8 + c * 18, 142));
	}

	@Override
	public void broadcastChanges() {
		if (!viewer.level().isClientSide) {
			boolean changed = PortableFurnaceHelper.smeltOneIfPossible(owner, handler);
			if (changed) {
				setRemoteSlot(0, handler.getStackInSlot(0));
				setRemoteSlot(2, handler.getStackInSlot(2));
				PortableFurnaceHelper.sync(owner);
			}
		}
		super.broadcastChanges();
	}

	@Override
	public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
		if (readOnly) return ItemStack.EMPTY;
		ItemStack empty = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if (slot == null || !slot.hasItem()) return empty;

		ItemStack stack = slot.getItem();
		ItemStack original = stack.copy();

		if (index == 2) {
			int before = stack.getCount();
			ItemStack movedCopy = original.copy();
			if (!moveItemStackTo(stack, 3, 39, true)) return empty;
			int after = stack.getCount();
			int moved = before - after;
			if (moved > 0) {
				slot.onTake(player, movedCopy.copyWithCount(moved));
			}
			slot.setChanged();
			return original;
		} else if (index == 0) {
			if (!moveItemStackTo(stack, 3, 39, false)) return empty;
		} else if (index == 1) {
			return empty;
		} else {
			transferOutputIfPresent(player);
			if (!moveItemStackTo(stack, 0, 1, false)) return empty;
		}

		if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
		else slot.setChanged();
		return original;
	}
	
	@Override
	public boolean stillValid(@NotNull Player player) {
		return true;
	}
	
	private void transferOutputIfPresent(Player player) {
		Slot outputSlot = this.getSlot(2);
		ItemStack output = outputSlot.getItem();
		if (output.isEmpty()) return;

		ItemStack moving = output.copy();
		boolean fullyAdded = player.getInventory().add(moving);
		int moved = output.getCount() - moving.getCount();
		if (moved > 0) {
			outputSlot.onTake(player, output.copyWithCount(moved));
			output.shrink(moved);
			if (output.isEmpty() || fullyAdded) {
				outputSlot.set(ItemStack.EMPTY);
			} else {
				outputSlot.setChanged();
			}
		}
	}
}