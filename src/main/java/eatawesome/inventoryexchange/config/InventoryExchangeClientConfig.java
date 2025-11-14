package eatawesome.inventoryexchange.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class InventoryExchangeClientConfig {

	// spec
	public static final ModConfigSpec CLIENT_SPEC;
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
	public static final ModConfigSpec.BooleanValue CFG_LIMIT_CONDENSING;
	public static final ModConfigSpec.BooleanValue CFG_LIMIT_FEEDING;
	public static final ModConfigSpec.BooleanValue CFG_LIMIT_REPAIRING;
	public static final ModConfigSpec.BooleanValue CFG_LIMIT_SMELTING;

	// keys
	private static final String KEY_LIMIT_CONDENSING = "limit_condensing";
	private static final String KEY_LIMIT_FEEDING = "limit_feeding";
	private static final String KEY_LIMIT_REPAIRING = "limit_repairing";
	private static final String KEY_LIMIT_SMELTING = "limit_smelting";

	// defaults
	private static final boolean DEFAULT_LIMIT_CONDENSING = true;
	private static final boolean DEFAULT_LIMIT_FEEDING = false;
	private static final boolean DEFAULT_LIMIT_REPAIRING = true;
	private static final boolean DEFAULT_LIMIT_SMELTING = false;

	static {
		// header
		BUILDER.comment(" " + InventoryExchangeConfigTranslations.CLIENT_HEADER.title())
			   .translation(InventoryExchangeConfigTranslations.CLIENT_HEADER.getTranslationKey());

		// entries
		CFG_LIMIT_CONDENSING = BUILDER
			.comment(
				"\n " + InventoryExchangeConfigTranslations.LIMIT_CONDENSING.title(),
				" " + InventoryExchangeConfigTranslations.LIMIT_CONDENSING.tooltip()
			)
			.translation(InventoryExchangeConfigTranslations.LIMIT_CONDENSING.getTranslationKey())
			.define(KEY_LIMIT_CONDENSING, DEFAULT_LIMIT_CONDENSING);

		CFG_LIMIT_FEEDING = BUILDER
				.comment(
					"\n " + InventoryExchangeConfigTranslations.LIMIT_FEEDING.title(),
					" " + InventoryExchangeConfigTranslations.LIMIT_FEEDING.tooltip()
				)
				.translation(InventoryExchangeConfigTranslations.LIMIT_FEEDING.getTranslationKey())
				.define(KEY_LIMIT_FEEDING, DEFAULT_LIMIT_FEEDING);

		CFG_LIMIT_REPAIRING = BUILDER
				.comment(
					"\n " + InventoryExchangeConfigTranslations.LIMIT_REPAIRING.title(),
					" " + InventoryExchangeConfigTranslations.LIMIT_REPAIRING.tooltip()
				)
				.translation(InventoryExchangeConfigTranslations.LIMIT_REPAIRING.getTranslationKey())
				.define(KEY_LIMIT_REPAIRING, DEFAULT_LIMIT_REPAIRING);

		CFG_LIMIT_SMELTING = BUILDER
				.comment(
					"\n " + InventoryExchangeConfigTranslations.LIMIT_SMELTING.title(),
					" " + InventoryExchangeConfigTranslations.LIMIT_SMELTING.tooltip()
				)
				.translation(InventoryExchangeConfigTranslations.LIMIT_SMELTING.getTranslationKey())
				.define(KEY_LIMIT_SMELTING, DEFAULT_LIMIT_SMELTING);

		// build
		CLIENT_SPEC = BUILDER.build();
	}

	private InventoryExchangeClientConfig() {}

	public static boolean isCondensingLimited() {
		return CFG_LIMIT_CONDENSING.get();
	}

	public static boolean isFeedingLimited() {
		return CFG_LIMIT_FEEDING.get();
	}

	public static boolean isRepairingLimited() {
		return CFG_LIMIT_REPAIRING.get();
	}

	public static boolean isSmeltingLimited() {
		return CFG_LIMIT_SMELTING.get();
	}
}