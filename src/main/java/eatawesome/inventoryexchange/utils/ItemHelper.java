package eatawesome.inventoryexchange.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public final class ItemHelper {
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
}
