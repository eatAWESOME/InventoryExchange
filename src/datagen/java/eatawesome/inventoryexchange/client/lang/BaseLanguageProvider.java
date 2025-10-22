package eatawesome.inventoryexchange.client.lang;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.client.lang.FormatSplitter.Component;
import moze_intel.projecte.config.IPEConfig;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.NotNull;

/**
 * @apiNote From Mekanism
 */
public abstract class BaseLanguageProvider extends LanguageProvider {

	private final ConvertibleLanguageProvider[] altProviders;
	private final String modid;

	public BaseLanguageProvider(PackOutput output, String modid) {
		super(output, modid, "en_us");
		this.modid = modid;
		altProviders = new ConvertibleLanguageProvider[]{
				new UpsideDownLanguageProvider(output, modid),
				new NonAmericanLanguageProvider(output, modid, "en_au"),
				new NonAmericanLanguageProvider(output, modid, "en_gb")
		};
	}

	protected void add(eatawesome.inventoryexchange.utils.text.IHasTranslationKey key, String value) {
		add(key.getTranslationKey(), value);
	}

	protected void add(moze_intel.projecte.utils.text.IHasTranslationKey key, String value) {
		add(key.getTranslationKey(), value);
	}

	private String getConfigSectionTranslationPath(IPEConfig config) {
		String baseConfigFolder = InventoryExchange.MODNAME.toLowerCase(Locale.ROOT);
		String fileName = config.getFileName().replaceAll("[^a-zA-Z0-9]+", ".").toLowerCase(Locale.ROOT);
		return modid + ".configuration.section." + baseConfigFolder + "." + fileName + ".toml";
	}

	protected void addPEConfigs(Collection<IPEConfig> configs) {
		add(modid + ".configuration.title", InventoryExchange.MODNAME + " Config");
		for (IPEConfig config : configs) {
			String key = getConfigSectionTranslationPath(config);
			add(key, config.getTranslation());
			add(key + ".title", InventoryExchange.MODNAME + " - " + config.getTranslation());
		}
	}

	protected void addInventoryExchangeConfigs(eatawesome.inventoryexchange.config.IConfigTranslation... translations) {
		for (eatawesome.inventoryexchange.config.IConfigTranslation translation : translations) {
			add(translation, translation.title());
			add(translation.getTranslationKey() + ".tooltip", translation.tooltip());
			String button = translation.button();
			if (button != null) {
				add(translation.getTranslationKey() + ".button", button);
			}
		}
	}

	protected void addPEConfigs(moze_intel.projecte.config.IConfigTranslation... translations) {
		for (moze_intel.projecte.config.IConfigTranslation translation : translations) {
			add(translation, translation.title());
			add(translation.getTranslationKey() + ".tooltip", translation.tooltip());
			String button = translation.button();
			if (button != null) {
				add(translation.getTranslationKey() + ".button", button);
			}
		}
	}

	protected void addModInfo(String modName, String description) {
		add("fml.menu.mods.info.displayname." + modid, modName);
		add("fml.menu.mods.info.description." + modid, description);
	}

	/*protected void addAliases(IAliasedTranslation... translations) {
		for (IAliasedTranslation translation : translations) {
			add(translation, translation.getAlias());
		}
	}*/

	protected void addAlias(String path, String translation) {
		add(Util.makeDescriptionId("alias", ResourceLocation.fromNamespaceAndPath(modid, path)), translation);
	}

	@Override
	public void add(@NotNull String key, @NotNull String value) {
		super.add(key, value);
		if (altProviders.length > 0) {
			List<Component> splitEnglish = FormatSplitter.split(value);
			for (ConvertibleLanguageProvider provider : altProviders) {
				provider.convert(key, value, splitEnglish);
			}
		}
	}

	@NotNull
	@Override
	public CompletableFuture<?> run(@NotNull CachedOutput cache) {
		CompletableFuture<?> future = super.run(cache);
		if (altProviders.length > 0) {
			CompletableFuture<?>[] futures = new CompletableFuture[altProviders.length + 1];
			futures[0] = future;
			for (int i = 0; i < altProviders.length; i++) {
				futures[i + 1] = altProviders[i].run(cache);
			}
			return CompletableFuture.allOf(futures);
		}
		return future;
	}
}