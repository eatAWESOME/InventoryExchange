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
	public static final ModConfigSpec.BooleanValue CFG_TOOLTIPS_EMC_ENABLE;
	public static final ModConfigSpec.BooleanValue CFG_TOOLTIPS_EMC_SHIFT;
	public static final ModConfigSpec.BooleanValue CFG_TOOLTIPS_TAG_ENABLE;

	// keys
	private static final String KEY_LIMIT_CONDENSING = "condensing";
	private static final String KEY_LIMIT_FEEDING = "feeding";
	private static final String KEY_LIMIT_REPAIRING = "repairing";
	private static final String KEY_LIMIT_SMELTING = "smelting";
	private static final String KEY_TOOLTIPS_EMC_ENABLE = "enable";
	private static final String KEY_TOOLTIPS_EMC_SHIFT = "shift";
	private static final String KEY_TOOLTIPS_TAG_ENABLE = "tag_enable";

	// defaults
	private static final boolean DEFAULT_LIMIT_CONDENSING = true;
	private static final boolean DEFAULT_LIMIT_FEEDING = false;
	private static final boolean DEFAULT_LIMIT_REPAIRING = true;
	private static final boolean DEFAULT_LIMIT_SMELTING = false;
	private static final boolean DEFAULT_TOOLTIPS_EMC_ENABLE = true;
	private static final boolean DEFAULT_TOOLTIPS_EMC_SHIFT = false;
	private static final boolean DEFAULT_TOOLTIPS_TAG_ENABLE = false;

	static {
		// header
		BUILDER.comment(" " + InventoryExchangeConfigTranslations.CLIENT_HEADER.title())
			   .translation(InventoryExchangeConfigTranslations.CLIENT_HEADER.getTranslationKey());

		// Limits
		InventoryExchangeConfigTranslations.LIMITS.applyToBuilder(BUILDER).push("limits");
		
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
		
		BUILDER.pop();
		
		//Tooltips
		InventoryExchangeConfigTranslations.TOOLTIPS.applyToBuilder(BUILDER).push("tooltips");
		
		InventoryExchangeConfigTranslations.TOOLTIPS_EMC.applyToBuilder(BUILDER).push("emc");
		
		CFG_TOOLTIPS_EMC_ENABLE = BUILDER
				.comment(
					"\n " + InventoryExchangeConfigTranslations.TOOLTIPS_EMC_ENABLE.title(),
					" " + InventoryExchangeConfigTranslations.TOOLTIPS_EMC_ENABLE.tooltip()
				)
				.translation(InventoryExchangeConfigTranslations.TOOLTIPS_EMC_ENABLE.getTranslationKey())
				.define(KEY_TOOLTIPS_EMC_ENABLE, DEFAULT_TOOLTIPS_EMC_ENABLE);
		
		CFG_TOOLTIPS_EMC_SHIFT = BUILDER
				.comment(
					"\n " + InventoryExchangeConfigTranslations.TOOLTIPS_EMC_SHIFT.title(),
					" " + InventoryExchangeConfigTranslations.TOOLTIPS_EMC_SHIFT.tooltip()
				)
				.translation(InventoryExchangeConfigTranslations.TOOLTIPS_EMC_SHIFT.getTranslationKey())
				.define(KEY_TOOLTIPS_EMC_SHIFT, DEFAULT_TOOLTIPS_EMC_SHIFT);
		
		BUILDER.pop();
		
		CFG_TOOLTIPS_TAG_ENABLE = BUILDER
				.comment(
					"\n " + InventoryExchangeConfigTranslations.TOOLTIPS_TAG_ENABLE.title(),
					" " + InventoryExchangeConfigTranslations.TOOLTIPS_TAG_ENABLE.tooltip()
				)
				.translation(InventoryExchangeConfigTranslations.TOOLTIPS_TAG_ENABLE.getTranslationKey())
				.define(KEY_TOOLTIPS_TAG_ENABLE, DEFAULT_TOOLTIPS_TAG_ENABLE);

		BUILDER.pop();
		
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

	public static boolean showEMCTooltips() {
		return CFG_TOOLTIPS_EMC_ENABLE.get();
	}

	public static boolean shiftEMCTooltips() {
		return CFG_TOOLTIPS_EMC_SHIFT.get();
	}

	public static boolean showTagTooltips() {
		return CFG_TOOLTIPS_TAG_ENABLE.get();
	}
}