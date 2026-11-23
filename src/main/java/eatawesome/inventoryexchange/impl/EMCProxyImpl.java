package eatawesome.inventoryexchange.impl;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import eatawesome.inventoryexchange.api.ItemInfo;
import eatawesome.inventoryexchange.api.proxy.IEMCProxy;
import eatawesome.inventoryexchange.emc.components.DataComponentManager;
import eatawesome.inventoryexchange.utils.EMCHelper;

public class EMCProxyImpl implements IEMCProxy {

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public long getValue(@NotNull ItemInfo info) {
		return DataComponentManager.getEmcValue(Objects.requireNonNull(info));
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public long getSellValue(@NotNull ItemInfo info) {
		return EMCHelper.getEmcSellValue(getValue(info));
	}
}