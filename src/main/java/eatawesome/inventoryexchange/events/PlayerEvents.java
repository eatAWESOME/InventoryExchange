package eatawesome.inventoryexchange.events;

import eatawesome.inventoryexchange.InventoryExchange;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = InventoryExchange.MODID)
public class PlayerEvents {

	@SubscribeEvent
	public static void onEntityItemPickup(ItemEntityPickupEvent.Pre event) {
		Player player = event.getPlayer();
	    ItemStack itemStack = event.getItemEntity().getItem();
	    knowledgeCheck(player, itemStack);
	}
	
	@SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
		Player player = event.getEntity();
        AbstractContainerMenu container = event.getContainer();
        containerKnowledgeCheck(player, container);
    }

    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event) {
    	Player player = event.getEntity();
        AbstractContainerMenu container = event.getContainer();
        containerKnowledgeCheck(player, container);
    }
	
	@SubscribeEvent
	public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
		Player player = event.getEntity();
	    ItemStack itemStack = event.getCrafting();
	    knowledgeCheck(player, itemStack);
	}
	
	@SubscribeEvent
	public static void onItemSmelted(PlayerEvent.ItemSmeltedEvent event) {
		Player player = event.getEntity();
	    ItemStack itemStack = event.getSmelting();
	    knowledgeCheck(player, itemStack);
	}

	public static void containerKnowledgeCheck(Player player, AbstractContainerMenu container) {
		for (ItemStack itemStack : container.getItems()) {
			knowledgeCheck(player, itemStack);
	    }
		for (ItemStack itemStack : player.inventoryMenu.getItems()) {
			knowledgeCheck(player, itemStack);
	    }
	}
	
	public static void knowledgeCheck(Player player, ItemStack itemStack) {
		if (player instanceof ServerPlayer serverPlayer) {
	        try {
	        	IKnowledgeProvider knowledge = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
	            if (!itemStack.isEmpty() && IEMCProxy.INSTANCE.getValue(itemStack) > 0) {
	                if (!knowledge.hasKnowledge(itemStack)) {
	                	knowledge.addKnowledge(itemStack);
	                	knowledge.syncKnowledgeChange(serverPlayer, ItemInfo.fromStack(itemStack), true);
	                	player.displayClientMessage(Component.literal("Learned " + itemStack.getItem().getName(itemStack).getString() + " Transmutation Knowledge"), false);
	                }
	            }
	        } catch (IllegalArgumentException e) {
	            System.err.println("Error retrieving knowledge provider: " + e.getMessage());
	        }
	    }
	}
}