package eatawesome.inventoryexchange.config;

import eatawesome.inventoryexchange.InventoryExchange;
import net.minecraft.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum InventoryExchangeConfigTranslations implements IConfigTranslation {
	// Commmon Config
	COMMON_HEADER("common_header", InventoryExchange.MODNAME + " Common Config", ""),
	INFINITE_CONTAINER_MAX_ROWS("infinite_container_max_rows", "Infinite Container Max Rows", "Maximum number of rows for the infinite container. Minimum 10."),
	ENABLE_DEBUG_LOGS("enable_debug_logs", "Enable Debug Logs", "If true, enables debug logs."),
	ALLOW_ULTIMA_WATCH("allow_ultima_watch", "Allow Ultima Watch", "If true, allows use of the Ultima Watch."),
	COMMON_DEBUG_LOGGING("common.debug_logging", "Debug Logging", "Enable more verbose debug logging."),

	// Client Config
	CLIENT_HEADER("client_header", InventoryExchange.MODNAME + " Client Config", ""),
	LIMIT_CONDENSING("limit_condensing", "Limit Condensing", "If true, disables inventory condensing once there are no more condensable items remaining."),
	LIMIT_FEEDING("limit_feeding", "Limit Feeding", "If true, disables feeding once healing is possible."),
	LIMIT_REPAIRING("limit_repairing", "Limit Repairing", "If true, disables repairing once there are no more repairable items remaining."),
	LIMIT_SMELTING("limit_smelting", "Limit Smelting", "If true, disables inventory smelting once there are no more smeltable items remaining."),
	CLIENT_TOOLTIPS("client.tooltips", "Tooltip Settings", "Settings for configuring Tooltips provided by InventoryExchange.", true),
	CLIENT_TOOLTIPS_EMC("client.tooltips.emc", "EMC Tooltips", "Show the EMC value as a tooltip on items and blocks."),
	CLIENT_TOOLTIPS_EMC_SHIFT("client.tooltips.emc.shift", "Shift EMC Tooltips",
			"Requires holding shift to display the EMC value as a tooltip on items and blocks. Note: this does nothing if EMC Tooltips are disabled."),
	CLIENT_TOOLTIPS_TAGS("client.tooltips.tags", "Tag Tooltips", "Show item tags in tooltips (useful for custom EMC registration)."),
	
	//EMC Mapping
	MAPPING_DUMP_TO_FILE("mapping.dump_to_file", "Dump Everything To File",
			"Want to take a look at the internals of EMC Calculation? Enable this to write all the conversions and setValue-Commands to config/InventoryExchange/mapping_dump.json"),
	MAPPING_PREGENERATED("mapping.pregenerated", "Pregenerate EMC",
			"When the next EMC mapping occurs write the results to config/InventoryExchange/pregenerated_emc.json and only ever run the mapping again when that file "
			+ "does not exist, this setting is set to false, or an error occurred parsing that file."),
	MAPPING_LOG_EXPLOITS("mapping.log_exploits", "Log Known EMC Exploits",
			"Logs known EMC Exploits. This can not and will not find all possible exploits. This will only find exploits that result in fixed/custom emc values "
			+ "that the algorithm did not overwrite. Exploits that derive from conversions that are unknown to InventoryExchange will not be found."),

	MAPPING_MAPPERS("mapping.mapper", "EMC Mappers", "Used to configure settings for the various EMC Mappers.", true),

	MAPPING_BREWING_MAPPER("mapping.mapper.brewing", "Brewing Mapper", "Add Conversions for Brewing Recipes.", true),
	MAPPING_OXIDATION_MAPPER("mapping.mapper.oxidation", "Oxidization Mapper", "Add Conversions for all oxidizable blocks.", true),
	MAPPING_TAG_MAPPER("mapping.mapper.tag", "Tag Mapper",
			"Adds back and forth conversions of objects and their Tag variant. (EMC values assigned to tags will not behave properly if this mapper is disabled)",
			"Edit Tags"),
	MAPPING_WAXABLE_MAPPER("mapping.mapper.waxable", "Waxable Mapper", "Add Conversions for all waxable blocks", true),

	MAPPING_CUSTOM_CONVERSION_MAPPER("mapping.mapper.custom.conversion", "Custom Conversion Mapper",
			"Loads json files within datapacks (data/<domain>/pe_custom_conversions/*.json) to add values and conversions.", "Edit Custom Conversions"),
	MAPPING_CUSTOM_EMC_MAPPER("mapping.mapper.custom.emc", "Custom EMC Mapper", "Uses the `custom_emc.json` File to add EMC values.", "Edit Custom EMC"),

	MAPPING_CRT_CONVERSION_MAPPER("mapping.mapper.crt.conversion", "CrT Conversion EMC Mapper",
			"Allows adding custom conversions through CraftTweaker. This behaves similarly to if someone used a custom conversion file instead.",
			"Edit CrT Conversion"),
	MAPPING_CRT_EMC_MAPPER("mapping.mapper.crt.emc", "CrT Custom EMC Mapper",
			"Allows setting EMC values through CraftTweaker. This behaves similarly to if someone used the custom emc file instead.", "Edit CrT Custom EMC"),

	MAPPING_BLACKLIST_ORE_MAPPER("mapping.mapper.blacklist.ore", "Ore Blacklist Mapper", "Set EMC=0 for everything in the c:ores tag.",
			"Edit Ore Blacklist"),
	MAPPING_BLACKLIST_RAW_ORE_MAPPER("mapping.mapper.blacklist.raw_ore", "Raw Ore Blacklist Mapper",
			"Set EMC=0 for everything in the c:raw_materials tag.", "Edit Raw Ores Blacklist"),

	MAPPING_CRAFTING_MAPPER("mapping.mapper.crafting", "Crafting Mapper",
			"Add Conversions for Crafting Recipes gathered from net.minecraft.world.item.crafting.RecipeManager", true),
	MAPPING_CRAFTING_MAPPER_VANILLA("mapping.mapper.crafting.vanilla", "Vanilla Recipe Types", "Maps the different vanilla recipe types.", true),
	MAPPING_CRAFTING_MAPPER_SMITHING("mapping.mapper.crafting.smithing", "Smithing", "Maps smithing recipes.", true),
	MAPPING_CRAFTING_MAPPER_FALLBACK("mapping.mapper.crafting.fallback", "Fallback",
			"Fallback for default handling of recipes that extend ICraftingRecipe, AbstractCookingRecipe, SingleItemRecipe, or SmithingRecipe. "
			+ "This will catch modded extensions of the vanilla recipe classes, and if the VanillaRecipeTypes mapper is disabled, "
			+ "this mapper will still catch the vanilla recipes.", true),

	MAPPING_CRAFTING_MAPPER_MARK_HANDLED("mapping.mapper.crafting.mark_handled", "Mark Special Recipes Handled",
			"This mapper does not actually map anything, but instead just marks recipe classes as having been handled, if we either expose a processor for them, "
			+ "or they are self referencing (for example map cloning recipes).", "Edit Special Recipes"),
	MAPPING_CRAFTING_MAPPER_SHULKER_RECOLORING("mapping.mapper.crafting.shulker_recoloring", "Shulker Recoloring Mapper",
			"Propagates shulker box values to colored variants.", "Edit Shulker Recoloring"),
	MAPPING_CRAFTING_MAPPER_TIPPED_ARROW("mapping.mapper.crafting.tipped_arrow", "Tipped Arrow Mapper",
			"Add conversions for all lingering potions to arrow recipes.", "Edit Tipped Arrows"),
	MAPPING_CRAFTING_MAPPER_DECORATED_POT("mapping.mapper.crafting.decorated_pot", "Decorated Pot Mapper",
			"Adds conversions for all the different decorated pot combinations.", "Edit Decorated Pots"),
	MAPPING_CRAFTING_MAPPER_SUSPICIOUS_STEW("mapping.mapper.crafting.suspicious_stew", "Suspicious Stew Mapper",
			"Adds conversions for all the different types of suspicious stews.", "Edit Suspicious Stew"),

	MAPPING_MAPPER_ENABLED("mapping.mapper.enabled", "Enabled", "Determines whether this EMC Mapper is enabled."),
	MAPPING_RECIPE_TYPE_MAPPER_ENABLED("mapping.mapper.recipe_type.enabled", "Enabled", "Determines whether this Recipe Type Mapper is enabled."),
	MAPPING_PIXELMON_MAPPER("mapping.mapper.pixelmon.enabled", "Pixelmon Mapper", "Adds Pixelmon item EMC values if pixelmon is installed.", "Edit Pixelmon"),
	
	//Data Component Processors
	MAPPING_PROCESSORS("mapping.processors", "Data Component Processors", "Used to configure settings for the various Data Component Processors.",
			"Edit Processors"),

	DCP_ARMOR_TRIM("processing.data_component_processor.armor_trim", "Armor Trim Processor", "Calculates EMC value of trimmed armor.", true),
	DCP_BANNERS("processing.data_component_processor.banner", "Banner Processor", "Calculates EMC value of patterned banners.", true),
	DCP_BUNDLE("processing.data_component_processor.bundle", "Bundle Processor", "Calculates EMC value of items stored in bundles.", true),
	DCP_CONTAINER("processing.data_component_processor.container", "Container Processor", "Calculates EMC value of items stored in vanilla's container component. For example shulker boxes.", true),
	DCP_DAMAGE("processing.data_component_processor.damage", "Damage Processor", "Reduces the EMC value the more damaged an item is.", true),
	DCP_DECORATED_POT("processing.data_component_processor.decorated_pot", "Decorated Pot Processor",
			"Takes the different sherds into account for each decorated pot.", "Edit Pot Processor"),
	DCP_DECORATED_SHIELD("processing.data_component_processor.decorated_shield", "Decorated Shield Processor", "Calculates EMC value of decorated shield.",
			"Edit Shield Processor"),
	DCP_ENCHANTMENT("processing.data_component_processor.enchantment", "Enchantment Processor",
			"Increases the EMC value to take into account any enchantments on an item.", true),
	DCP_ENCHANTMENT_EMC_BONUS("processing.data_component_processor.enchantment.emc_bonus", "Enchantment EMC Bonus",
			"The amount (scaled by rarity) to increase EMC value by for enchantments."),

	DCP_FIREWORK("processing.data_component_processor.firework", "Firework Processor", "Calculates EMC value of fireworks.", true),
	DCP_FIREWORK_STAR("processing.data_component_processor.firework_star", "Firework Star Processor",
			"Calculates the EMC value of Firework Stars, based on what was required to craft them.", "Edit Star Processor"),
	DCP_MAP_EXTENSION("processing.data_component_processor.map_extension", "Map Extension Processor",
			"Increases the EMC value of maps that have had their range extended.", "Edit Map Ext. Processor"),
	DCP_MERCURIAL_EYE("processing.data_component_processor.mercurial_eye", "Mercurial Eye Processor",
			"Factors in the EMC value of Klein Stars stored in Mercurial eyes.", "Edit Eye Processor"),
	DCP_STORED_EMC("processing.data_component_processor.stored_emc", "Stored EMC Processor",
			"Increases the EMC value of the item to take into account any EMC the item has stored.", true),
	DCP_WRITABLE_BOOK("processing.data_component_processor.writable_book", "Writable Book Processor",
			"Allows persisting the contents of writable books. Does not change the EMC value.", "Edit Writable Books"),
	DCP_WRITTEN_BOOK("processing.data_component_processor.written_book", "Written Book Processor",
			"Allows persisting the contents of written books. Does not change the EMC value.", true),

	DCP_ENABLED("processing.enabled", "Enabled", "Determines whether this Data Component Processor is enabled and can adjust the EMC value of items."),
	DCP_PERSISTENT("processing.persistent", "Persistent",
			"Determines whether this Data Component Processor can affect the persistent data that gets saved to knowledge/copied in a condenser.")
	
	;
	
	private final String key;
	private final String title;
	private final String tooltip;
	@Nullable
	private final String button;

	InventoryExchangeConfigTranslations(String path, String title, String tooltip) {
		this(path, title, tooltip, false);
	}

	InventoryExchangeConfigTranslations(String path, String title, String tooltip, boolean isSection) {
		this(path, title, tooltip, IConfigTranslation.getSectionTitle(title, isSection));
	}

	InventoryExchangeConfigTranslations(String path, String title, String tooltip, @Nullable String button) {
		this.key = Util.makeDescriptionId("configuration", InventoryExchange.rl(path));
		this.title = title;
		this.tooltip = tooltip;
		this.button = button;
	}

	@NotNull
	@Override
	public String getTranslationKey() {
		return key;
	}

	@Override
	public String title() {
		return title;
	}

	@Override
	public String tooltip() {
		return tooltip;
	}

	@Nullable
	@Override
	public String button() {
		return button;
	}
}