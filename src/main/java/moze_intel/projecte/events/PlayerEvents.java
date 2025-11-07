package moze_intel.projecte.events;

import moze_intel.projecte.PECore;
import moze_intel.projecte.impl.TransmutationOffline;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.EntityEvent;

@EventBusSubscriber(modid = PECore.MODID)
public class PlayerEvents {

	@SubscribeEvent
	public static void onConstruct(EntityEvent.EntityConstructing evt) {
		if (EffectiveSide.get().isServer() // No world to check yet
			&& evt.getEntity() instanceof Player && !(evt.getEntity() instanceof FakePlayer)) {
			TransmutationOffline.clear(evt.getEntity().getUUID());
			PECore.debugLog("Clearing offline data cache in preparation to load online data");
		}
	}
}