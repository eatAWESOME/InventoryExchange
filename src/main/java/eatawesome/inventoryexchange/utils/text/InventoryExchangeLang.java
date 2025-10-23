package eatawesome.inventoryexchange.utils.text;

import eatawesome.inventoryexchange.InventoryExchange;
import net.minecraft.Util;

public enum InventoryExchangeLang implements ILangEntry {
	// Container
	INFINITE_CONTAINER_CONTAINER("container", "infinite_container"),
	INFINITE_CONTAINER_SORT("container", "infinite_container_sort"),
	TRANSMUTATION_CONTAINER("container", "transmutation"),
	
	// Items
	ULTIMA("item", "ultima"),
	ULTIMA_TOOL("item", "ultima_tool"),
	ULTIMA_HELMET("item", "ultima_helmet"),
	ULTIMA_CHESTPLATE("item", "ultima_chestplate"),
	ULTIMA_LEGGINGS("item", "ultima_leggings"),
	ULTIMA_BOOTS("item", "ultima_boots"),
	TOME_OF_KNOWLEDGE("item", "tome_of_knowledge"),
	WATCH_OF_FLOWING_TIME("item", "watch_of_flowing_time"),
	
	// Keys
	CRAFTING("key", "crafting"),
	FEED_TOGGLE("key", "feed_toggle"),
	FURNACE("key", "furnace"),
	INFINITE_CONTAINER("key", "infinite_container"),
	REPAIR_TOGGLE("key", "repair_toggle"),
	TRANSMUTATION("key", "transmutation"),
	ULTIMA_TOOL_MODE("key", "ultima_tool_mode"),
	
	// Messages
	FEEDING_ENABLED("message", "feeding_enabled"),
	FEEDING_DISABLED("message", "feeding_disabled"),
	REPAIR_ENABLED("message", "repair_enabled"),
	REPAIR_DISABLED("message", "repair_disabled"),
	ULTIMA_TOOL_SINGLE("message", "ultima_tool_single"),
	ULTIMA_TOOL_WIDE("message", "ultima_tool_wide"),
	ULTIMA_TOOL_TALL("message", "ultima_tool_tall"),
	ULTIMA_TOOL_DEEP("message", "ultima_tool_deep"),
	KNOWLEDGE_LEARNED("message", "knowledge_learned"),
	
	// Misc
	INVENTORYEXCHANGE("misc", "mod_name"),
	PACK_DESCRIPTION("misc", "pack_description");

	private final String key;

	InventoryExchangeLang(String type, String path) {
		this(Util.makeDescriptionId(type, InventoryExchange.rl(path)));
	}

	InventoryExchangeLang(String key) {
		this.key = key;
	}

	@Override
	public String getTranslationKey() {
		return key;
	}
}