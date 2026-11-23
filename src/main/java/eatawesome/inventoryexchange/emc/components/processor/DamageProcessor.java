package eatawesome.inventoryexchange.emc.components.processor;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import eatawesome.inventoryexchange.api.ItemInfo;
import eatawesome.inventoryexchange.api.components.DataComponentProcessor;
import eatawesome.inventoryexchange.api.components.IDataComponentProcessor;
import eatawesome.inventoryexchange.config.InventoryExchangeConfigTranslations;

@DataComponentProcessor(priority = Integer.MAX_VALUE)
public class DamageProcessor implements IDataComponentProcessor {

	@DataComponentProcessor.Instance
	public static final DamageProcessor INSTANCE = new DamageProcessor();

	@Override
	public String getName() {
		return InventoryExchangeConfigTranslations.DCP_DAMAGE.title();
	}

	@Override
	public String getTranslationKey() {
		return InventoryExchangeConfigTranslations.DCP_DAMAGE.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return InventoryExchangeConfigTranslations.DCP_DAMAGE.tooltip();
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public long recalculateEMC(@NotNull ItemInfo info, @Range(from = 1, to = Long.MAX_VALUE) long currentEMC) throws ArithmeticException {
		ItemStack fakeStack = info.createStack();
		if (fakeStack.isDamaged()) {
			int maxDamage = fakeStack.getMaxDamage();
			//If mods implement their custom damage values incorrectly damage may be greater than max damage,
			// in which case we just ignore the damage data rather than making the item have no emc
			int remainingDurability = maxDamage - fakeStack.getDamageValue();
			if (remainingDurability == 0) {
				//Note: Shouldn't happen anymore as vanilla properly destroys tools when they get used at Durability: 1/ max
				// if we do have this case for some reason, return that it isn't worth any emc anymore
				return 0;
			} else if (remainingDurability == 1) {
				//Skip the multiplication
				currentEMC /= maxDamage;
			} else if (remainingDurability > 1) {
				currentEMC = Math.multiplyExact(currentEMC, remainingDurability) / maxDamage;
			}
		}
		return currentEMC;
	}
}