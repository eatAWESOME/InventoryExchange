package eatawesome.inventoryexchange.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class InventoryExchangeServerConfig {

	// spec
	public static final ModConfigSpec SERVER_SPEC;
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
	public static final ModConfigSpec.DoubleValue CFG_PIXELMON_KNOCKOUT_MULTIPLIER;
	public static final ModConfigSpec.DoubleValue CFG_PIXELMON_CAPTURE_MULTIPLIER;

	// keys
	private static final String KEY_PIXELMON_KNOCKOUT_MULTIPLIER = "pixelmon_knockout_multiplier";
	private static final String KEY_PIXELMON_CAPTURE_MULTIPLIER = "pixelmon_capture_multiplier";

	// defaults
	private static final double DEFAULT_PIXELMON_KNOCKOUT_MULTIPLIER = 1.0;
	private static final double DEFAULT_PIXELMON_CAPTURE_MULTIPLIER = 5.0;

	static {
		// header
		BUILDER.comment(" " + InventoryExchangeConfigTranslations.SERVER_HEADER.title())
			   .translation(InventoryExchangeConfigTranslations.SERVER_HEADER.getTranslationKey());

		// Pixelmon
		InventoryExchangeConfigTranslations.PIXELMON.applyToBuilder(BUILDER).push("pixelmon");
		
		CFG_PIXELMON_KNOCKOUT_MULTIPLIER = BUILDER
			.comment(
				"\n " + InventoryExchangeConfigTranslations.PIXELMON_KNOCKOUT_MULTIPLIER.title(),
				" " + InventoryExchangeConfigTranslations.PIXELMON_KNOCKOUT_MULTIPLIER.tooltip()
			)
			.translation(InventoryExchangeConfigTranslations.PIXELMON_KNOCKOUT_MULTIPLIER.getTranslationKey())
			.defineInRange(KEY_PIXELMON_KNOCKOUT_MULTIPLIER, DEFAULT_PIXELMON_KNOCKOUT_MULTIPLIER, 0.0, Double.MAX_VALUE);

		CFG_PIXELMON_CAPTURE_MULTIPLIER = BUILDER
				.comment(
					"\n " + InventoryExchangeConfigTranslations.PIXELMON_CAPTURE_MULTIPLIER.title(),
					" " + InventoryExchangeConfigTranslations.PIXELMON_CAPTURE_MULTIPLIER.tooltip()
				)
				.translation(InventoryExchangeConfigTranslations.PIXELMON_CAPTURE_MULTIPLIER.getTranslationKey())
				.defineInRange(KEY_PIXELMON_CAPTURE_MULTIPLIER, DEFAULT_PIXELMON_CAPTURE_MULTIPLIER, 0.0, Double.MAX_VALUE);
		
		BUILDER.pop();
		
		// build
		SERVER_SPEC = BUILDER.build();
	}

	private InventoryExchangeServerConfig() {}

	public static double pixelmon_knockout_multiplier() {
		return CFG_PIXELMON_KNOCKOUT_MULTIPLIER.get();
	}
	
	public static double pixelmon_capture_multiplier() {
		return CFG_PIXELMON_CAPTURE_MULTIPLIER.get();
	}
}