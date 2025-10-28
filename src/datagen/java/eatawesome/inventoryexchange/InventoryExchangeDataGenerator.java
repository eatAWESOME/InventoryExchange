package eatawesome.inventoryexchange;

import java.util.concurrent.CompletableFuture;

import eatawesome.inventoryexchange.client.InventoryExchangeItemModelProvider;
import eatawesome.inventoryexchange.client.lang.InventoryExchangeLangProvider;
import eatawesome.inventoryexchange.common.InventoryExchangePackMetadataGenerator;
import eatawesome.inventoryexchange.common.recipe.InventoryExchangeRecipeProvider;
import eatawesome.inventoryexchange.common.tag.InventoryExchangeBlockTagsProvider;
import eatawesome.inventoryexchange.common.tag.InventoryExchangeItemTagsProvider;
import eatawesome.inventoryexchange.utils.text.InventoryExchangeLang;
import moze_intel.projecte.ProjectEDataGenerator;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = InventoryExchange.MODID)
public class InventoryExchangeDataGenerator {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		PackOutput output = gen.getPackOutput();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		CompletableFuture<Provider> lookupProvider = event.getLookupProvider();

		gen.addProvider(true, new InventoryExchangePackMetadataGenerator(output, InventoryExchangeLang.PACK_DESCRIPTION));
		gen.addProvider(event.includeClient(), new InventoryExchangeLangProvider(output));
		gen.addProvider(event.includeClient(), new InventoryExchangeItemModelProvider(output, existingFileHelper));
		InventoryExchangeBlockTagsProvider blockTagsProvider = gen.addProvider(event.includeServer(), new InventoryExchangeBlockTagsProvider(output, lookupProvider, existingFileHelper));
		gen.addProvider(event.includeServer(), new InventoryExchangeItemTagsProvider(output, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
		gen.addProvider(event.includeServer(), new InventoryExchangeRecipeProvider(output, lookupProvider, existingFileHelper));
		
		ProjectEDataGenerator.gatherData(event);
	}
}