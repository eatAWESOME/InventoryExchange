package moze_intel.projecte.events;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.IAlchBagProvider;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.impl.TransmutationOffline;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = PECore.MODID)
public class PlayerEvents {

	// On death or return from end, sync to the client
	@SubscribeEvent
	public static void respawnEvent(PlayerEvent.PlayerRespawnEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			IKnowledgeProvider knowledge = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
			if (knowledge != null) {
				knowledge.sync(player);
			}
			IAlchBagProvider bagProvider = player.getCapability(PECapabilities.ALCH_BAG_CAPABILITY);
			if (bagProvider != null) {
				bagProvider.syncAllBags(player);
			}
		}
	}

	@SubscribeEvent
	public static void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		Player player = event.getEntity();
		if (player instanceof ServerPlayer serverPlayer) {
			// Sync to the client for "normal" interdimensional teleports (nether portal, etc.)
			IKnowledgeProvider knowledge = serverPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
			if (knowledge != null) {
				knowledge.sync(serverPlayer);
			}
			IAlchBagProvider bagProvider = serverPlayer.getCapability(PECapabilities.ALCH_BAG_CAPABILITY);
			if (bagProvider != null) {
				bagProvider.syncAllBags(serverPlayer);
			}
		}
	}

	@SubscribeEvent
	public static void playerConnect(PlayerEvent.PlayerLoggedInEvent event) {
		ServerPlayer player = (ServerPlayer) event.getEntity();
		IKnowledgeProvider knowledge = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
		if (knowledge != null) {
			knowledge.sync(player);
			PlayerHelper.updateScore(player, PlayerHelper.SCOREBOARD_EMC, knowledge.getEmc());
		}

		IAlchBagProvider alchBagProvider = player.getCapability(PECapabilities.ALCH_BAG_CAPABILITY);
		if (alchBagProvider != null) {
			alchBagProvider.syncAllBags(player);
		}

		PECore.debugLog("Sent knowledge and bag data to {}", player.getName());
	}

	@SubscribeEvent
	public static void onConstruct(EntityEvent.EntityConstructing evt) {
		if (EffectiveSide.get().isServer() // No world to check yet
			&& evt.getEntity() instanceof Player && !(evt.getEntity() instanceof FakePlayer)) {
			TransmutationOffline.clear(evt.getEntity().getUUID());
			PECore.debugLog("Clearing offline data cache in preparation to load online data");
		}
	}
}