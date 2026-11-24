package eatawesome.inventoryexchange.integration.projecte;

import java.util.Iterator;

import eatawesome.inventoryexchange.api.mapper.EMCMapper;
import eatawesome.inventoryexchange.api.mapper.IEMCMapper;
import eatawesome.inventoryexchange.api.mapper.collector.IMappingCollector;
import eatawesome.inventoryexchange.api.nss.NSSItem;
import eatawesome.inventoryexchange.api.nss.NormalizedSimpleStack;
import eatawesome.inventoryexchange.config.InventoryExchangeConfigTranslations;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongSortedMaps;
import moze_intel.projecte.config.CustomEMCParser;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;

@EMCMapper(requiredMods = "projecte", priority = Integer.MIN_VALUE + 1)
public class ProjectEEMCMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, ReloadableServerResources serverResources, RegistryAccess registryAccess, ResourceManager resourceManager) {
		CustomEMCParser.init(registryAccess);

		for (Iterator<Object2LongMap.Entry<moze_intel.projecte.api.nss.NSSItem>> iterator =
				Object2LongSortedMaps.fastIterator(CustomEMCParser.currentEntries.entries()); iterator.hasNext();) {
			Object2LongMap.Entry<moze_intel.projecte.api.nss.NSSItem> entry = iterator.next();
			moze_intel.projecte.api.nss.NSSItem projectEItem = entry.getKey();

			NSSItem item = projectEItem.representsTag()
					? NSSItem.createTag(projectEItem.getResourceLocation())
					: NSSItem.createItem(
							projectEItem.getResourceLocation(), projectEItem.getComponentsPatch());

			item.forSelfAndEachElement(mapper, entry.getLongValue(),
					(collector, stack, value) -> collector.setValueBefore(stack, value));
		}
	}

	@Override
	public String getName() {
		return InventoryExchangeConfigTranslations.MAPPING_PROJECTE_MAPPER.title();
	}

	@Override
	public String getTranslationKey() {
		return InventoryExchangeConfigTranslations.MAPPING_PROJECTE_MAPPER.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return InventoryExchangeConfigTranslations.MAPPING_PROJECTE_MAPPER.tooltip();
	}
}