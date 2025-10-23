package eatawesome.inventoryexchange.events;

import java.math.BigInteger;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.capabilities.IAbilityEMCProvider;
import eatawesome.inventoryexchange.capabilities.InventoryExchangeCapabilities;
import eatawesome.inventoryexchange.network.packets.to_client.KnowledgeToastPKT;
import eatawesome.inventoryexchange.utils.InfiniteContainerHelper;
import eatawesome.inventoryexchange.utils.PlayerHelper;
import eatawesome.inventoryexchange.utils.PortableFurnaceHelper;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = InventoryExchange.MODID)
public class PlayerEvents {

	// Learn knowledge on item pickup and insert items into infinite container if inventory full
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
	
	// Insert items leftover from pickup into infinite container
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
	
	// Knowledge from container open
	@SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
		Player player = event.getEntity();
        AbstractContainerMenu container = event.getContainer();
        containerKnowledgeCheck(player, container);
    }

	// Knowledge from container close
    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event) {
    	Player player = event.getEntity();
        AbstractContainerMenu container = event.getContainer();
        containerKnowledgeCheck(player, container);
    }
	
    // Knowledge from crafting
	@SubscribeEvent
	public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
		Player player = event.getEntity();
	    ItemStack itemStack = event.getCrafting();
	    knowledgeCheck(player, itemStack);
	}
	
	// Knowledge from smelting
	@SubscribeEvent
	public static void onItemSmelted(PlayerEvent.ItemSmeltedEvent event) {
		Player player = event.getEntity();
	    ItemStack itemStack = event.getSmelting();
	    knowledgeCheck(player, itemStack);
	}

	// Drop items on death
	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		if (event.isWasDeath()) {
			Player oldPlayer = event.getOriginal();
			Player newPlayer = event.getEntity();
			if (newPlayer instanceof ServerPlayer newServerPlayer && !newPlayer.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
				IItemHandlerModifiable oldInfinite = InfiniteContainerHelper.getInventory(oldPlayer);
				IItemHandlerModifiable oldFurnace = PortableFurnaceHelper.getInventory(oldPlayer);
				IItemHandlerModifiable newInfinite = InfiniteContainerHelper.getInventory(newPlayer);
				IItemHandlerModifiable newFurnace = PortableFurnaceHelper.getInventory(newPlayer);

				dropAndClearHandler(oldPlayer, oldInfinite, false);
				dropAndClearHandler(oldPlayer, oldFurnace, true);

				clearHandler(newInfinite, false);
				clearHandler(newFurnace, true);
				
				IKnowledgeProvider oldKnowledge = oldPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
				IAbilityEMCProvider oldAbilityEMC = oldPlayer.getCapability(InventoryExchangeCapabilities.ABILITY_EMC_CAPABILITY);
				IKnowledgeProvider newKnowledge = newPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
				IAbilityEMCProvider newAbilityEMC = newPlayer.getCapability(InventoryExchangeCapabilities.ABILITY_EMC_CAPABILITY);

				oldKnowledge.setEmc(BigInteger.valueOf(0));
				oldKnowledge.syncEmc((ServerPlayer) oldPlayer);
				oldAbilityEMC.setEmc(BigInteger.valueOf(0));
				oldAbilityEMC.sync((ServerPlayer) oldPlayer);
				newKnowledge.setEmc(BigInteger.valueOf(0));
				newKnowledge.syncEmc(newServerPlayer);
				newAbilityEMC.setEmc(BigInteger.valueOf(0));
				newAbilityEMC.sync(newServerPlayer);
			}
		}
	}
	
	@SubscribeEvent
	public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
		if (event.getEntity() instanceof Player player && PlayerHelper.hasFullUltimaSet(player)) {
			event.setNewDamage(0.0F);
		}
	}
	
	@SubscribeEvent
	public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
		if (event.getEntity() instanceof Player player) {
			if (event.getSlot() == EquipmentSlot.HEAD || event.getSlot() == EquipmentSlot.CHEST || event.getSlot() == EquipmentSlot.LEGS || event.getSlot() == EquipmentSlot.FEET) {
				if (!PlayerHelper.hasFullUltimaSet(player)) {
					PlayerHelper.removeUltimaEffects(player);
				}
			}
		}
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
	                	ItemStack icon = itemStack.copy();
						icon.setCount(1);
						PacketDistributor.sendToPlayer(serverPlayer, new KnowledgeToastPKT(icon));
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

	private static void dropAndClearHandler(Player source, IItemHandlerModifiable handler, boolean isPortableFurnace) {
		if (handler == null) return;
		for (int i = 0; i < handler.getSlots(); i++) {
			if (isPortableFurnace && i == 1) continue;
			ItemStack stack = handler.getStackInSlot(i);
			if (!stack.isEmpty()) {
				ItemStack toDrop = stack.copy();
				handler.setStackInSlot(i, ItemStack.EMPTY);
				ItemEntity entity = new ItemEntity(source.level(), source.getX(), source.getY() + 0.5, source.getZ(), toDrop);
				source.level().addFreshEntity(entity);
			}
		}
	}

	private static void clearHandler(IItemHandlerModifiable handler, boolean isPortableFurnace) {
		if (handler == null) return;
		for (int i = 0; i < handler.getSlots(); i++) {
			if (isPortableFurnace && i == 1) continue;
			handler.setStackInSlot(i, ItemStack.EMPTY);
		}
	}
}