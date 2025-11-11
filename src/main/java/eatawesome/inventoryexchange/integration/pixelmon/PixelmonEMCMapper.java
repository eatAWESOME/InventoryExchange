package eatawesome.inventoryexchange.integration.pixelmon;

import eatawesome.inventoryexchange.config.InventoryExchangeConfigTranslations;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NSSItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;

@EMCMapper(requiredMods = "pixelmon", priority = -1)
public class PixelmonEMCMapper implements IEMCMapper {

	@Override
	public String getName() {
		return InventoryExchangeConfigTranslations.MAPPING_PIXELMON_EMC_MAPPER.title();
	}

	@Override
	public String getTranslationKey() {
		return InventoryExchangeConfigTranslations.MAPPING_PIXELMON_EMC_MAPPER.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return InventoryExchangeConfigTranslations.MAPPING_PIXELMON_EMC_MAPPER.tooltip();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addMappings(IMappingCollector collector, ReloadableServerResources serverResources, RegistryAccess registryAccess, ResourceManager resourceManager) {
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "cooked_red_apricorn")), 32L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "cooked_yellow_apricorn")), 32L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "cooked_blue_apricorn")), 32L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "cooked_green_apricorn")), 32L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "cooked_pink_apricorn")), 32L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "cooked_white_apricorn")), 32L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "cooked_black_apricorn")), 32L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "fire_stone_shard")), 1_024L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "water_stone_shard")), 1_024L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "moon_stone_shard")), 1_024L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "thunder_stone_shard")), 1_024L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "leaf_stone_shard")), 1_024L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "sun_stone_shard")), 1_024L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "dawn_stone_shard")), 1_024L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "dusk_stone_shard")), 1_024L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "ice_stone_shard")), 1_024L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "shiny_stone_shard")), 1_024L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "ruby")), 1_024L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "sapphire")), 1_024L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "aluminum_ingot")), 256L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "platinum_ingot")), 4_096L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "silver_ingot")), 2_048L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "tumblestone")), 256L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "sky_tumblestone")), 256L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "black_tumblestone")), 256L);
		
	}
}