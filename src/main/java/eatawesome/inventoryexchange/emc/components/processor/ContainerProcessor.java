package eatawesome.inventoryexchange.emc.components.processor;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.jetbrains.annotations.NotNull;

import eatawesome.inventoryexchange.api.ItemInfo;
import eatawesome.inventoryexchange.api.components.DataComponentProcessor;
import eatawesome.inventoryexchange.config.InventoryExchangeConfigTranslations;

@DataComponentProcessor
public class ContainerProcessor extends SimpleContainerProcessor<ItemContainerContents> {

	@Override
	public String getName() {
		return InventoryExchangeConfigTranslations.DCP_CONTAINER.title();
	}

	@Override
	public String getTranslationKey() {
		return InventoryExchangeConfigTranslations.DCP_CONTAINER.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return InventoryExchangeConfigTranslations.DCP_CONTAINER.tooltip();
	}

	@Override
	protected DataComponentType<ItemContainerContents> getComponentType(@NotNull ItemInfo info) {
		return DataComponents.CONTAINER;
	}

	@Override
	protected boolean shouldPersist(@NotNull ItemInfo info, @NotNull ItemContainerContents component) {
		return !component.equals(ItemContainerContents.EMPTY);
	}

	@Override
	protected Iterable<ItemStack> getStoredItems(ItemContainerContents component) {
		return component.nonEmptyItems();
	}
}
