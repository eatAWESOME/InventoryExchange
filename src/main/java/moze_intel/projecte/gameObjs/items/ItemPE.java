package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Range;

public class ItemPE extends Item {

	public ItemPE(Properties props) {
		super(props);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		if (oldStack.getItem() != newStack.getItem()) {
			return true;
		} else if (oldStack.getOrDefault(PEDataComponentTypes.ACTIVE, false) != newStack.getOrDefault(PEDataComponentTypes.ACTIVE, false)) {
			return true;
		}
		return false;
	}

	@Range(from = 0, to = Long.MAX_VALUE)
	private static long getEmc(ItemStack stack) {
		return stack.getOrDefault(PEDataComponentTypes.STORED_EMC, 0L);
	}

	public static void removeEmc(ItemStack stack, float amount) {
		removeEmc(stack, EMCHelper.removeFractionalEMC(stack, amount));
	}

	public static void removeEmc(ItemStack stack, @Range(from = 0, to = Long.MAX_VALUE) long amount) {
		if (amount > 0) {
			stack.update(PEDataComponentTypes.STORED_EMC, 0L, amount, (emc, change) -> Math.max(emc - change, 0));
		}
	}

	public static boolean hasEmc(Player player, ItemStack stack, @Range(from = 0, to = Long.MAX_VALUE) long amount, boolean subUnitRemoval) {
		if (subUnitRemoval && getEmc(stack) > 0) {
			return true;
		}
		return false;
	}

	public static boolean hotBarOrOffHand(int slot) {
		return slot < Inventory.getSelectionSize() || slot == Inventory.SLOT_OFFHAND;
	}
}