package eatawesome.inventoryexchange.config;

import eatawesome.inventoryexchange.InventoryExchange;
import net.minecraft.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum InventoryExchangeConfigTranslations implements IConfigTranslation {
	// Config
	CONFIG_HEADER("config_header", InventoryExchange.MODNAME + " Config", ""),
	INFINITE_CONTAINER_MAX_ROWS("infinite_container_max_rows", "Infinite Container Max Rows", "Maximum number of rows for the infinite container. Minimum 10."),
	ENABLE_DEBUG_LOGS("enable_debug_logs", "Enable Debug Logs", "If true, enables debug logs."),
	ALLOW_ULTIMA_WATCH("allow_ultima_watch", "Allow Ultima Watch", "If true, allows use of the Ultima Watch.");

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