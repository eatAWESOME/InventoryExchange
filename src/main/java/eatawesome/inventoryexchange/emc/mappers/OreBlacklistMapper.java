package eatawesome.inventoryexchange.emc.mappers;

import java.util.Optional;

import eatawesome.inventoryexchange.api.mapper.EMCMapper;
import eatawesome.inventoryexchange.api.mapper.IEMCMapper;
import eatawesome.inventoryexchange.api.mapper.collector.IMappingCollector;
import eatawesome.inventoryexchange.api.nss.NSSItem;
import eatawesome.inventoryexchange.api.nss.NormalizedSimpleStack;
import eatawesome.inventoryexchange.config.InventoryExchangeConfigTranslations;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;

@EMCMapper
public class OreBlacklistMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@EMCMapper.Instance
	public static final OreBlacklistMapper INSTANCE = new OreBlacklistMapper();

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, ReloadableServerResources serverResources,
			RegistryAccess registryAccess, ResourceManager resourceManager) {
		Optional<Named<Item>> tag = BuiltInRegistries.ITEM.getTag(Tags.Items.ORES);
		if (tag.isPresent()) {
			for (Holder<Item> holder : tag.get()) {
				NSSItem ore = NSSItem.createItem(holder);
				mapper.setValueBefore(ore, 0L);
				mapper.setValueAfter(ore, 0L);
			}
		}
	}

	@Override
	public String getName() {
		return InventoryExchangeConfigTranslations.MAPPING_BLACKLIST_ORE_MAPPER.title();
	}

	@Override
	public String getTranslationKey() {
		return InventoryExchangeConfigTranslations.MAPPING_BLACKLIST_ORE_MAPPER.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return InventoryExchangeConfigTranslations.MAPPING_BLACKLIST_ORE_MAPPER.tooltip();
	}
}