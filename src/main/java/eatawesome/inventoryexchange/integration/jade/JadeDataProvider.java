package eatawesome.inventoryexchange.integration.jade;

import eatawesome.inventoryexchange.api.proxy.IEMCProxy;
import eatawesome.inventoryexchange.utils.EMCHelper;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class JadeDataProvider implements IBlockComponentProvider {

	static final JadeDataProvider INSTANCE = new JadeDataProvider();

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
		long value = IEMCProxy.INSTANCE.getValue(accessor.getBlock());
		if (value > 0) {
			tooltip.add(EMCHelper.getEmcTextComponent(value, 1));
		}
	}

	@Override
	public ResourceLocation getUid() {
		return InventoryExchangeJadeConstants.EMC_PROVIDER;
	}
}