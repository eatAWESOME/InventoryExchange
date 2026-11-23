package eatawesome.inventoryexchange.gameObjs.container.slots.transmutation;

import java.math.BigInteger;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import eatawesome.inventoryexchange.api.proxy.IEMCProxy;
import eatawesome.inventoryexchange.gameObjs.container.inventory.TransmutationInventory;
import eatawesome.inventoryexchange.gameObjs.container.slots.InventoryContainerSlot;
import eatawesome.inventoryexchange.utils.ItemHelper;

public class SlotOutput extends InventoryContainerSlot {

	private final TransmutationInventory inv;

	public SlotOutput(TransmutationInventory inv, int index, int x, int y) {
		super(inv, index, x, y);
		this.inv = inv;
	}

	@Override
	protected void onSwapCraft(int amount) {
		remove(amount);
	}

	@NotNull
	@Override
	public ItemStack remove(int amount) {
		if (amount == 0) {
			return ItemStack.EMPTY;
		}
		ItemStack stack = ItemHelper.size(getItem(), amount);
		long emcValue = IEMCProxy.INSTANCE.getValue(stack);
		BigInteger bigEmcValue = BigInteger.valueOf(emcValue);
		if (amount > 1) {
			bigEmcValue = bigEmcValue.multiply(BigInteger.valueOf(amount));
			if (bigEmcValue.compareTo(inv.getAvailableEmc()) > 0) {
				//Requesting more emc than available
				//Container expects stacksize=0-Itemstack for 'nothing'
				return ItemStack.EMPTY;
			}
		} else if (emcValue > inv.getAvailableEmcAsLong()) {
			//Requesting more emc than available
			//Container expects stacksize=0-Itemstack for 'nothing'
			return ItemStack.EMPTY;
		}
		if (inv.isServer()) {
			inv.removeEmc(bigEmcValue);
		}
		return stack;
	}

	@Override
	public void onTake(Player player, @NotNull ItemStack stack) {
		if (player instanceof ServerPlayer serverPlayer && stack.getCount() > 0) {
			serverPlayer.awardStat(Stats.ITEM_CRAFTED.get(stack.getItem()), stack.getCount());
		}
		super.onTake(player, stack);
	}
	
	@Override
	public void initialize(@NotNull ItemStack stack) {
	}

	@Override
	public void set(@NotNull ItemStack stack) {
	}

	@Override
	public boolean mayPlace(@NotNull ItemStack stack) {
		return false;
	}

	@Override
	public boolean mayPickup(@NotNull Player player) {
		return !hasItem() || IEMCProxy.INSTANCE.getValue(getItem()) <= inv.getAvailableEmcAsLong();
	}
}