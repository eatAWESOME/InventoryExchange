package eatawesome.inventoryexchange.integration.wthit;

import eatawesome.inventoryexchange.api.proxy.IEMCProxy;
import eatawesome.inventoryexchange.utils.EMCHelper;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;

public class WTHITDataProvider implements IBlockComponentProvider {

	static final WTHITDataProvider INSTANCE = new WTHITDataProvider();

	@Override
	public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
		long value = IEMCProxy.INSTANCE.getValue(accessor.getBlock());
		if (value > 0) {
			tooltip.addLine(EMCHelper.getEmcTextComponent(value, 1));
		}
	}
}