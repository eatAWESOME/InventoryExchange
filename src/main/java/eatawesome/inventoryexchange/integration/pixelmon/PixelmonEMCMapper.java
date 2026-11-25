package eatawesome.inventoryexchange.integration.pixelmon;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;

import eatawesome.inventoryexchange.api.mapper.EMCMapper;
import eatawesome.inventoryexchange.api.mapper.IEMCMapper;
import eatawesome.inventoryexchange.api.mapper.collector.IMappingCollector;
import eatawesome.inventoryexchange.api.nss.NSSItem;
import eatawesome.inventoryexchange.config.InventoryExchangeConfigTranslations;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.common.ModConfigSpec;

@EMCMapper(requiredMods = "pixelmon", priority = -1)
public class PixelmonEMCMapper implements IEMCMapper {

	private ModConfigSpec.BooleanValue includePokeballs;
	private ModConfigSpec.BooleanValue includeCandy;
	
	@Override
	public String getName() {
		return InventoryExchangeConfigTranslations.MAPPING_PIXELMON_MAPPER.title();
	}

	@Override
	public String getTranslationKey() {
		return InventoryExchangeConfigTranslations.MAPPING_PIXELMON_MAPPER.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return InventoryExchangeConfigTranslations.MAPPING_PIXELMON_MAPPER.tooltip();
	}
	
	@Override
	public void addConfigOptions(ModConfigSpec.Builder builder) {
		includePokeballs = InventoryExchangeConfigTranslations.MAPPING_PIXELMON_INCLUDE_POKEBALLS
				.applyToBuilder(builder)
				.define("include_pokeballs", false);

		includeCandy = InventoryExchangeConfigTranslations.MAPPING_PIXELMON_INCLUDE_CANDY
				.applyToBuilder(builder)
				.define("include_candy", false);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void addMappings(IMappingCollector collector, ReloadableServerResources serverResources, RegistryAccess registryAccess, ResourceManager resourceManager) {
		//Cooked Apricorns
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "cooked_red_apricorn")), 32L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "cooked_yellow_apricorn")), 32L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "cooked_blue_apricorn")), 32L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "cooked_green_apricorn")), 32L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "cooked_pink_apricorn")), 32L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "cooked_white_apricorn")), 32L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "cooked_black_apricorn")), 32L);
		//Evolution Stones
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
		//Raw Materials
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "crystal")), 128L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "ruby")), 1_024L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "sapphire")), 1_024L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "aluminum_ingot")), 256L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "platinum_ingot")), 4_096L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "silver_ingot")), 2_048L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "tumblestone")), 256L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "sky_tumblestone")), 256L);
		collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "black_tumblestone")), 256L);
		//Pokeballs
		if (includePokeballs.get()) {
			collector.setValueBefore(NSSItem.CODEC.codec().parse(RegistryOps.create(JsonOps.INSTANCE, registryAccess), JsonParser
					.parseString("{\"id\":\"pixelmon:poke_ball\",\"data\":{\"pixelmon:poke_ball\":\"beast_ball\"}}")).getOrThrow(), 10_000L);
			collector.setValueBefore(NSSItem.CODEC.codec().parse(RegistryOps.create(JsonOps.INSTANCE, registryAccess), JsonParser
					.parseString("{\"id\":\"pixelmon:poke_ball\",\"data\":{\"pixelmon:poke_ball\":\"cherish_ball\"}}")).getOrThrow(), 10_000L);
			collector.setValueBefore(NSSItem.CODEC.codec().parse(RegistryOps.create(JsonOps.INSTANCE, registryAccess), JsonParser
					.parseString("{\"id\":\"pixelmon:poke_ball\",\"data\":{\"pixelmon:poke_ball\":\"christmas_ball\"}}")).getOrThrow(), 256L);
			collector.setValueBefore(NSSItem.CODEC.codec().parse(RegistryOps.create(JsonOps.INSTANCE, registryAccess), JsonParser
					.parseString("{\"id\":\"pixelmon:poke_ball\",\"data\":{\"pixelmon:poke_ball\":\"gs_ball\"}}")).getOrThrow(), 2_500_000L);
			collector.setValueBefore(NSSItem.CODEC.codec().parse(RegistryOps.create(JsonOps.INSTANCE, registryAccess), JsonParser
					.parseString("{\"id\":\"pixelmon:poke_ball\",\"data\":{\"pixelmon:poke_ball\":\"master_ball\"}}")).getOrThrow(), 5_000_000L);
			collector.setValueBefore(NSSItem.CODEC.codec().parse(RegistryOps.create(JsonOps.INSTANCE, registryAccess), JsonParser
					.parseString("{\"id\":\"pixelmon:poke_ball\",\"data\":{\"pixelmon:poke_ball\":\"origin_ball\"}}")).getOrThrow(), 5_000_000L);
			collector.setValueBefore(NSSItem.CODEC.codec().parse(RegistryOps.create(JsonOps.INSTANCE, registryAccess), JsonParser
					.parseString("{\"id\":\"pixelmon:poke_ball\",\"data\":{\"pixelmon:poke_ball\":\"park_ball\"}}")).getOrThrow(), 5_000_000L);
			collector.setValueBefore(NSSItem.CODEC.codec().parse(RegistryOps.create(JsonOps.INSTANCE, registryAccess), JsonParser
					.parseString("{\"id\":\"pixelmon:poke_ball\",\"data\":{\"pixelmon:poke_ball\":\"strange_ball\"}}")).getOrThrow(), 10_000L);
		}
		//Candy
		if (includeCandy.get()) {
			collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "xs_exp_candy")), 100L);
			collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "s_exp_candy")), 800L);
			collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "m_exp_candy")), 3_000L);
			collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "l_exp_candy")), 10_000L);
			collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "xl_exp_candy")), 30_000L);
			collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "rare_candy")), 100_000L);
			collector.setValueBefore(NSSItem.createItem(ResourceLocation.fromNamespaceAndPath("pixelmon", "dynamax_candy")), 2_048L);
		}
	}
}