package eatawesome.inventoryexchange.client.lang;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.config.InventoryExchangeConfigTranslations;
import moze_intel.projecte.PECore;
import moze_intel.projecte.config.PEConfigTranslations;
import moze_intel.projecte.config.ProjectEConfig;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeItems;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import eatawesome.inventoryexchange.utils.InventoryExchangeKeybind;
import eatawesome.inventoryexchange.utils.text.InventoryExchangeLang;
import moze_intel.projecte.utils.text.PELang;
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
		add(PELang.PROJECTE, PECore.MODNAME);
		add(PELang.PACK_DESCRIPTION, "Resources used for " + PECore.MODNAME);
		addModInfo(PECore.MODNAME, "A complete rewrite of EE2 for modern Minecraft versions.");
		add(PELang.SECONDS, "%1$s seconds");
		add(PELang.EVERY_TICK, "%1$s seconds (every tick)");
		add(PELang.HIGH_ALCHEMIST, "High alchemist %1$s has joined the server");
		add(PELang.BLACKLIST, "Blacklist");
		add(PELang.WHITELIST, "Whitelist");
		add(PELang.DENSITY_MODE_TARGET, "Set target to: %1$s");
		
		// Containers
		add(InventoryExchangeLang.INFINITE_CONTAINER_CONTAINER, "Infinite Inventory");
		add(InventoryExchangeLang.INFINITE_CONTAINER_SORT, "Sort");
		add(InventoryExchangeLang.TRANSMUTATION_CONTAINER, "Transmutation");
		
		// Keybinds
		add(InventoryExchangeKeybind.CRAFTING, "Open Crafting");
		add(InventoryExchangeKeybind.FEED_TOGGLE, "Toggle Feeding via EMC");
		add(InventoryExchangeKeybind.FURNACE, "Open Furnace");
		add(InventoryExchangeKeybind.INFINITE_CONTAINER, "Open Infinite Inventory");
		add(InventoryExchangeKeybind.REPAIR_TOGGLE, "Toggle Repairing Items via EMC");
		add(InventoryExchangeKeybind.TRANSMUTATION, "Transmutation");
		add(InventoryExchangeKeybind.ULTIMA_TOOL_MODE, "Change Ultima Tool Mode");

		// Commands
		add(PELang.CLEAR_KNOWLEDGE_NOTIFY, "Your transmutation knowledge was cleared by %1$s!");
		add(PELang.CLEAR_KNOWLEDGE_SUCCESS, "Cleared knowledge for: %1$s");
		add(PELang.DUMP_MISSING_EMC_NONE_MISSING, "All Items have an EMC value.");
		add(PELang.DUMP_MISSING_EMC_ONE_MISSING, "One Item is missing an EMC value, printing to server log.");
		add(PELang.DUMP_MISSING_EMC_MULTIPLE_MISSING, "%1$s Items are missing an EMC value, printing to client log.");
		add(PELang.COMMAND_INVALID_ITEM, "Error: The item or tag \"%1$s\" was not found!");
		add(PELang.COMMAND_NO_ITEM, "Please give an item or tag to change");
		add(PELang.COMMAND_REMOVE_SUCCESS, "Removed EMC value for %1$s.");
		add(PELang.COMMAND_RESET_SUCCESS, "Reset EMC value for %1$s.");
		add(PELang.COMMAND_SET_SUCCESS, "Set EMC value for %1$s to %2$s!");
		add(PELang.COMMAND_EMC_INVALID, "The value \"%1$s\" is invalid, it must be a positive integer.");
		add(PELang.COMMAND_EMC_NEGATIVE, "Cannot remove %1$s EMC from %2$s as this would make their EMC negative.");
		add(PELang.COMMAND_EMC_ADD_SUCCESS, "Added %1$s EMC to %2$s.");
		add(PELang.COMMAND_EMC_REMOVE_SUCCESS, "Removed %1$s EMC from %2$s.");
		add(PELang.COMMAND_EMC_SET_SUCCESS, "Set the EMC of %1$s to %2$s.");
		add(PELang.COMMAND_EMC_TEST_SUCCESS, "%1$s does have enough EMC to remove %2$s.");
		add(PELang.COMMAND_EMC_TEST_FAIL, "%1$s does not have enough EMC to remove %2$s.");
		add(PELang.COMMAND_EMC_GET_SUCCESS, "%1$s has %2$s EMC.");
		add(PELang.COMMAND_KNOWLEDGE_INVALID, "The item \"%1$s\" does not  have an EMC value, and cannot be learned.");
		add(PELang.COMMAND_KNOWLEDGE_CLEAR_SUCCESS, "Successfully cleared the knowledge of %1$s.");
		add(PELang.COMMAND_KNOWLEDGE_CLEAR_FAIL, "%1$s does not have any knowledge to clear.");
		add(PELang.COMMAND_KNOWLEDGE_LEARN_SUCCESS, "%1$s has successfully learned %2$s.");
		add(PELang.COMMAND_KNOWLEDGE_LEARN_FAIL, "%1$s already has knowledge of %2$s.");
		add(PELang.COMMAND_KNOWLEDGE_UNLEARN_SUCCESS, "%1$s has successfully unlearned %2$s.");
		add(PELang.COMMAND_KNOWLEDGE_UNLEARN_FAIL, "%1$s does not have knowledge of %2$s.");
		add(PELang.COMMAND_KNOWLEDGE_TEST_SUCCESS, "%1$s has knowledge of %2$s.");
		add(PELang.COMMAND_KNOWLEDGE_TEST_FAIL, "%1$s does not have knowledge of %2$s.");
		add(PELang.COMMAND_PROVIDER_FAIL, "Failed to get provider for %1$s.");
		add(PELang.RELOAD_NOTICE, "Restart or use \"/reload\" when all changes are complete.");
		add(InventoryExchangeLang.FEEDING_ENABLED, "Feeding via EMC: Enabled");
		add(InventoryExchangeLang.FEEDING_DISABLED, "Feeding via EMC: Disabled");
		add(InventoryExchangeLang.REPAIR_ENABLED, "Repairing Items via EMC: Enabled");
		add(InventoryExchangeLang.REPAIR_DISABLED, "Repairing Items via EMC: Disabled");
		add(InventoryExchangeLang.SMELTING_ENABLED, "Auto Smelting via EMC: Enabled");
		add(InventoryExchangeLang.SMELTING_DISABLED, "Auto Smelting via EMC: Disabled");
		
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
		add(PELang.TIME_WATCH_DISABLED, "Item disabled by server admin");
		add(PELang.TIME_WATCH_MODE, "Time control mode: %1$s");
		add(PELang.TIME_WATCH_MODE_SWITCH, "Time control mode set to: %1$s");
		add(PELang.TIME_WATCH_OFF, "Off");
		add(PELang.TIME_WATCH_FAST_FORWARD, "Fast-Forward");
		add(PELang.TIME_WATCH_REWIND, "Rewind");

		// Tooltips
		add(PELang.TOOLTIP_TOME, "Unlocks all transmutation knowledge when learned");
		add(PELang.TOOLTIP_TIME_WATCH_1, "Become the master of time");
		add(PELang.TOOLTIP_TIME_WATCH_2, "Interact (%1$s) to change mode");
		
		// Subtitles
		add(PESoundEvents.WIND_MAGIC, "Wind Magic");
		add(PESoundEvents.WATER_MAGIC, "Water Magic");
		add(PESoundEvents.POWER, "Device Powered");
		add(PESoundEvents.HEAL, "Healing Performed");
		add(PESoundEvents.DESTRUCT, "Destruction");
		add(PESoundEvents.CHARGE, "Device Charged");
		add(PESoundEvents.UNCHARGE, "Device Uncharged");
		add(PESoundEvents.TRANSMUTE, "Block Transmuted");

		// Configs
		addInventoryExchangeConfigs(InventoryExchangeConfigTranslations.values());
		addPEConfigs(ProjectEConfig.getConfigs());
		addPEConfigs(PEConfigTranslations.values());
		
		// Transmutation
		add(PELang.TRANSMUTATION_TRANSMUTE, "Transmutation");
		add(PELang.TRANSMUTATION_LEARNED_1, "L");
		add(PELang.TRANSMUTATION_LEARNED_2, "e");
		add(PELang.TRANSMUTATION_LEARNED_3, "a");
		add(PELang.TRANSMUTATION_LEARNED_4, "r");
		add(PELang.TRANSMUTATION_LEARNED_5, "n");
		add(PELang.TRANSMUTATION_LEARNED_6, "e");
		add(PELang.TRANSMUTATION_LEARNED_7, "d");
		add(PELang.TRANSMUTATION_LEARNED_8, "!");
		add(PELang.TRANSMUTATION_UNLEARNED_1, "U");
		add(PELang.TRANSMUTATION_UNLEARNED_2, "n");
		add(PELang.TRANSMUTATION_UNLEARNED_3, "l");
		add(PELang.TRANSMUTATION_UNLEARNED_4, "e");
		add(PELang.TRANSMUTATION_UNLEARNED_5, "a");
		add(PELang.TRANSMUTATION_UNLEARNED_6, "r");
		add(PELang.TRANSMUTATION_UNLEARNED_7, "n");
		add(PELang.TRANSMUTATION_UNLEARNED_8, "e");
		add(PELang.TRANSMUTATION_UNLEARNED_9, "d");
		add(InventoryExchangeLang.KNOWLEDGE_LEARNED, "Transmutation Knowledge Learned!");
		
		// EMC
		add(PELang.EMC, "%1$s EMC");
		add(PELang.EMC_MAX_GEN_RATE, "Maximum Generation Rate: %1$s EMC/t");
		add(PELang.EMC_MAX_OUTPUT_RATE, "Maximum Output Rate: %1$s EMC/t");
		add(PELang.EMC_MAX_STORAGE, "Maximum Storage: %1$s EMC");
		add(PELang.EMC_STORED, "Stored EMC: %1$s");
		add(PELang.EMC_HAS_KNOWLEDGE, "Learned");
		add(PELang.EMC_NO_KNOWLEDGE, "Unlearned");
		add(PELang.EMC_TOOLTIP, "EMC: %1$s");
		add(PELang.EMC_STACK_TOOLTIP, "Stack EMC: %1$s");
		add(PELang.EMC_TOOLTIP_WITH_SELL, "EMC: %1$s (%2$s)");
		add(PELang.EMC_STACK_TOOLTIP_WITH_SELL, "Stack EMC: %1$s (%2$s)");
		add(PELang.EMC_TOO_MUCH, "WAY TOO MUCH");
		
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
		add(Util.makeDescriptionId("emc", PECore.rl("postfix." + index)), "%1$s " + postfix);
	}
}