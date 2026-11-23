package eatawesome.inventoryexchange.events;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.api.capabilities.IAbilityEMCProvider;
import eatawesome.inventoryexchange.api.capabilities.IKnowledgeProvider;
import eatawesome.inventoryexchange.api.capabilities.InventoryExchangeCapabilities;
import eatawesome.inventoryexchange.gameObjs.container.PortableFurnaceMenu;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeSoundEvents;
import eatawesome.inventoryexchange.utils.CondensingToggle;
import eatawesome.inventoryexchange.utils.FeedingToggle;
import eatawesome.inventoryexchange.utils.PlayerHelper;
import eatawesome.inventoryexchange.utils.PortableFurnaceHelper;
import eatawesome.inventoryexchange.utils.RepairingToggle;
import eatawesome.inventoryexchange.utils.SmeltingToggle;
import eatawesome.inventoryexchange.handlers.InternalAbilities;
import eatawesome.inventoryexchange.impl.capability.PortableFurnaceImpl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = InventoryExchange.MODID)
public class TickEvents {
	
	private static final Map<UUID, Boolean> wasFlyingMap = new HashMap<>();
	private static final int FLYING_COST = 32;
	private static final int FEED_COST = 128;
	private static final int FEED_POINTS = 4;
	private static final float FEED_SATURATION = 2.4F;
	
	
	@SubscribeEvent
	public static void onPlayerTickPre(PlayerTickEvent.Pre event) {
		Player player = event.getEntity();
		boolean full = PlayerHelper.hasFullUltimaSet(player);
		if (full) {
			PlayerHelper.updateUltimaEffects(player);
		}
	}
	
	@SubscribeEvent
	public static void onPlayerTickPost(PlayerTickEvent.Post event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			boolean isFlying = serverPlayer.getAbilities().flying;
			boolean wasFlying = wasFlyingMap.getOrDefault(serverPlayer.getUUID(), false);
	
			InternalAbilities.tick(serverPlayer);
			
			IKnowledgeProvider knowledge = serverPlayer.getCapability(InventoryExchangeCapabilities.KNOWLEDGE_CAPABILITY);
			IAbilityEMCProvider abilityEMC = serverPlayer.getCapability(InventoryExchangeCapabilities.ABILITY_EMC_CAPABILITY);
			
			// Flying via EMC
			if (isFlying && !serverPlayer.isCreative() && !serverPlayer.isSpectator()) {
				if (abilityEMC.getEmc().intValue() >= FLYING_COST) {
					int emc = abilityEMC.getEmc().intValue() - FLYING_COST;
					abilityEMC.setEmc(BigInteger.valueOf(emc));
					abilityEMC.sync(serverPlayer);
				} else if (knowledge.getEmc().intValue() >= 1) {
					int emc = abilityEMC.getEmc().intValue() + 100 - FLYING_COST;
					abilityEMC.setEmc(BigInteger.valueOf(emc));
					abilityEMC.sync(serverPlayer);
					int totalEmc = knowledge.getEmc().intValue() - 1;
					knowledge.setEmc(BigInteger.valueOf(totalEmc));
					knowledge.syncEmc(serverPlayer);
				}
			}
			if (isFlying && !wasFlying) {
				serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), InventoryExchangeSoundEvents.HEAL.get(), SoundSource.PLAYERS, 0.8F, 1.0F);
				wasFlyingMap.put(serverPlayer.getUUID(), isFlying);
			} else if (wasFlying && !isFlying) {
				serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), InventoryExchangeSoundEvents.UNCHARGE.get(), SoundSource.PLAYERS, 0.8F, 1.0F);
				wasFlyingMap.put(serverPlayer.getUUID(), isFlying);
			}
		
			// Inventory Condensing
			if (CondensingToggle.isEnabled(serverPlayer)) {
				boolean condensingSuccess = PlayerHelper.condenseFirstItem(serverPlayer);
				if (!condensingSuccess && CondensingToggle.isLimited(serverPlayer)) {
					CondensingToggle.setComplete(serverPlayer);
				}
			}
		
			// Eating via EMC
			if (FeedingToggle.isEnabled(serverPlayer)) {
				if (serverPlayer.getFoodData().getFoodLevel() <= 16) {
					if (!serverPlayer.isCreative() && !serverPlayer.isSpectator()) {
						if (knowledge.getEmc().intValue() >= FEED_COST) {
							serverPlayer.getFoodData().eat(FEED_POINTS, FEED_SATURATION);
							serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.8F, 1.0F);
							int emc = knowledge.getEmc().intValue() - FEED_COST;
							knowledge.setEmc(BigInteger.valueOf(emc));
							knowledge.syncEmc(serverPlayer);
						}
					}
				} else if (FeedingToggle.isLimited(serverPlayer)) {
					FeedingToggle.setComplete(serverPlayer);
				}
			}
			
			// Repairing via EMC
			if (RepairingToggle.isEnabled(serverPlayer)) {
				double availableEMC = (double) knowledge.getEmc().intValue() + ((double) abilityEMC.getEmc().intValue()) / 100;
				double spentEMC = 0;

				for (ItemStack itemStack : serverPlayer.getInventory().items) spentEMC += PlayerHelper.repairItem(serverPlayer, itemStack, availableEMC - spentEMC);
				for (ItemStack itemStack : serverPlayer.getInventory().armor) spentEMC += PlayerHelper.repairItem(serverPlayer, itemStack, availableEMC - spentEMC);
				for (ItemStack itemStack : serverPlayer.getInventory().offhand) spentEMC += PlayerHelper.repairItem(serverPlayer, itemStack, availableEMC - spentEMC);

				if (spentEMC > 0) {
					int totalEmc = (int) Math.floor(availableEMC - spentEMC);
					int emc = (int) Math.floor((availableEMC - spentEMC - totalEmc) * 100);
					knowledge.setEmc(BigInteger.valueOf(totalEmc));
					knowledge.syncEmc(serverPlayer);
					abilityEMC.setEmc(BigInteger.valueOf(emc));
					abilityEMC.sync(serverPlayer);
				} else if (RepairingToggle.isLimited(serverPlayer)) {
					if (!PlayerHelper.hasRepairableItem(serverPlayer)) {
						RepairingToggle.setComplete(serverPlayer);
					}
				}
			}
			
			// Smelting via EMC
			if (!serverPlayer.isSpectator()) {
		        if (!(serverPlayer.containerMenu instanceof PortableFurnaceMenu)) {
		        	// Portable Furnace Smelting
			        PortableFurnaceHelper.smeltOneIfPossible(serverPlayer, new PortableFurnaceImpl(serverPlayer).getInventory());
			        
			        // Auto Smelting in Inventory
			        if (SmeltingToggle.isEnabled(serverPlayer) && !serverPlayer.isCreative()) {
			        	boolean smeltingSuccess = PortableFurnaceHelper.smeltOneIfPossible(serverPlayer);
						if (!smeltingSuccess && SmeltingToggle.isLimited(serverPlayer)) {
							SmeltingToggle.setComplete(serverPlayer);
						}
			        }
		        }
			}
		}
	}
}