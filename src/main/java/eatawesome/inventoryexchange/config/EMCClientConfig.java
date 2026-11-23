package eatawesome.inventoryexchange.config;

import eatawesome.inventoryexchange.config.value.CachedBooleanValue;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * For config options that only the client cares about
 */
public class EMCClientConfig extends BaseConfig {

	private final ModConfigSpec configSpec;

	public final CachedBooleanValue emcToolTips;
	public final CachedBooleanValue shiftEmcToolTips;
	public final CachedBooleanValue tagToolTips;

	EMCClientConfig() {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

		InventoryExchangeConfigTranslations.CLIENT_TOOLTIPS.applyToBuilder(builder).push("tooltips");
		emcToolTips = CachedBooleanValue.wrap(this, InventoryExchangeConfigTranslations.CLIENT_TOOLTIPS_EMC.applyToBuilder(builder).define("emc", true));
		shiftEmcToolTips = CachedBooleanValue.wrap(this, InventoryExchangeConfigTranslations.CLIENT_TOOLTIPS_EMC_SHIFT.applyToBuilder(builder)
				.define("shift_emc", false));
		tagToolTips = CachedBooleanValue.wrap(this, InventoryExchangeConfigTranslations.CLIENT_TOOLTIPS_TAGS.applyToBuilder(builder).define("tag", false));
		builder.pop();

		configSpec = builder.build();
	}

	@Override
	public String getFileName() {
		return "client";
	}

	@Override
	public String getTranslation() {
		return "Client Config";
	}

	@Override
	public ModConfigSpec getConfigSpec() {
		return configSpec;
	}

	@Override
	public ModConfig.Type getConfigType() {
		return ModConfig.Type.CLIENT;
	}
}