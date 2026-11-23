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
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Oxidizable;

@EMCMapper
public class OxidizationMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, ReloadableServerResources serverResources,
			RegistryAccess registryAccess, ResourceManager resourceManager) {
		Registry<Block> blocks = registryAccess.registryOrThrow(Registries.BLOCK);
		int recipeCount = 0;
		for (Map.Entry<ResourceKey<Block>, Oxidizable> entry : blocks.getDataMap(NeoForgeDataMaps.OXIDIZABLES).entrySet()) {
			//Add conversions both directions due to scraping
			Block block = blocks.get(entry.getKey());
			if (block != null) {
				NSSItem unweathered = NSSItem.createItem(block);
				NSSItem weathered = NSSItem.createItem(entry.getValue().nextOxidationStage());
				mapper.addConversion(1, weathered, EMCHelper.intMapOf(unweathered, 1));
				mapper.addConversion(1, unweathered, EMCHelper.intMapOf(weathered, 1));
				recipeCount += 2;
			}
		}
		InventoryExchange.debugLog("{} Statistics:", getName());
		InventoryExchange.debugLog("Found {} Oxidizable Conversions", recipeCount);
	}

	@Override
	public String getName() {
		return InventoryExchangeConfigTranslations.MAPPING_OXIDATION_MAPPER.title();
	}

	@Override
	public String getTranslationKey() {
		return InventoryExchangeConfigTranslations.MAPPING_OXIDATION_MAPPER.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return InventoryExchangeConfigTranslations.MAPPING_OXIDATION_MAPPER.tooltip();
	}
}