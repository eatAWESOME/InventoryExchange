package eatawesome.inventoryexchange.emc.components.processor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import eatawesome.inventoryexchange.api.ItemInfo;

public abstract class PersistentComponentProcessor<TYPE> extends SimplePersistentComponentProcessor<TYPE> {

	@Range(from = 0, to = Long.MAX_VALUE)
	protected abstract long recalculateEMC(@NotNull ItemInfo info, @Range(from = 1, to = Long.MAX_VALUE) long currentEMC, @NotNull TYPE component) throws ArithmeticException;

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public final long recalculateEMC(@NotNull ItemInfo info, @Range(from = 1, to = Long.MAX_VALUE) long currentEMC) throws ArithmeticException {
		if (validItem(info)) {
			TYPE component = info.getOrNull(getComponentType(info));
			if (component != null && shouldPersist(info, component)) {
				return recalculateEMC(info, currentEMC, component);
			}
		}
		return currentEMC;
	}
}