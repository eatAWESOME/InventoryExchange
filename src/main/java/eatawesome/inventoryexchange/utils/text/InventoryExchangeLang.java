package eatawesome.inventoryexchange.utils.text;

import eatawesome.inventoryexchange.InventoryExchange;
import moze_intel.projecte.utils.text.ILangEntry;
import net.minecraft.Util;

public enum InventoryExchangeLang implements ILangEntry {
	// Keys
	TRANSMUTATION("key", "transmutation"),
	
	// Misc
	INVENTORYEXCHANGE("misc", "mod_name");

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