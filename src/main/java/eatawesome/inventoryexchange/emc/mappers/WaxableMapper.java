package eatawesome.inventoryexchange.emc.mappers;

import java.util.Map;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.api.mapper.EMCMapper;
import eatawesome.inventoryexchange.api.mapper.IEMCMapper;
import eatawesome.inventoryexchange.api.mapper.collector.IMappingCollector;
import eatawesome.inventoryexchange.api.nss.NSSItem;
import eatawesome.inventoryexchange.api.nss.NormalizedSimpleStack;
import eatawesome.inventoryexchange.config.InventoryExchangeConfigTranslations;
import eatawesome.inventoryexchange.utils.EMCHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Waxable;

@EMCMapper
public class WaxableMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, ReloadableServerResources serverResources,
			RegistryAccess registryAccess, ResourceManager resourceManager) {
		Registry<Block> blocks = registryAccess.registryOrThrow(Registries.BLOCK);
		NSSItem wax = NSSItem.createItem(Items.HONEYCOMB);
		int recipeCount = 0;
		for (Map.Entry<ResourceKey<Block>, Waxable> entry : blocks.getDataMap(NeoForgeDataMaps.WAXABLES).entrySet()) {
			//Add conversions both directions due to scraping
			Block block = blocks.get(entry.getKey());
			if (block != null) {
				NSSItem base = NSSItem.createItem(block);
				NSSItem waxed = NSSItem.createItem(entry.getValue().waxed());
				mapper.addConversion(1, waxed, EMCHelper.intMapOf(
						base, 1,
						wax, 1
				));
				//Scraping the block does not return the wax
				mapper.addConversion(1, base, EMCHelper.intMapOf(waxed, 1));
				recipeCount += 2;
			}
		}
		InventoryExchange.debugLog("{} Statistics:", getName());
		InventoryExchange.debugLog("Found {} Waxable Conversions", recipeCount);
	}

	@Override
	public String getName() {
		return InventoryExchangeConfigTranslations.MAPPING_WAXABLE_MAPPER.title();
	}

	@Override
	public String getTranslationKey() {
		return InventoryExchangeConfigTranslations.MAPPING_WAXABLE_MAPPER.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return InventoryExchangeConfigTranslations.MAPPING_WAXABLE_MAPPER.tooltip();
	}
}