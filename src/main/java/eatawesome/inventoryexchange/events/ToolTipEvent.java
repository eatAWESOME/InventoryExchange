package eatawesome.inventoryexchange.events;

import java.util.List;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.api.proxy.IEMCProxy;
import eatawesome.inventoryexchange.config.EMCConfig;
import eatawesome.inventoryexchange.utils.EMCHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = InventoryExchange.MODID, value = Dist.CLIENT)
public class ToolTipEvent {

	@SubscribeEvent
	public static void tTipEvent(ItemTooltipEvent event) {
		ItemStack current = event.getItemStack();
		if (current.isEmpty()) {
			return;
		}
		List<Component> tooltip = event.getToolTip();
		
		if (EMCConfig.client.tagToolTips.get()) {
			current.getTags().forEach(tag -> tooltip.add(Component.literal("#" + tag.location())));
		}

		if (EMCConfig.client.emcToolTips.get() && (!EMCConfig.client.shiftEmcToolTips.get() || Screen.hasShiftDown())) {
			long value = IEMCProxy.INSTANCE.getValue(current);
			if (value > 0) {
				tooltip.add(EMCHelper.getEmcTextComponent(value, 1));
				if (current.getCount() > 1) {
					tooltip.add(EMCHelper.getEmcTextComponent(value, current.getCount()));
				}
			} 
		}
	}
}