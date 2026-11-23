package eatawesome.inventoryexchange.config;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import eatawesome.inventoryexchange.InventoryExchange;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLPaths;

public class EMCConfig {

	public static final Path CONFIG_DIR = FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(InventoryExchange.MODID + "/emc"));
	private static final Map<IConfigSpec, IInventoryExchangeConfig> KNOWN_CONFIGS = new HashMap<>();

	public static final EMCClientConfig client = new EMCClientConfig();

	public static void register(ModContainer modContainer) {
		registerConfig(modContainer, client);
	}

	public static Collection<IInventoryExchangeConfig> getConfigs() {
		return Collections.unmodifiableCollection(KNOWN_CONFIGS.values());
	}

	/**
	 * Creates and register a mod config, and track it so that we can properly clear cached values.
	 */
	public static void registerConfig(ModContainer modContainer, IInventoryExchangeConfig config) {
		modContainer.registerConfig(config.getConfigType(), config.getConfigSpec(), InventoryExchange.MODID + "/emc/" + config.getFileName() + ".toml");
		KNOWN_CONFIGS.put(config.getConfigSpec(), config);
	}

	public static void onConfigLoad(ModConfigEvent event) {
		//Note: We listen to both the initial load and the reload, to make sure that we fix any accidentally
		// cached values from calls before the initial loading
		ModConfig config = event.getConfig();
		//Make sure it is for the same modid as us
		if (config.getModId().equals(InventoryExchange.MODID)) {
			IInventoryExchangeConfig peConfig = KNOWN_CONFIGS.get(config.getSpec());
			if (peConfig != null) {
				peConfig.clearCache(event instanceof ModConfigEvent.Unloading);
			}
		}
	}
}