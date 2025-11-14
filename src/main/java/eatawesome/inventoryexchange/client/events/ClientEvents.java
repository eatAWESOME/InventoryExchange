package eatawesome.inventoryexchange.client.events;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.config.InventoryExchangeClientConfig;
import eatawesome.inventoryexchange.network.packets.to_server.ClientConfigSyncPKT;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = InventoryExchange.MODID, value = Dist.CLIENT)
public class ClientEvents {

	// Player connect config sync
    @SubscribeEvent
    public static void onClientLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
    	syncConfig();
    }
    
    private static void syncConfig() {
    	PacketDistributor.sendToServer(new ClientConfigSyncPKT(
    		InventoryExchangeClientConfig.isCondensingLimited(),
			InventoryExchangeClientConfig.isFeedingLimited(),
			InventoryExchangeClientConfig.isRepairingLimited(),
			InventoryExchangeClientConfig.isSmeltingLimited()
		));
    }
}