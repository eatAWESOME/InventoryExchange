package eatawesome.inventoryexchange.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class InventoryExchangeCommonConfig {

	// spec
	public static final ModConfigSpec COMMON_SPEC;
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
	public static final ModConfigSpec.BooleanValue CFG_DEBUG_LOGS;

	// keys
	private static final String KEY_DEBUG_LOGS = "enable_debug_logs";

	// defaults
	private static final boolean DEFAULT_DEBUG_LOGS = false;

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

		// build
		COMMON_SPEC = BUILDER.build();
	}

	private InventoryExchangeCommonConfig() {}

	public static boolean isDebugLoggingEnabled() {
		return CFG_DEBUG_LOGS.get();
	}
}