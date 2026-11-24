package eatawesome.inventoryexchange.client.lang;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.config.InventoryExchangeConfigTranslations;
import eatawesome.inventoryexchange.config.EMCConfig;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeItems;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeSoundEvents;
import eatawesome.inventoryexchange.utils.InventoryExchangeKeybind;
import eatawesome.inventoryexchange.utils.text.InventoryExchangeLang;
import net.minecraft.Util;
import net.minecraft.data.PackOutput;

public class InventoryExchangeLangProvider extends BaseLanguageProvider {

	public InventoryExchangeLangProvider(PackOutput output) {
		super(output, InventoryExchange.MODID);
	}

	@Override
	protected void addTranslations() {
		
		// Misc stuff
		add(InventoryExchangeLang.INVENTORYEXCHANGE, InventoryExchange.MODNAME);
		add(InventoryExchangeLang.PACK_DESCRIPTION, InventoryExchange.MODNAME + " Mod");
		addModInfo(InventoryExchange.MODNAME, "A mod for extending inventory storage and item transmutation.");
		add(InventoryExchangeLang.SECONDS, "%1$s seconds");
		add(InventoryExchangeLang.EVERY_TICK, "%1$s seconds (every tick)");
		add(InventoryExchangeLang.BLACKLIST, "Blacklist");
		add(InventoryExchangeLang.WHITELIST, "Whitelist");
		
		// Containers
		add(InventoryExchangeLang.INFINITE_CONTAINER_CONTAINER, "Infinite Inventory");
		add(InventoryExchangeLang.INFINITE_CONTAINER_SORT, "Sort");
		add(InventoryExchangeLang.TRANSMUTATION_CONTAINER, "Transmutation");
		
		// Keybinds
		add(InventoryExchangeKeybind.CRAFTING, "Open Crafting");
		add(InventoryExchangeKeybind.CONDENSE_TOGGLE, "Toggle Inventory Condensing");
		add(InventoryExchangeKeybind.FEED_TOGGLE, "Toggle Feeding via EMC");
		add(InventoryExchangeKeybind.FURNACE, "Open Furnace");
		add(InventoryExchangeKeybind.INFINITE_CONTAINER, "Open Infinite Inventory");
		add(InventoryExchangeKeybind.REPAIR_TOGGLE, "Toggle Repairing Items via EMC");
		add(InventoryExchangeKeybind.SMELT_TOGGLE, "Toggle Smelting Items via EMC");
		add(InventoryExchangeKeybind.TRANSMUTATION, "Open Transmutation");
		add(InventoryExchangeKeybind.ULTIMA_TOOL_MODE, "Change Ultima Tool Mode");

		// Commands
		add(InventoryExchangeLang.CLEAR_KNOWLEDGE_NOTIFY, "Your transmutation knowledge was cleared by %1$s!");
		add(InventoryExchangeLang.CLEAR_KNOWLEDGE_SUCCESS, "Cleared knowledge for: %1$s");
		add(InventoryExchangeLang.DUMP_MISSING_EMC_NONE_MISSING, "All Items have an EMC value.");
		add(InventoryExchangeLang.DUMP_MISSING_EMC_ONE_MISSING, "One Item is missing an EMC value, printing to server log.");
		add(InventoryExchangeLang.DUMP_MISSING_EMC_MULTIPLE_MISSING, "%1$s Items are missing an EMC value, printing to client log.");
		add(InventoryExchangeLang.COMMAND_INVALID_ITEM, "Error: The item or tag \"%1$s\" was not found!");
		add(InventoryExchangeLang.COMMAND_NO_ITEM, "Please give an item or tag to change");
		add(InventoryExchangeLang.COMMAND_REMOVE_SUCCESS, "Removed EMC value for %1$s.");
		add(InventoryExchangeLang.COMMAND_RESET_SUCCESS, "Reset EMC value for %1$s.");
		add(InventoryExchangeLang.COMMAND_SET_SUCCESS, "Set EMC value for %1$s to %2$s!");
		add(InventoryExchangeLang.COMMAND_EMC_INVALID, "The value \"%1$s\" is invalid, it must be a positive integer.");
		add(InventoryExchangeLang.COMMAND_EMC_NEGATIVE, "Cannot remove %1$s EMC from %2$s as this would make their EMC negative.");
		add(InventoryExchangeLang.COMMAND_EMC_ADD_SUCCESS, "Added %1$s EMC to %2$s.");
		add(InventoryExchangeLang.COMMAND_EMC_REMOVE_SUCCESS, "Removed %1$s EMC from %2$s.");
		add(InventoryExchangeLang.COMMAND_EMC_SET_SUCCESS, "Set the EMC of %1$s to %2$s.");
		add(InventoryExchangeLang.COMMAND_EMC_TEST_SUCCESS, "%1$s does have enough EMC to remove %2$s.");
		add(InventoryExchangeLang.COMMAND_EMC_TEST_FAIL, "%1$s does not have enough EMC to remove %2$s.");
		add(InventoryExchangeLang.COMMAND_EMC_GET_SUCCESS, "%1$s has %2$s EMC.");
		add(InventoryExchangeLang.COMMAND_KNOWLEDGE_INVALID, "The item \"%1$s\" does not  have an EMC value, and cannot be learned.");
		add(InventoryExchangeLang.COMMAND_KNOWLEDGE_CLEAR_SUCCESS, "Successfully cleared the knowledge of %1$s.");
		add(InventoryExchangeLang.COMMAND_KNOWLEDGE_CLEAR_FAIL, "%1$s does not have any knowledge to clear.");
		add(InventoryExchangeLang.COMMAND_KNOWLEDGE_LEARN_SUCCESS, "%1$s has successfully learned %2$s.");
		add(InventoryExchangeLang.COMMAND_KNOWLEDGE_LEARN_FAIL, "%1$s already has knowledge of %2$s.");
		add(InventoryExchangeLang.COMMAND_KNOWLEDGE_UNLEARN_SUCCESS, "%1$s has successfully unlearned %2$s.");
		add(InventoryExchangeLang.COMMAND_KNOWLEDGE_UNLEARN_FAIL, "%1$s does not have knowledge of %2$s.");
		add(InventoryExchangeLang.COMMAND_KNOWLEDGE_TEST_SUCCESS, "%1$s has knowledge of %2$s.");
		add(InventoryExchangeLang.COMMAND_KNOWLEDGE_TEST_FAIL, "%1$s does not have knowledge of %2$s.");
		add(InventoryExchangeLang.COMMAND_PROVIDER_FAIL, "Failed to get provider for %1$s.");
		add(InventoryExchangeLang.RELOAD_NOTICE, "Restart or use \"/reload\" when all changes are complete.");
		add(InventoryExchangeLang.CONDENSING_ENABLED, "Inventory Condensing: Enabled");
		add(InventoryExchangeLang.CONDENSING_DISABLED, "Inventory Condensing: Disabled");
		add(InventoryExchangeLang.CONDENSING_COMPLETED, "Inventory Condensing: Completed");
		add(InventoryExchangeLang.FEEDING_ENABLED, "Feeding via EMC: Enabled");
		add(InventoryExchangeLang.FEEDING_DISABLED, "Feeding via EMC: Disabled");
		add(InventoryExchangeLang.FEEDING_COMPLETED, "Feeding via EMC: Completed");
		add(InventoryExchangeLang.REPAIRING_ENABLED, "Repairing Items via EMC: Enabled");
		add(InventoryExchangeLang.REPAIRING_DISABLED, "Repairing Items via EMC: Disabled");
		add(InventoryExchangeLang.REPAIRING_COMPLETED, "Repairing Items via EMC: Completed");
		add(InventoryExchangeLang.SMELTING_ENABLED, "Smelting via EMC: Enabled");
		add(InventoryExchangeLang.SMELTING_DISABLED, "Smelting via EMC: Disabled");
		add(InventoryExchangeLang.SMELTING_COMPLETED, "Smelting via EMC: Completed");
		
		// Items
		add(InventoryExchangeItems.ULTIMA, "Ultima");
		add(InventoryExchangeItems.ULTIMA_TOOL, "Ultima Tool");
		add(InventoryExchangeItems.ULTIMA_WATCH, "Ultima Watch");
		add(InventoryExchangeItems.ULTIMA_HELMET, "Ultima Helmet");
		add(InventoryExchangeItems.ULTIMA_CHESTPLATE, "Ultima Chestplate");
		add(InventoryExchangeItems.ULTIMA_LEGGINGS, "Ultima Leggings");
		add(InventoryExchangeItems.ULTIMA_BOOTS, "Ultima Boots");
		add(InventoryExchangeItems.TOME_OF_KNOWLEDGE, "Tome of Knowledge");
		
		// Item Messages
		add(InventoryExchangeLang.ULTIMA_TOOL_SINGLE, "Ultima Tool set to Single Mode");
		add(InventoryExchangeLang.ULTIMA_TOOL_WIDE, "Ultima Tool set to Wide Mode (3x)");
		add(InventoryExchangeLang.ULTIMA_TOOL_TALL, "Ultima Tool set to Tall Mode (3x)");
		add(InventoryExchangeLang.ULTIMA_TOOL_DEEP, "Ultima Tool set to Deep Mode (3x)");
		add(InventoryExchangeLang.TIME_WATCH_DISABLED, "Item disabled by server admin");
		add(InventoryExchangeLang.TIME_WATCH_MODE, "Time control mode: %1$s");
		add(InventoryExchangeLang.TIME_WATCH_MODE_SWITCH, "Time control mode set to: %1$s");
		add(InventoryExchangeLang.TIME_WATCH_OFF, "Off");
		add(InventoryExchangeLang.TIME_WATCH_FAST_FORWARD, "Fast-Forward");
		add(InventoryExchangeLang.TIME_WATCH_REWIND, "Rewind");

		// Tooltips
		add(InventoryExchangeLang.TOOLTIP_TOME, "Unlocks all transmutation knowledge when learned");
		add(InventoryExchangeLang.TOOLTIP_TIME_WATCH_1, "Become the master of time");
		add(InventoryExchangeLang.TOOLTIP_TIME_WATCH_2, "Interact (%1$s) to change mode");
		
		// Subtitles
		add(InventoryExchangeSoundEvents.FLIGHT_START, "Flight Started");
		add(InventoryExchangeSoundEvents.FLIGHT_END, "Flight Ended");
		add(InventoryExchangeSoundEvents.VEIN_MINE, "Vein Mined");

		// Configs
		addInventoryExchangeConfigs(InventoryExchangeConfigTranslations.values());
		addInventoryExchangeConfigs(EMCConfig.getConfigs());
		
		// Transmutation
		add(InventoryExchangeLang.TRANSMUTATION_TRANSMUTE, "Transmutation");
		add(InventoryExchangeLang.TRANSMUTATION_LEARNED_1, "L");
		add(InventoryExchangeLang.TRANSMUTATION_LEARNED_2, "e");
		add(InventoryExchangeLang.TRANSMUTATION_LEARNED_3, "a");
		add(InventoryExchangeLang.TRANSMUTATION_LEARNED_4, "r");
		add(InventoryExchangeLang.TRANSMUTATION_LEARNED_5, "n");
		add(InventoryExchangeLang.TRANSMUTATION_LEARNED_6, "e");
		add(InventoryExchangeLang.TRANSMUTATION_LEARNED_7, "d");
		add(InventoryExchangeLang.TRANSMUTATION_LEARNED_8, "!");
		add(InventoryExchangeLang.TRANSMUTATION_UNLEARNED_1, "U");
		add(InventoryExchangeLang.TRANSMUTATION_UNLEARNED_2, "n");
		add(InventoryExchangeLang.TRANSMUTATION_UNLEARNED_3, "l");
		add(InventoryExchangeLang.TRANSMUTATION_UNLEARNED_4, "e");
		add(InventoryExchangeLang.TRANSMUTATION_UNLEARNED_5, "a");
		add(InventoryExchangeLang.TRANSMUTATION_UNLEARNED_6, "r");
		add(InventoryExchangeLang.TRANSMUTATION_UNLEARNED_7, "n");
		add(InventoryExchangeLang.TRANSMUTATION_UNLEARNED_8, "e");
		add(InventoryExchangeLang.TRANSMUTATION_UNLEARNED_9, "d");
		add(InventoryExchangeLang.KNOWLEDGE_LEARNED, "Transmutation Knowledge Learned!");
		
		// EMC
		add(InventoryExchangeLang.EMC, "%1$s EMC");
		add(InventoryExchangeLang.EMC_MAX_GEN_RATE, "Maximum Generation Rate: %1$s EMC/t");
		add(InventoryExchangeLang.EMC_MAX_OUTPUT_RATE, "Maximum Output Rate: %1$s EMC/t");
		add(InventoryExchangeLang.EMC_MAX_STORAGE, "Maximum Storage: %1$s EMC");
		add(InventoryExchangeLang.EMC_STORED, "Stored EMC: %1$s");
		add(InventoryExchangeLang.EMC_HAS_KNOWLEDGE, "Learned");
		add(InventoryExchangeLang.EMC_NO_KNOWLEDGE, "Unlearned");
		add(InventoryExchangeLang.EMC_TOOLTIP, "EMC: %1$s");
		add(InventoryExchangeLang.EMC_STACK_TOOLTIP, "Stack EMC: %1$s");
		add(InventoryExchangeLang.EMC_TOOLTIP_WITH_SELL, "EMC: %1$s (%2$s)");
		add(InventoryExchangeLang.EMC_STACK_TOOLTIP_WITH_SELL, "Stack EMC: %1$s (%2$s)");
		add(InventoryExchangeLang.EMC_TOO_MUCH, "WAY TOO MUCH");
		
		//Postfix names for large EMC values.
		addPostfix(0, "Trillion");
		addPostfix(1, "Quadrillion");
		addPostfix(2, "Quintillion");
		addPostfix(3, "Sextillion");
		addPostfix(4, "Septillion");
		addPostfix(5, "Octillion");
		addPostfix(6, "Nonillion");
		addPostfix(7, "Decillion");
		addPostfix(8, "Undecillion");
		addPostfix(9, "Duodecillion");
		addPostfix(10, "Tredecillion");
		addPostfix(11, "Quattuordecillion");
		addPostfix(12, "Quindecillion");
		addPostfix(13, "Sexdecillion");
		addPostfix(14, "Septendecillion");
		addPostfix(15, "Octodecillion");
		addPostfix(16, "Novemdecillion");
		addPostfix(17, "Vigintillion");
	}

	private void addPostfix(int index, String postfix) {
		add(Util.makeDescriptionId("emc", InventoryExchange.rl("postfix." + index)), "%1$s " + postfix);
	}
}