package eatawesome.inventoryexchange.events;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.capabilities.IAbilityEMCProvider;
import eatawesome.inventoryexchange.capabilities.InventoryExchangeCapabilities;
import eatawesome.inventoryexchange.gameObjs.container.PortableFurnaceMenu;
import eatawesome.inventoryexchange.utils.PortableFurnaceHelper;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import eatawesome.inventoryexchange.handlers.InternalAbilities;
import eatawesome.inventoryexchange.impl.capability.PortableFurnaceImpl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

@EventBusSubscriber(modid = InventoryExchange.MODID)
public class TickEvents {
	
	private static final Map<UUID, Boolean> wasFlyingMap = new HashMap<>();
	
	@SubscribeEvent
	public static void playerTick(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		
		if (player instanceof ServerPlayer serverPlayer) {
			boolean isFlying = player.getAbilities().flying;
			boolean wasFlying = wasFlyingMap.getOrDefault(player.getUUID(), false);
	
			InternalAbilities.tick(player);
			
			IKnowledgeProvider knowledge = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
			IAbilityEMCProvider abilityEMC = player.getCapability(InventoryExchangeCapabilities.ABILITY_EMC_CAPABILITY);
			
			// Flying via EMC
			if (isFlying) {
				if (abilityEMC.getEmc().intValue() >= 32) {
					int emc = abilityEMC.getEmc().intValue() - 32;
					abilityEMC.setEmc(BigInteger.valueOf(emc));
					abilityEMC.sync(serverPlayer);
				} else if (knowledge.getEmc().intValue() >= 1) {
					int emc = abilityEMC.getEmc().intValue() + 100 - 32;
					abilityEMC.setEmc(BigInteger.valueOf(emc));
					abilityEMC.sync(serverPlayer);
					int totalEmc = knowledge.getEmc().intValue() - 1;
					knowledge.setEmc(BigInteger.valueOf(totalEmc));
					knowledge.syncEmc(serverPlayer);
				}
			}
			if (isFlying && !wasFlying) {
				player.level().playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.HEAL.get(), SoundSource.PLAYERS, 0.8F, 1.0F);
				wasFlyingMap.put(player.getUUID(), isFlying);
			} else if (wasFlying && !isFlying) {
				player.level().playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.UNCHARGE.get(), SoundSource.PLAYERS, 0.8F, 1.0F);
				wasFlyingMap.put(player.getUUID(), isFlying);
			}
		
			// Eating via EMC
			if (player.getFoodData().getFoodLevel() <= 16) {
				if (knowledge.getEmc().intValue() >= 128) {
					player.getFoodData().eat(4, 2.4F);
					player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.8F, 1.0F);
					int emc = knowledge.getEmc().intValue() - 128;
					knowledge.setEmc(BigInteger.valueOf(emc));
					knowledge.syncEmc(serverPlayer);
				}
			}
			
			// Smelting via EMC
			if (!player.isSpectator()) {
		        if (player.containerMenu instanceof PortableFurnaceMenu) return;
	
		        IItemHandlerModifiable handler = new PortableFurnaceImpl(player).getInventory();
		        if (PortableFurnaceHelper.smeltOneIfPossible(player, handler)) {
		            PortableFurnaceHelper.sync(player);
		        }
			}
			
		}
	}
}