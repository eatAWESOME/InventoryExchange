package eatawesome.inventoryexchange.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class InventoryExchangeCommonConfig {

	// spec
	public static final ModConfigSpec COMMON_SPEC;
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
	public static final ModConfigSpec.BooleanValue CFG_DEBUG_LOGS;
	public static final ModConfigSpec.IntValue CFG_MAX_ROWS;
	public static final ModConfigSpec.BooleanValue CFG_ULTIMA_WATCH;

	// keys
	private static final String KEY_DEBUG_LOGS = "enable_debug_logs";
	private static final String KEY_MAX_ROWS = "infinite_container_max_rows";
	private static final String KEY_ULTIMA_WATCH = "allow_ultima_watch";

	// defaults
	private static final boolean DEFAULT_DEBUG_LOGS = false;
	private static final int DEFAULT_MAX_ROWS = 500;
	private static final int MIN_ROWS = 10;
	private static final boolean DEFAULT_ULTIMA_WATCH = true;

	static {
		// header
		BUILDER.comment(" " + InventoryExchangeConfigTranslations.COMMON_HEADER.title())
			   .translation(InventoryExchangeConfigTranslations.COMMON_HEADER.getTranslationKey());

		// entries
		CFG_DEBUG_LOGS = BUILDER
			.comment(
				"\n " + InventoryExchangeConfigTranslations.ENABLE_DEBUG_LOGS.title(),
				" " + InventoryExchangeConfigTranslations.ENABLE_DEBUG_LOGS.tooltip()
			)
			.translation(InventoryExchangeConfigTranslations.ENABLE_DEBUG_LOGS.getTranslationKey())
			.define(KEY_DEBUG_LOGS, DEFAULT_DEBUG_LOGS);

		CFG_MAX_ROWS = BUILDER
			.comment(
				"\n " + InventoryExchangeConfigTranslations.INFINITE_CONTAINER_MAX_ROWS.title(),
				" " + InventoryExchangeConfigTranslations.INFINITE_CONTAINER_MAX_ROWS.tooltip()
			)
			.translation(InventoryExchangeConfigTranslations.INFINITE_CONTAINER_MAX_ROWS.getTranslationKey())
			.defineInRange(KEY_MAX_ROWS, DEFAULT_MAX_ROWS, MIN_ROWS, Integer.MAX_VALUE);

		CFG_ULTIMA_WATCH = BUILDER
			.comment(
				"\n " + InventoryExchangeConfigTranslations.ALLOW_ULTIMA_WATCH.title(),
				" " + InventoryExchangeConfigTranslations.ALLOW_ULTIMA_WATCH.tooltip()
			)
			.translation(InventoryExchangeConfigTranslations.ALLOW_ULTIMA_WATCH.getTranslationKey())
			.define(KEY_ULTIMA_WATCH, DEFAULT_ULTIMA_WATCH);

		// build
		COMMON_SPEC = BUILDER.build();
	}

	private InventoryExchangeCommonConfig() {}

	public static boolean isDebugLoggingEnabled() {
		return CFG_DEBUG_LOGS.get();
	}

	public static int getMaxInfiniteContainerRows() {
		return Math.max(MIN_ROWS, CFG_MAX_ROWS.get());
	}

	public static boolean isUltimaWatchAllowed() {
		return CFG_ULTIMA_WATCH.get();
	}
}