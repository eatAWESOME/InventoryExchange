package eatawesome.inventoryexchange.emc.mappers;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongSortedMaps;
import java.util.Iterator;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.api.mapper.EMCMapper;
import eatawesome.inventoryexchange.api.mapper.IEMCMapper;
import eatawesome.inventoryexchange.api.mapper.collector.IMappingCollector;
import eatawesome.inventoryexchange.api.nss.NSSItem;
import eatawesome.inventoryexchange.api.nss.NormalizedSimpleStack;
import eatawesome.inventoryexchange.config.InventoryExchangeCustomEMCParser;
import eatawesome.inventoryexchange.config.InventoryExchangeConfigTranslations;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;

@EMCMapper(priority = Integer.MIN_VALUE)
public class CustomEMCMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, ReloadableServerResources serverResources,
			RegistryAccess registryAccess, ResourceManager resourceManager) {
		for (Iterator<Object2LongMap.Entry<NSSItem>> iterator = Object2LongSortedMaps.fastIterator(InventoryExchangeCustomEMCParser.currentEntries.entries()); iterator.hasNext(); ) {
			Object2LongMap.Entry<NSSItem> entry = iterator.next();
			NSSItem item = entry.getKey();
			long emc = entry.getLongValue();
			InventoryExchange.debugLog("Adding custom EMC value for {}: {}", item, emc);
			//Note: We set it for each of the values in the tag to make sure it is properly taken into account when calculating the individual EMC values
			item.forSelfAndEachElement(mapper, emc, IMappingCollector::setValueBefore);
		}
	}

	@Override
	public String getName() {
		return InventoryExchangeConfigTranslations.MAPPING_CUSTOM_EMC_MAPPER.title();
	}

	@Override
	public String getTranslationKey() {
		return InventoryExchangeConfigTranslations.MAPPING_CUSTOM_EMC_MAPPER.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return InventoryExchangeConfigTranslations.MAPPING_CUSTOM_EMC_MAPPER.tooltip();
	}
}