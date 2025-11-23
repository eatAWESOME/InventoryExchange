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
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

@EventBusSubscriber(modid = InventoryExchange.MODID)
public class PlayerEvents {

    // Learn knowledge on item pickup and insert items into infinite container if inventory full or insert items into partial stacks first
    @SubscribeEvent
    public static void onEntityItemPickupPre(ItemEntityPickupEvent.Pre event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
        	ItemEntity itemEntity = event.getItemEntity();
            ItemStack itemStack = itemEntity.getItem();
            PlayerHelper.knowledgeCheck(serverPlayer, itemStack);
            if (!itemStack.isEmpty() && !itemEntity.hasPickUpDelay()) {
                ItemStack remainderStack = PlayerHelper.insertIntoInventoryThenHandler(serverPlayer.getInventory(), InfiniteContainerHelper.getInventory(serverPlayer), itemStack.copy());
                int pickedUpCount = itemStack.getCount() - remainderStack.getCount();
                if (pickedUpCount > 0) {
                    event.setCanPickup(TriState.FALSE);
                    if (remainderStack.isEmpty()) {
                    	event.getItemEntity().discard();
                    	itemStack.setCount(pickedUpCount);
                    }
                    PlayerHelper.playPickupSound(serverPlayer);
                    serverPlayer.awardStat(Stats.ITEM_PICKED_UP.get(itemStack.getItem()), pickedUpCount);
                    serverPlayer.onItemPickup(itemEntity);
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
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerHelper.knowledgeCheck(serverPlayer, event.getCrafting());
        }
    }
    
    // Knowledge from smelting
    @SubscribeEvent
    public static void onItemSmelted(PlayerEvent.ItemSmeltedEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerHelper.knowledgeCheck(serverPlayer, event.getSmelting());
        }
    }
    
    // Knowledge from right click
    @SubscribeEvent
    public static void onRightClickBlock(RightClickBlock event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            Block block = event.getLevel().getBlockState(event.getPos()).getBlock();
            if (block == null) return;
            ItemStack itemStack = new ItemStack(block);
            if (itemStack.isEmpty()) return;
            PlayerHelper.knowledgeCheck(serverPlayer, itemStack);
        }
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
                PlayerHelper.dropAndClearHandler(oldPlayer, InfiniteContainerHelper.getInventory(oldPlayer), false);
                PlayerHelper.dropAndClearHandler(oldPlayer, PortableFurnaceHelper.getInventory(oldPlayer), true);
    
                PlayerHelper.clearHandler(InfiniteContainerHelper.getInventory(newPlayer), false);
                PlayerHelper.clearHandler(PortableFurnaceHelper.getInventory(newPlayer), true);
                
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