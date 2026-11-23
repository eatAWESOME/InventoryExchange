package eatawesome.inventoryexchange.utils;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public final class ItemHelper {
	
	public static IItemHandlerModifiable immutableCopy(IItemHandler toCopy) {
		int slots = toCopy.getSlots();
		final List<ItemStack> list = new ArrayList<>(slots);
		for (int i = 0; i < slots; i++) {
			list.add(toCopy.getStackInSlot(i).copy());
		}
		return new IItemHandlerModifiable() {
			@Override
			public void setStackInSlot(int slot, @NotNull ItemStack stack) {
			}

			@Override
			public int getSlots() {
				return list.size();
			}

			@NotNull
			@Override
			public ItemStack getStackInSlot(int slot) {
				return list.get(slot);
			}

			@NotNull
			@Override
			public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
				return stack;
			}

			@NotNull
			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				return ItemStack.EMPTY;
			}

			@Override
			public int getSlotLimit(int slot) {
				return getStackInSlot(slot).getMaxStackSize();
			}

			@Override
			public boolean isItemValid(int slot, @NotNull ItemStack stack) {
				return true;
			}
		};
	}
	
	public static int simulateFit(Player player, ItemStack itemStack) {
		int remainder = itemStack.getCount();
		for (ItemStack invStack : player.getInventory().items) {
			if (invStack.isEmpty()) {
				//Slot is empty, just put it all there
				return 0;
			}
			if (ItemStack.isSameItemSameComponents(itemStack, invStack)) {
				int amountSlotNeeds = invStack.getMaxStackSize() - invStack.getCount();
				//Double check we don't have an over sized stack
				if (amountSlotNeeds > 0) {
					if (remainder <= amountSlotNeeds) {
						//If the slot can accept it all, return it all fit
						return 0;
					}
					//Otherwise take that many items out and
					remainder -= amountSlotNeeds;
				}
			}
			if (remainder <= 0) {
				return remainder;
			}
		}
		IItemHandlerModifiable handler = InfiniteContainerHelper.getInventory(player);
		int slots = handler.getSlots();
		for (int i = 0; i < slots && !itemStack.isEmpty(); i++) {
            ItemStack invStack = handler.getStackInSlot(i);
            if (invStack.isEmpty()) {
				//Slot is empty, just put it all there
				return 0;
			}
			if (ItemStack.isSameItemSameComponents(itemStack, invStack)) {
				int amountSlotNeeds = invStack.getMaxStackSize() - invStack.getCount();
				//Double check we don't have an over sized stack
				if (amountSlotNeeds > 0) {
					if (remainder <= amountSlotNeeds) {
						//If the slot can accept it all, return it all fit
						return 0;
					}
					//Otherwise take that many items out and
					remainder -= amountSlotNeeds;
				}
			}
			if (remainder <= 0) {
				return remainder;
			}
		}
		return remainder;
	}
	
	/**
	 * @return The amount of the given stack that could not fit. If it all fit, zero is returned
	 */
	public static int simulateFit(NonNullList<ItemStack> inv, ItemStack stack) {
		int remainder = stack.getCount();
		for (ItemStack invStack : inv) {
			if (invStack.isEmpty()) {
				//Slot is empty, just put it all there
				return 0;
			}
			if (ItemStack.isSameItemSameComponents(stack, invStack)) {
				int amountSlotNeeds = invStack.getMaxStackSize() - invStack.getCount();
				//Double check we don't have an over sized stack
				if (amountSlotNeeds > 0) {
					if (remainder <= amountSlotNeeds) {
						//If the slot can accept it all, return it all fit
						return 0;
					}
					//Otherwise take that many items out and
					remainder -= amountSlotNeeds;
				}
			}
		}
		return remainder;
	}
	
	public static ItemStack size(ItemStack stack, int size) {
		if (size <= 0 || stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		return stack.copyWithCount(size);
	}
}
