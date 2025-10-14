package eatawesome.inventoryexchange.events;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.utils.InfiniteContainerHelper;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

@EventBusSubscriber(modid = InventoryExchange.MODID)
public class PlayerEvents {

	@SubscribeEvent
	public static void onEntityItemPickupPre(ItemEntityPickupEvent.Pre event) {
		Player player = event.getPlayer();
	    ItemStack itemStack = event.getItemEntity().getItem();
	    knowledgeCheck(player, itemStack);
	    if (!player.level().isClientSide && !itemStack.isEmpty() && !event.getItemEntity().hasPickUpDelay()) {
	    	if (!canInventoryAccept(player.getInventory(), itemStack)) {
	        	IItemHandlerModifiable infinite = InfiniteContainerHelper.getInventory(player);
		        ItemStack insertStack = itemStack.copy();
		        int preInsertCount = insertStack.getCount();
		        if (insertIntoHandler(infinite, insertStack)) {
		        	if (preInsertCount - insertStack.getCount() > 0) {
		        		playPickupSound(player);
		        	}
		        	if (insertStack.isEmpty()) {
			            event.getItemEntity().discard();
			        } else {
			            event.getItemEntity().setItem(insertStack);
			        }
		        }
	    	}
        }
	}
	
	@SubscribeEvent
	public static void onEntityItemPickupPost(ItemEntityPickupEvent.Post event) {
		Player player = event.getPlayer();
	    ItemStack itemStack = event.getItemEntity().getItem();
	    if (!player.level().isClientSide && !itemStack.isEmpty() && !event.getItemEntity().isRemoved()) {
        	IItemHandlerModifiable infinite = InfiniteContainerHelper.getInventory(player);
	        ItemStack insertStack = itemStack.copy();
	        int preInsertCount = insertStack.getCount();
	        if (insertIntoHandler(infinite, insertStack)) {
	        	if (preInsertCount - insertStack.getCount() > 0) {
	        		playPickupSound(player);
	        	}
	        	if (insertStack.isEmpty()) {
		            event.getItemEntity().discard();
		        } else {
		            event.getItemEntity().setItem(insertStack);
		        }
	        }
        }
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
	
	private static boolean canInventoryAccept(Inventory inv, ItemStack stack) {
		if (stack.isEmpty()) return true;
		for (ItemStack cur : inv.items) {
			if (cur.isEmpty()) {
				return true;
			}
			if (ItemStack.isSameItemSameComponents(cur, stack)) {
				int limit = Math.min(cur.getMaxStackSize(), 64);
				if (cur.getCount() < limit) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean insertIntoHandler(IItemHandlerModifiable handler, ItemStack stack) {
		if (stack.isEmpty()) return false;
		boolean changed = false;
		int slots = handler.getSlots();

		// Merge into existing stacks
		for (int i = 0; i < slots && !stack.isEmpty(); i++) {
			ItemStack cur = handler.getStackInSlot(i);
			if (cur.isEmpty()) continue;
			if (!ItemStack.isSameItemSameComponents(cur, stack)) continue;

			int limit = Math.min(cur.getMaxStackSize(), handler.getSlotLimit(i));
			if (cur.getCount() >= limit) continue;

			int canAdd = Math.min(limit - cur.getCount(), stack.getCount());
			if (canAdd <= 0) continue;

			ItemStack newStack = cur.copy();
			newStack.grow(canAdd);
			handler.setStackInSlot(i, newStack);
			stack.shrink(canAdd);
			changed = true;
		}

		// Fill empty slots
		for (int i = 0; i < slots && !stack.isEmpty(); i++) {
			ItemStack cur = handler.getStackInSlot(i);
			if (!cur.isEmpty()) continue;
			if (!handler.isItemValid(i, stack)) continue;

			int toMove = Math.min(stack.getCount(), Math.min(stack.getMaxStackSize(), handler.getSlotLimit(i)));
			if (toMove <= 0) continue;

			ItemStack place = stack.copy();
			place.setCount(toMove);
			handler.setStackInSlot(i, place);
			stack.shrink(toMove);
			changed = true;
		}
		return changed;
	}
	
	private static void playPickupSound(Player player) {
        float vol = 0.2F;
        float pitch = ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F;
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, vol, pitch);
    }
}