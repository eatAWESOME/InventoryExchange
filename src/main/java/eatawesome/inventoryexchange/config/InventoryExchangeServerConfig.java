package eatawesome.inventoryexchange.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class InventoryExchangeServerConfig {

	// spec
	public static final ModConfigSpec SERVER_SPEC;
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
	public static final ModConfigSpec.IntValue CFG_MAX_ROWS;
	public static final ModConfigSpec.BooleanValue CFG_ULTIMA_WATCH;
	public static final ModConfigSpec.DoubleValue CFG_PIXELMON_KNOCKOUT_MULTIPLIER;
	public static final ModConfigSpec.DoubleValue CFG_PIXELMON_CAPTURE_MULTIPLIER;

	// keys
	private static final String KEY_MAX_ROWS = "infinite_container_max_rows";
	private static final String KEY_ULTIMA_WATCH = "allow_ultima_watch";
	private static final String KEY_PIXELMON_KNOCKOUT_MULTIPLIER = "knockout_multiplier";
	private static final String KEY_PIXELMON_CAPTURE_MULTIPLIER = "capture_multiplier";

	// defaults
	private static final int DEFAULT_MAX_ROWS = 500;
	private static final int MIN_ROWS = 10;
	private static final boolean DEFAULT_ULTIMA_WATCH = true;
	private static final double DEFAULT_PIXELMON_KNOCKOUT_MULTIPLIER = 1.0;
	private static final double DEFAULT_PIXELMON_CAPTURE_MULTIPLIER = 5.0;
	private static final double MIN_MULTIPLIER = 0;

	static {
		// header
		BUILDER.comment(" " + InventoryExchangeConfigTranslations.SERVER_HEADER.title())
			   .translation(InventoryExchangeConfigTranslations.SERVER_HEADER.getTranslationKey());

		
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
		
		// Pixelmon
		InventoryExchangeConfigTranslations.PIXELMON.applyToBuilder(BUILDER).push("pixelmon");
		
		CFG_PIXELMON_KNOCKOUT_MULTIPLIER = BUILDER
			.comment(
				"\n " + InventoryExchangeConfigTranslations.PIXELMON_KNOCKOUT_MULTIPLIER.title(),
				" " + InventoryExchangeConfigTranslations.PIXELMON_KNOCKOUT_MULTIPLIER.tooltip()
			)
			.translation(InventoryExchangeConfigTranslations.PIXELMON_KNOCKOUT_MULTIPLIER.getTranslationKey())
			.defineInRange(KEY_PIXELMON_KNOCKOUT_MULTIPLIER, DEFAULT_PIXELMON_KNOCKOUT_MULTIPLIER, MIN_MULTIPLIER, Double.MAX_VALUE);

		CFG_PIXELMON_CAPTURE_MULTIPLIER = BUILDER
				.comment(
					"\n " + InventoryExchangeConfigTranslations.PIXELMON_CAPTURE_MULTIPLIER.title(),
					" " + InventoryExchangeConfigTranslations.PIXELMON_CAPTURE_MULTIPLIER.tooltip()
				)
				.translation(InventoryExchangeConfigTranslations.PIXELMON_CAPTURE_MULTIPLIER.getTranslationKey())
				.defineInRange(KEY_PIXELMON_CAPTURE_MULTIPLIER, DEFAULT_PIXELMON_CAPTURE_MULTIPLIER, MIN_MULTIPLIER, Double.MAX_VALUE);
		
		BUILDER.pop();
		
		// build
		SERVER_SPEC = BUILDER.build();
	}

	private InventoryExchangeServerConfig() {}

	public static int getMaxInfiniteContainerRows() {
		return Math.max(MIN_ROWS, CFG_MAX_ROWS.get());
	}

	public static boolean isUltimaWatchAllowed() {
		return CFG_ULTIMA_WATCH.get();
	}

	public static double pixelmonKnockoutMultiplier() {
		return CFG_PIXELMON_KNOCKOUT_MULTIPLIER.get();
	}
	
	public static double pixelmonCaptureMultiplier() {
		return CFG_PIXELMON_CAPTURE_MULTIPLIER.get();
	}
}