package eatawesome.inventoryexchange.client;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.config.InventoryExchangeClientConfig;
import eatawesome.inventoryexchange.gameObjs.gui.InfiniteContainerScreen;
import eatawesome.inventoryexchange.gameObjs.gui.PortableFurnaceScreen;
import eatawesome.inventoryexchange.gameObjs.gui.TransmutationScreen;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeContainerTypes;
import eatawesome.inventoryexchange.utils.ClientKeyHelper;
import mezz.jei.api.runtime.IRecipesGui;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = InventoryExchange.MODID, dist = Dist.CLIENT)
public class InventoryExchangeClient {

	public InventoryExchangeClient(ModContainer modContainer, IEventBus modEventBus) {
		modContainer.registerConfig(ModConfig.Type.CLIENT, InventoryExchangeClientConfig.CLIENT_SPEC, InventoryExchange.MODID + "/client.toml");
		modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
		
		modEventBus.addListener(this::registerKeybindings);
		modEventBus.addListener(this::registerScreens);
		modEventBus.addListener(this::clientSetup);
	}
	
	private void registerKeybindings(RegisterKeyMappingsEvent event) {
		ClientKeyHelper.registerKeyBindings(event);
	}
	
	private void registerScreens(RegisterMenuScreensEvent event) {
        event.register(InventoryExchangeContainerTypes.INFINITE_CONTAINER.get(), InfiniteContainerScreen::new);
		event.register(InventoryExchangeContainerTypes.PORTABLE_FURNACE.get(), PortableFurnaceScreen::new);
		event.register(InventoryExchangeContainerTypes.TRANSMUTATION_CONTAINER.get(), TransmutationScreen::new);
    }
	
	private void clientSetup(FMLClientSetupEvent evt) {
		if (ModList.get().isLoaded("jei")) {
			NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, (ScreenEvent.Opening event) -> {
				if (event.getCurrentScreen() instanceof TransmutationScreen screen) {
					if (event.getNewScreen() instanceof IRecipesGui) {
						screen.switchingToJEI = true;
					}
				}
			});
		}
		InventoryExchange.debugLog("Condensing Limited = {}", InventoryExchangeClientConfig.isCondensingLimited());
		InventoryExchange.debugLog("Feeding Limited = {}", InventoryExchangeClientConfig.isFeedingLimited());
		InventoryExchange.debugLog("Repairing Limited = {}", InventoryExchangeClientConfig.isRepairingLimited());
		InventoryExchange.debugLog("Smelting Limited = {}", InventoryExchangeClientConfig.isSmeltingLimited());
	}
}