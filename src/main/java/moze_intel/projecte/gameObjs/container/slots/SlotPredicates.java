package moze_intel.projecte.gameObjs.container.slots;

import java.util.function.Predicate;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.world.item.ItemStack;

public final class SlotPredicates {

	public static final Predicate<ItemStack> ALWAYS_FALSE = input -> false;

	public static final Predicate<ItemStack> HAS_EMC = IEMCProxy.INSTANCE::hasValue;

	public static final Predicate<ItemStack> COLLECTOR_LOCK = input -> false;

	private SlotPredicates() {
	}
}