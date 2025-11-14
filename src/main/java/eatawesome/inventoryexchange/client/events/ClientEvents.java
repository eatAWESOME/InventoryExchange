package eatawesome.inventoryexchange.client.events;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.config.InventoryExchangeClientConfig;
import eatawesome.inventoryexchange.network.packets.to_server.ClientConfigSyncPKT;
import eatawesome.inventoryexchange.network.packets.to_server.KeyPressPKT;
import eatawesome.inventoryexchange.utils.InventoryExchangeKeybind;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = InventoryExchange.MODID, value = Dist.CLIENT)
public class ClientEvents {

	private static boolean pickItemDownOld = false;
	
	// Player connect config sync
    @SubscribeEvent
    public static void onClientLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
    	PacketDistributor.sendToServer(new ClientConfigSyncPKT(
    		InventoryExchangeClientConfig.isCondensingLimited(),
			InventoryExchangeClientConfig.isFeedingLimited(),
			InventoryExchangeClientConfig.isRepairingLimited(),
			InventoryExchangeClientConfig.isSmeltingLimited()
		));
    }
    
    // Pick item
    @SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event) {
    	Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player == null) return;
		
		// Edge detection on vanilla pick block key
		boolean pickItemDown = mc.options.keyPickItem.isDown();
		if (pickItemDown && !pickItemDownOld) {
			if (!player.isCreative() && !player.isSpectator() && mc.screen == null && player.getMainHandItem().isEmpty()) {
				PacketDistributor.sendToServer(new KeyPressPKT(InventoryExchangeKeybind.PICK_ITEM));
			}
		}
		pickItemDownOld = pickItemDown;
	}
}