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
	CONDENSING("key", "condense_toggle"),
	CRAFTING("key", "crafting"),
	FEED_TOGGLE("key", "feed_toggle"),
	FURNACE("key", "furnace"),
	INFINITE_CONTAINER("key", "infinite_container"),
	REPAIR_TOGGLE("key", "repair_toggle"),
	SMELT_TOGGLE("key", "smelt_toggle"),
	TRANSMUTATION("key", "transmutation"),
	ULTIMA_TOOL_MODE("key", "ultima_tool_mode"),
	
	// Messages
	CONDENSING_ENABLED("message", "condensing_enabled"),
	CONDENSING_DISABLED("message", "condensing_disabled"),
	CONDENSING_COMPLETED("message", "condensing_completed"),
	FEEDING_ENABLED("message", "feeding_enabled"),
	FEEDING_DISABLED("message", "feeding_disabled"),
	FEEDING_COMPLETED("message", "feeding_completed"),
	REPAIRING_ENABLED("message", "repairing_enabled"),
	REPAIRING_DISABLED("message", "repairing_disabled"),
	REPAIRING_COMPLETED("message", "repairing_completed"),
	SMELTING_ENABLED("message", "smelting_enabled"),
	SMELTING_DISABLED("message", "smelting_disabled"),
	SMELTING_COMPLETED("message", "smelting_completed"),
	ULTIMA_TOOL_SINGLE("message", "ultima_tool_single"),
	ULTIMA_TOOL_WIDE("message", "ultima_tool_wide"),
	ULTIMA_TOOL_TALL("message", "ultima_tool_tall"),
	ULTIMA_TOOL_DEEP("message", "ultima_tool_deep"),
	KNOWLEDGE_LEARNED("message", "knowledge_learned"),
	
	// Misc
	INVENTORYEXCHANGE("misc", "mod_name"),
	PACK_DESCRIPTION("misc", "pack_description"),
	SECONDS("misc", "seconds"),
	EVERY_TICK("misc", "seconds.every_tick"),
	BLACKLIST("misc", "blacklist"),
	WHITELIST("misc", "whitelist"),
	
	//Command
	CLEAR_KNOWLEDGE_NOTIFY("command", "clear_knowledge.notify"),
	CLEAR_KNOWLEDGE_SUCCESS("command", "clear_knowledge.success"),
	DUMP_MISSING_EMC_NONE_MISSING("command", "dump_missing_emc.none_missing"),
	DUMP_MISSING_EMC_ONE_MISSING("command", "dump_missing_emc.one_missing"),
	DUMP_MISSING_EMC_MULTIPLE_MISSING("command", "dump_missing_emc.multiple_missing"),
	COMMAND_INVALID_ITEM("command", "emc.invalid_item"),
	COMMAND_NO_ITEM("command", "emc.no_item"),
	COMMAND_REMOVE_SUCCESS("command", "remove.success"),
	COMMAND_RESET_SUCCESS("command", "reset.success"),
	COMMAND_SET_SUCCESS("command", "set.success"),
	COMMAND_EMC_INVALID("command", "emc.invalid"),
	COMMAND_EMC_NEGATIVE("command", "emc.negative"),
	COMMAND_EMC_ADD_SUCCESS("command", "emc.add.success"),
	COMMAND_EMC_REMOVE_SUCCESS("command", "emc.remove.success"),
	COMMAND_EMC_SET_SUCCESS("command", "emc.set.success"),
	COMMAND_EMC_GET_SUCCESS("command", "emc.get.success"),
	COMMAND_EMC_TEST_SUCCESS("command", "emc.test.success"),
	COMMAND_EMC_TEST_FAIL("command", "emc.test.fail"),
	COMMAND_KNOWLEDGE_INVALID("command", "knowledge.invalid"),
	COMMAND_KNOWLEDGE_CLEAR_SUCCESS("command", "knowledge.clear.success"),
	COMMAND_KNOWLEDGE_CLEAR_FAIL("command", "knowledge.clear.fail"),
	COMMAND_KNOWLEDGE_LEARN_SUCCESS("command", "knowledge.learn.success"),
	COMMAND_KNOWLEDGE_LEARN_FAIL("command", "knowledge.learn.fail"),
	COMMAND_KNOWLEDGE_UNLEARN_SUCCESS("command", "knowledge.unlearn.success"),
	COMMAND_KNOWLEDGE_UNLEARN_FAIL("command", "knowledge.unlearn.fail"),
	COMMAND_KNOWLEDGE_TEST_SUCCESS("command", "knowledge.test.success"),
	COMMAND_KNOWLEDGE_TEST_FAIL("command", "knowledge.test.fail"),
	COMMAND_PROVIDER_FAIL("command", "provider.fail"),
	RELOAD_NOTICE("command", "reload.notice"),
	
	//EMC
	EMC("emc", "emc"),
	EMC_MAX_GEN_RATE("emc", "max_gen_rate"),
	EMC_MAX_OUTPUT_RATE("emc", "max_output_rate"),
	EMC_MAX_STORAGE("emc", "max_storage"),
	EMC_STORED("emc", "stored"),
	EMC_HAS_KNOWLEDGE("emc", "has_knowledge"),
	EMC_NO_KNOWLEDGE("emc", "no_knowledge"),
	EMC_TOOLTIP("emc", "tooltip"),
	EMC_IE_TOOLTIP("emc", "tooltip_ie"),
	EMC_STACK_TOOLTIP("emc", "tooltip.stack"),
	EMC_IE_STACK_TOOLTIP("emc", "tooltip_ie.stack"),
	EMC_TOO_MUCH("emc", "too_much"),
	
	//Watch
	TIME_WATCH_DISABLED("time_watch", "disabled"),
	TIME_WATCH_MODE("time_watch", "mode"),
	TIME_WATCH_MODE_SWITCH("time_watch", "mode_switch"),
	TIME_WATCH_OFF("time_watch", "off"),
	TIME_WATCH_FAST_FORWARD("time_watch", "fast_forward"),
	TIME_WATCH_REWIND("time_watch", "rewind"),
	
	//Tooltips
	TOOLTIP_TIME_WATCH_1("tooltip", "time_watch.1"),
	TOOLTIP_TIME_WATCH_2("tooltip", "time_watch.2"),
	TOOLTIP_TOME("tooltip", "tome"),
	
	//Transmutation GUI
	TRANSMUTATION_TRANSMUTE("transmutation", "transmute"),
	TRANSMUTATION_LEARNED_1("transmutation", "learned.1"),
	TRANSMUTATION_LEARNED_2("transmutation", "learned.2"),
	TRANSMUTATION_LEARNED_3("transmutation", "learned.3"),
	TRANSMUTATION_LEARNED_4("transmutation", "learned.4"),
	TRANSMUTATION_LEARNED_5("transmutation", "learned.5"),
	TRANSMUTATION_LEARNED_6("transmutation", "learned.6"),
	TRANSMUTATION_LEARNED_7("transmutation", "learned.7"),
	TRANSMUTATION_LEARNED_8("transmutation", "learned.8"),
	TRANSMUTATION_UNLEARNED_1("transmutation", "unlearned.1"),
	TRANSMUTATION_UNLEARNED_2("transmutation", "unlearned.2"),
	TRANSMUTATION_UNLEARNED_3("transmutation", "unlearned.3"),
	TRANSMUTATION_UNLEARNED_4("transmutation", "unlearned.4"),
	TRANSMUTATION_UNLEARNED_5("transmutation", "unlearned.5"),
	TRANSMUTATION_UNLEARNED_6("transmutation", "unlearned.6"),
	TRANSMUTATION_UNLEARNED_7("transmutation", "unlearned.7"),
	TRANSMUTATION_UNLEARNED_8("transmutation", "unlearned.8"),
	TRANSMUTATION_UNLEARNED_9("transmutation", "unlearned.9")

	;
	
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