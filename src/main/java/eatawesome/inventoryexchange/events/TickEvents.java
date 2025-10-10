package eatawesome.inventoryexchange.events;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.capabilities.IAbilityEMCProvider;
import eatawesome.inventoryexchange.capabilities.InventoryExchangeCapabilities;
import eatawesome.inventoryexchange.utils.PlayerHelper;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import eatawesome.inventoryexchange.handlers.InternalAbilities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = InventoryExchange.MODID)
public class TickEvents {
	
	private static final Map<UUID, Boolean> wasFlyingMap = new HashMap<>();
	
	@SubscribeEvent
	public static void playerTick(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		
		boolean isFlying = player.getAbilities().flying;
		boolean wasFlying = wasFlyingMap.getOrDefault(player.getUUID(), false);

		InternalAbilities.tick(player);
		if (isFlying && player instanceof ServerPlayer serverPlayer) {
			IKnowledgeProvider knowledge = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
			IAbilityEMCProvider abilityEMC = player.getCapability(InventoryExchangeCapabilities.ABILITY_EMC_CAPABILITY);
			if (abilityEMC.getEmc().intValue() >= 32) {
				int emc = abilityEMC.getEmc().intValue() - 32;
				abilityEMC.setEmc(BigInteger.valueOf(emc));
				abilityEMC.sync(serverPlayer);
				PlayerHelper.updateScore(serverPlayer, PlayerHelper.SCOREBOARD_ABILITY_EMC, emc);
			} else if (knowledge.getEmc().intValue() >= 1) {
				int emc = abilityEMC.getEmc().intValue() + 100 - 32;
				abilityEMC.setEmc(BigInteger.valueOf(emc));
				abilityEMC.sync(serverPlayer);
				PlayerHelper.updateScore(serverPlayer, PlayerHelper.SCOREBOARD_ABILITY_EMC, emc);
				int totalEmc = knowledge.getEmc().intValue() - 1;
				knowledge.setEmc(BigInteger.valueOf(totalEmc));
				knowledge.syncEmc(serverPlayer);
				PlayerHelper.updateScore(serverPlayer, PlayerHelper.SCOREBOARD_EMC, totalEmc);
			}
		}

		if (!player.level().isClientSide) {
			if (isFlying && !wasFlying) {
				player.level().playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.HEAL.get(), SoundSource.PLAYERS, 0.8F, 1.0F);
				wasFlyingMap.put(player.getUUID(), isFlying);
			} else if (wasFlying && !isFlying) {
				player.level().playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.UNCHARGE.get(), SoundSource.PLAYERS, 0.8F, 1.0F);
				wasFlyingMap.put(player.getUUID(), isFlying);
			}
		}
	}
}