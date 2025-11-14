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
	
	// Client Config
	CLIENT_HEADER("client_header", InventoryExchange.MODNAME + " Client Config", ""),
	LIMIT_CONDENSING("limit_condensing", "Limit Condensing", "If true, disables inventory condensing once there are no more condensable items remaining."),
	LIMIT_FEEDING("limit_feeding", "Limit Feeding", "If true, disables feeding once healing is possible."),
	LIMIT_REPAIRING("limit_repairing", "Limit Repairing", "If true, disables repairing once there are no more repairable items remaining."),
	LIMIT_SMELTING("limit_smelting", "Limit Smelting", "If true, disables inventory smelting once there are no more smeltable items remaining."),
	
	// Mapper
	MAPPING_PIXELMON_EMC_MAPPER("mapping.mapper.custom.emc", "Pixelmon EMC Mapper", "Adds Pixelmon item EMC values if pixelmon is installed.");
	
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