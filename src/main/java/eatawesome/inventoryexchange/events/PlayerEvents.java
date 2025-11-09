package eatawesome.inventoryexchange.events;

import java.math.BigInteger;
import java.util.List;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.capabilities.IAbilityEMCProvider;
import eatawesome.inventoryexchange.capabilities.InventoryExchangeCapabilities;
import eatawesome.inventoryexchange.utils.InfiniteContainerHelper;
import eatawesome.inventoryexchange.utils.PlayerHelper;
import eatawesome.inventoryexchange.utils.PortableFurnaceHelper;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

@EventBusSubscriber(modid = InventoryExchange.MODID)
public class PlayerEvents {

	// Learn knowledge on item pickup and insert items into infinite container if inventory full
	@SubscribeEvent
	public static void onEntityItemPickupPre(ItemEntityPickupEvent.Pre event) {
		Player player = event.getPlayer();
	    ItemStack itemStack = event.getItemEntity().getItem();
	    PlayerHelper.knowledgeCheck(player, itemStack);
	    if (!player.level().isClientSide && !itemStack.isEmpty() && !event.getItemEntity().hasPickUpDelay()) {
	    	if (!PlayerHelper.canInventoryAccept(player.getInventory(), itemStack)) {
	        	IItemHandlerModifiable infinite = InfiniteContainerHelper.getInventory(player);
		        ItemStack insertStack = itemStack.copy();
		        int preInsertCount = insertStack.getCount();
		        if (PlayerHelper.insertIntoHandler(infinite, insertStack)) {
		        	if (preInsertCount - insertStack.getCount() > 0) {
		        		PlayerHelper.playPickupSound(player);
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
	        if (PlayerHelper.insertIntoHandler(infinite, insertStack)) {
	        	if (preInsertCount - insertStack.getCount() > 0) {
	        		PlayerHelper.playPickupSound(player);
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
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerHelper.containerKnowledgeCheck(serverPlayer, event.getContainer());
            PlayerHelper.syncCapabilities(serverPlayer);
        }
    }

	// Knowledge from container close
    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerHelper.containerKnowledgeCheck(serverPlayer, event.getContainer());
            PlayerHelper.syncCapabilities(serverPlayer);
        }
    }
	
    // Knowledge from crafting
	@SubscribeEvent
	public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
		Player player = event.getEntity();
	    ItemStack itemStack = event.getCrafting();
	    PlayerHelper.knowledgeCheck(player, itemStack);
	}
	
	// Knowledge from smelting
	@SubscribeEvent
	public static void onItemSmelted(PlayerEvent.ItemSmeltedEvent event) {
		Player player = event.getEntity();
	    ItemStack itemStack = event.getSmelting();
	    PlayerHelper.knowledgeCheck(player, itemStack);
	}
	
	// Respawn capability sync
	@SubscribeEvent
	public static void respawnEvent(PlayerEvent.PlayerRespawnEvent event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			PlayerHelper.syncCapabilities(serverPlayer);
		}
	}

	// Dimension change capability sync
	@SubscribeEvent
	public static void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			PlayerHelper.syncCapabilities(serverPlayer);
		}
	}
	
	// Player connect capability sync
	@SubscribeEvent
	public static void playerConnect(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			PlayerHelper.syncCapabilities(serverPlayer);
		}
	}

	// Drop items on death
	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		Player oldPlayer = event.getOriginal();
		Player newPlayer = event.getEntity();
		if (newPlayer instanceof ServerPlayer newServerPlayer) {
			if (event.isWasDeath() && !newPlayer.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
				IItemHandlerModifiable oldInfinite = InfiniteContainerHelper.getInventory(oldPlayer);
				IItemHandlerModifiable oldFurnace = PortableFurnaceHelper.getInventory(oldPlayer);
				IItemHandlerModifiable newInfinite = InfiniteContainerHelper.getInventory(newPlayer);
				IItemHandlerModifiable newFurnace = PortableFurnaceHelper.getInventory(newPlayer);
	
				PlayerHelper.dropAndClearHandler(oldPlayer, oldInfinite, false);
				PlayerHelper.dropAndClearHandler(oldPlayer, oldFurnace, true);
	
				PlayerHelper.clearHandler(newInfinite, false);
				PlayerHelper.clearHandler(newFurnace, true);
				
				IKnowledgeProvider oldKnowledge = oldPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
				IAbilityEMCProvider oldAbilityEMC = oldPlayer.getCapability(InventoryExchangeCapabilities.ABILITY_EMC_CAPABILITY);
				IKnowledgeProvider newKnowledge = newPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
				IAbilityEMCProvider newAbilityEMC = newPlayer.getCapability(InventoryExchangeCapabilities.ABILITY_EMC_CAPABILITY);
	
				oldKnowledge.setEmc(BigInteger.valueOf(0));
				oldKnowledge.syncEmc((ServerPlayer) oldPlayer);
				oldAbilityEMC.setEmc(BigInteger.valueOf(0));
				oldAbilityEMC.sync((ServerPlayer) oldPlayer);
				newKnowledge.setEmc(BigInteger.valueOf(0));
				newAbilityEMC.setEmc(BigInteger.valueOf(0));
				PECore.debugLog("Reset knowledge and EMC for {}", newServerPlayer.getName());
			}
			PlayerHelper.syncCapabilities(newServerPlayer);
		}
	}
	
	@SubscribeEvent
	public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
		if (event.getEntity() instanceof Player player && PlayerHelper.hasFullUltimaSet(player)) {
			DamageSource src = event.getSource();
			if (!(src.is(DamageTypes.GENERIC_KILL) || src.is(DamageTypeTags.BYPASSES_INVULNERABILITY))) {
				event.setNewDamage(0.0F);
			}
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
	
	@SubscribeEvent
	public static void onAdvancementEarn(AdvancementEvent.AdvancementEarnEvent event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			AdvancementRewards rewards = event.getAdvancement().value().rewards();
			List<ResourceLocation> recipeIds = rewards.recipes();
			if (recipeIds == null || recipeIds.isEmpty()) return;

			serverPlayer.awardRecipesByKey(recipeIds);
		}
	}
}