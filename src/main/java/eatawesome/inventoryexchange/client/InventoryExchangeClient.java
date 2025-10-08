package eatawesome.inventoryexchange.client;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.utils.ClientKeyHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = InventoryExchange.MODID, dist = Dist.CLIENT)
public class InventoryExchangeClient {

	public InventoryExchangeClient(ModContainer container, IEventBus modEventBus) {
		container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
		modEventBus.addListener(this::registerKeybindings);
	}
	
	private void registerKeybindings(RegisterKeyMappingsEvent event) {
		ClientKeyHelper.registerKeyBindings(event);
	}
}