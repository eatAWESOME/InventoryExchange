package eatawesome.inventoryexchange.gameObjs.container;

import org.jetbrains.annotations.NotNull;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeContainerTypes;
import eatawesome.inventoryexchange.utils.PortableFurnaceHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.SlotItemHandler;

public class PortableFurnaceMenu extends AbstractContainerMenu {

	public static final int EMC_PER = 16;

	private final Player player;
	private final IItemHandlerModifiable handler;

	public PortableFurnaceMenu(int windowId, Inventory inv) {
		super(InventoryExchangeContainerTypes.PORTABLE_FURNACE.get(), windowId);
		this.player = inv.player;
		this.handler = PortableFurnaceHelper.getInventory(player);

		this.addSlot(new SlotItemHandler(handler, 0, 56, 17));
		this.addSlot(new SlotItemHandler(handler, 1, 56, 53) {
			@Override public boolean mayPlace(@NotNull ItemStack s) { return false; }
			@Override public boolean mayPickup(Player p) { return false; }
			@Override public boolean isHighlightable() { return false; }
		});
		this.addSlot(new SlotItemHandler(handler, 2, 116, 35) {
			@Override public boolean mayPlace(@NotNull ItemStack s) { return false; }
			@Override public boolean mayPickup(Player p) { return !handler.getStackInSlot(2).isEmpty(); }
		});

		for (int r = 0; r < 3; ++r) for (int c = 0; c < 9; ++c) this.addSlot(new Slot(inv, c + r * 9 + 9, 8 + c * 18, 84 + r * 18));
		for (int c = 0; c < 9; ++c) this.addSlot(new Slot(inv, c, 8 + c * 18, 142));

		if (!player.level().isClientSide) setRemoteSlot(1, new ItemStack(Items.COAL));
	}

	@Override
	public void broadcastChanges() {
		if (!player.level().isClientSide) {
			boolean changed = PortableFurnaceHelper.smeltOneIfPossible(player, handler);
			if (changed) {
				setRemoteSlot(0, handler.getStackInSlot(0));
				setRemoteSlot(2, handler.getStackInSlot(2));
				PortableFurnaceHelper.sync(player);
			}
			setRemoteSlot(1, new ItemStack(Items.COAL));
		}
		super.broadcastChanges();
	}

	@Override
	public @NotNull ItemStack quickMoveStack(@NotNull Player p, int index) {
		ItemStack empty = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if (slot == null || !slot.hasItem()) return empty;

		ItemStack stack = slot.getItem();
		ItemStack original = stack.copy();

		if (index == 2) {
			if (!moveItemStackTo(stack, 3, 39, true)) return empty;
			slot.setChanged();
			return original;
		} else if (index == 0) {
			if (!moveItemStackTo(stack, 3, 39, false)) return empty;
		} else if (index == 1) {
			return empty;
		} else {
			if (!moveItemStackTo(stack, 0, 1, false)) return empty;
		}

		if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
		else slot.setChanged();
		return original;
	}

	@Override
	public boolean stillValid(@NotNull Player p) { return true; }
}