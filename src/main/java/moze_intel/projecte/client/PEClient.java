package moze_intel.projecte.client;

import moze_intel.projecte.PECore;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = PECore.MODID, dist = Dist.CLIENT)
public class PEClient {

	public static final ResourceLocation ACTIVE_OVERRIDE = PECore.rl("active");
	public static final ResourceLocation MODE_OVERRIDE = PECore.rl("mode");

	public PEClient(ModContainer container, IEventBus modEventBus) {
		container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
	}
}