package eatawesome.inventoryexchange.emc.components.processor;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import eatawesome.inventoryexchange.api.ItemInfo;
import eatawesome.inventoryexchange.api.components.DataComponentProcessor;
import eatawesome.inventoryexchange.api.proxy.IEMCProxy;
import eatawesome.inventoryexchange.config.InventoryExchangeConfigTranslations;

@DataComponentProcessor
public class ArmorTrimProcessor extends PersistentComponentProcessor<ArmorTrim> {

	@Override
	public String getName() {
		return InventoryExchangeConfigTranslations.DCP_ARMOR_TRIM.title();
	}

	@Override
	public String getTranslationKey() {
		return InventoryExchangeConfigTranslations.DCP_ARMOR_TRIM.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return InventoryExchangeConfigTranslations.DCP_ARMOR_TRIM.tooltip();
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public long recalculateEMC(@NotNull ItemInfo info, @Range(from = 1, to = Long.MAX_VALUE) long currentEMC, @NotNull ArmorTrim trim) throws ArithmeticException {
		Holder<Item> material = trim.material().value().ingredient();
		long materialEmc = IEMCProxy.INSTANCE.getValue(material);
		if (materialEmc == 0) {
			//The material for the trim doesn't have an EMC value, so there is no valid EMC value for the applied trim as a whole
			return 0;
		}
		Holder<Item> template = trim.pattern().value().templateItem();
		long templateEmc = IEMCProxy.INSTANCE.getValue(template);
		if (templateEmc == 0) {
			//The template for the trim doesn't have an EMC value, and given the template is consumed: there is no valid EMC value for the applied trim as a whole
			return 0;
		}
		return Math.addExact(
				Math.addExact(currentEMC, materialEmc),
				templateEmc
		);
	}

	@Override
	protected boolean validItem(@NotNull ItemInfo info) {
		return info.getItem().is(ItemTags.TRIMMABLE_ARMOR);
	}

	@Override
	protected boolean shouldPersist(@NotNull ItemInfo info, @NotNull ArmorTrim component) {
		return true;
	}

	@Override
	protected DataComponentType<ArmorTrim> getComponentType(@NotNull ItemInfo info) {
		return DataComponents.TRIM;
	}
}
