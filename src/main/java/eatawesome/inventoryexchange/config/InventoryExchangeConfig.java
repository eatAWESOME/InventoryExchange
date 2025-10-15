package eatawesome.inventoryexchange.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class InventoryExchangeConfig {

	// Spec
	public static final ModConfigSpec COMMON_SPEC;
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
	public static final ModConfigSpec.BooleanValue CFG_DEBUG_LOGS;
	public static final ModConfigSpec.IntValue CFG_MAX_ROWS;

	// File
	private static final Logger LOGGER = LogUtils.getLogger();

	// Keys
	private static final String KEY_MAX_ROWS = "infinite_container_max_rows";
	private static final String KEY_DEBUG_LOGS = "enable_debug_logs";

	// Translations
	private static final String TK_DEBUG = "config.inventoryexchange.enable_debug_logs";
	private static final String TK_DEBUG_DESC = "config.inventoryexchange.enable_debug_logs.desc";
	private static final String TK_MAX_ROWS = "config.inventoryexchange.infinite_container_max_rows";
	private static final String TK_MAX_ROWS_DESC = "config.inventoryexchange.infinite_container_max_rows.desc";

	// Defaults
	private static final boolean DEFAULT_DEBUG_LOGS = false;
	private static final int DEFAULT_MAX_ROWS = 500;
	private static final int MIN_ROWS = 10;

	// Translations
	private static final Map<String, String> TRANSLATION_CACHE = new ConcurrentHashMap<>();
	private static boolean translationsLoaded = false;

	static {
		CFG_DEBUG_LOGS = BUILDER
			.comment(translate(TK_DEBUG_DESC))
			.translation(TK_DEBUG)
			.define(KEY_DEBUG_LOGS, DEFAULT_DEBUG_LOGS);

		CFG_MAX_ROWS = BUILDER
			.comment(translate(TK_MAX_ROWS_DESC))
			.translation(TK_MAX_ROWS)
			.defineInRange(KEY_MAX_ROWS, DEFAULT_MAX_ROWS, MIN_ROWS, Integer.MAX_VALUE);

		COMMON_SPEC = BUILDER.build();
	}

	private InventoryExchangeConfig() {}

	public static int getMaxInfiniteContainerRows() {
		return Math.max(MIN_ROWS, CFG_MAX_ROWS.get());
	}

	public static boolean isDebugLoggingEnabled() {
		return CFG_DEBUG_LOGS.get();
	}

	private static void loadTranslationsIfNeeded() {
		if (translationsLoaded) return;
		translationsLoaded = true;
		final String resourcePath = "assets/inventoryexchange/lang/en_us.json";
		try (InputStream in = InventoryExchangeConfig.class.getClassLoader().getResourceAsStream(resourcePath)) {
			if (in == null) {
				LOGGER.warn("Could not find translation resource: {}", resourcePath);
				return;
			}
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
				Gson gson = new Gson();
				JsonElement root = gson.fromJson(reader, JsonElement.class);
				if (root != null && root.isJsonObject()) {
					JsonObject obj = root.getAsJsonObject();
					for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
						if (e.getValue().isJsonPrimitive()) {
							TRANSLATION_CACHE.put(e.getKey(), e.getValue().getAsString());
						}
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.warn("Failed to load translations for config comments: {}", ex.getMessage());
		}
	}

	private static String translate(String key) {
		loadTranslationsIfNeeded();
		return TRANSLATION_CACHE.getOrDefault(key, key);
	}
}