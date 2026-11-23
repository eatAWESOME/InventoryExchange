package eatawesome.inventoryexchange.emc.components.processor;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.WritableBookContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import eatawesome.inventoryexchange.api.ItemInfo;
import eatawesome.inventoryexchange.api.components.DataComponentProcessor;
import eatawesome.inventoryexchange.config.InventoryExchangeConfigTranslations;

@DataComponentProcessor
public class WritableBookProcessor extends SimplePersistentComponentProcessor<WritableBookContent> {

	@Override
	public String getName() {
		return InventoryExchangeConfigTranslations.DCP_WRITABLE_BOOK.title();
	}

	@Override
	public String getTranslationKey() {
		return InventoryExchangeConfigTranslations.DCP_WRITABLE_BOOK.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return InventoryExchangeConfigTranslations.DCP_WRITABLE_BOOK.tooltip();
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public long recalculateEMC(@NotNull ItemInfo info, @Range(from = 1, to = Long.MAX_VALUE) long currentEMC) throws ArithmeticException {
		//Contents of the written book do not change the calculated EMC
		return currentEMC;
	}

	@Override
	protected DataComponentType<WritableBookContent> getComponentType(@NotNull ItemInfo info) {
		return DataComponents.WRITABLE_BOOK_CONTENT;
	}

	@Override
	protected boolean shouldPersist(@NotNull ItemInfo info, @NotNull WritableBookContent component) {
		return !component.equals(WritableBookContent.EMPTY);
	}
}
