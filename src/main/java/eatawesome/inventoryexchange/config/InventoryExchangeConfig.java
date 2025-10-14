package eatawesome.inventoryexchange.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.neoforged.fml.loading.FMLPaths;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class InventoryExchangeConfig {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String FILE_NAME = "inventoryexchange.toml";
    private static final String DIR_NAME = "inventoryexchange";

    // Config keys
    private static final String KEY_MAX_ROWS = "infinite_container_max_rows";
    private static final String KEY_DEBUG_LOGS = "enable_debug_logs";

    // Translation keys
    private static final String TK_DEBUG        = "config.inventoryexchange.enable_debug_logs";
    private static final String TK_DEBUG_DESC   = "config.inventoryexchange.enable_debug_logs.desc";
    private static final String TK_MAX_ROWS     = "config.inventoryexchange.infinite_container_max_rows";
    private static final String TK_MAX_ROWS_DESC= "config.inventoryexchange.infinite_container_max_rows.desc";

    // Defaults / constraints
    private static final boolean DEFAULT_DEBUG_LOGS = false;
    private static final int DEFAULT_MAX_ROWS = 500;
    private static final int MIN_ROWS = 10;

    private static boolean debugLogs = DEFAULT_DEBUG_LOGS;
    private static int maxInfiniteContainerRows = DEFAULT_MAX_ROWS;

    private static boolean loaded = false;

    // Translation cache
    private static final Map<String, String> TRANSLATION_CACHE = new ConcurrentHashMap<>();
    private static boolean translationsLoaded = false;

    private InventoryExchangeConfig() {}

    public static int getMaxInfiniteContainerRows() {
        ensureLoaded();
        return Math.max(MIN_ROWS, maxInfiniteContainerRows);
    }

    public static boolean isDebugLoggingEnabled() {
        ensureLoaded();
        return debugLogs;
    }

    public static synchronized void load() {
        if (loaded) {
            return;
        }
        Path dir = FMLPaths.CONFIGDIR.get().resolve(DIR_NAME);
        Path file = dir.resolve(FILE_NAME);
        try {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            if (!Files.exists(file)) {
                writeDefault(file);
            }
            parse(file);
        } catch (IOException e) {
            LOGGER.error("Failed to load InventoryExchange config, using defaults.", e);
            maxInfiniteContainerRows = DEFAULT_MAX_ROWS;
            debugLogs = DEFAULT_DEBUG_LOGS;
        }
        loaded = true;
    }

    private static void ensureLoaded() {
        if (!loaded) {
            load();
        }
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

    private static void writeDefault(Path file) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            bw.write("# InventoryExchange Configuration");
            bw.newLine();
            writeEntry(bw, TK_DEBUG, TK_DEBUG_DESC, KEY_DEBUG_LOGS, String.valueOf(DEFAULT_DEBUG_LOGS));
            bw.newLine();
            writeEntry(bw, TK_MAX_ROWS, TK_MAX_ROWS_DESC, KEY_MAX_ROWS, String.valueOf(DEFAULT_MAX_ROWS));
        }
    }

    private static void writeEntry(BufferedWriter bw,
                                   String titleKey,
                                   String descKey,
                                   String propKey,
                                   String defaultValue) throws IOException {
        String title = translate(titleKey);
        String desc  = translate(descKey);
        bw.write("# " + title);
        bw.newLine();
        bw.write("# " + desc);
        bw.newLine();
        bw.write(propKey + " = " + defaultValue);
        bw.newLine();
    }

    private static void parse(Path file) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) continue;

                int eq = line.indexOf('=');
                if (eq < 0) continue;

                String key = line.substring(0, eq).trim().toLowerCase(Locale.ROOT);
                String value = line.substring(eq + 1).trim();

                if (KEY_DEBUG_LOGS.equals(key)) {
                    parseDebugLogs(value);
                } else if (KEY_MAX_ROWS.equals(key)) {
                    parseMaxRows(value);
                }
            }
        }
        maxInfiniteContainerRows = Math.max(MIN_ROWS, maxInfiniteContainerRows);
    }

    private static void parseMaxRows(String value) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed < MIN_ROWS) {
                LOGGER.warn("{}={} below minimum {}, clamping.", KEY_MAX_ROWS, parsed, MIN_ROWS);
                parsed = MIN_ROWS;
            }
            maxInfiniteContainerRows = parsed;
        } catch (NumberFormatException ex) {
            LOGGER.warn("Invalid integer for {}: '{}'. Using default {}", KEY_MAX_ROWS, value, DEFAULT_MAX_ROWS);
            maxInfiniteContainerRows = DEFAULT_MAX_ROWS;
        }
    }

    private static void parseDebugLogs(String value) {
        String v = value.toLowerCase(Locale.ROOT);
        debugLogs = switch (v) {
            case "true", "yes", "1" -> true;
            case "false", "no", "0" -> false;
            default -> {
                LOGGER.warn("Invalid boolean for {}: '{}'. Using default {}", KEY_DEBUG_LOGS, value, DEFAULT_DEBUG_LOGS);
                yield DEFAULT_DEBUG_LOGS;
            }
        };
    }
}