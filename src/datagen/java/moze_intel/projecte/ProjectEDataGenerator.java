package moze_intel.projecte;

import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.client.sound.PESoundProvider;
import moze_intel.projecte.common.PECustomConversionProvider;
import moze_intel.projecte.common.PEDatapackRegistryProvider;
import moze_intel.projecte.emc.EMCMappingHandler;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

//@EventBusSubscriber(modid = PECore.MODID)
public class ProjectEDataGenerator {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		//Ensure that we register the configs for data component processors and the like
		EMCMappingHandler.loadMappers();

		DataGenerator gen = event.getGenerator();
		PackOutput output = gen.getPackOutput();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		PEDatapackRegistryProvider drProvider = gen.addProvider(event.includeServer(), new PEDatapackRegistryProvider(output, event.getLookupProvider()));
		CompletableFuture<Provider> lookupProvider = drProvider.getRegistryProvider();

		gen.addProvider(event.includeClient(), new PESoundProvider(output, existingFileHelper));
		gen.addProvider(event.includeServer(), new PECustomConversionProvider(output, lookupProvider));
	}
}