package eatawesome.inventoryexchange.config;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.neoforged.fml.loading.FMLPaths;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public final class InventoryExchangeConfig {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String FILE_NAME = "inventoryexchange.toml";
    private static final String DIR_NAME = "inventoryexchange";

    private static final String KEY_MAX_ROWS = "infinite_container_max_rows";
    private static final int DEFAULT_MAX_ROWS = 500;
    private static final int MIN_ROWS = 10;
    private static int maxInfiniteContainerRows = DEFAULT_MAX_ROWS;

    private static final String KEY_DEBUG_LOGS = "enable_debug_logs";
    private static final boolean DEFAULT_DEBUG_LOGS = false;
    private static boolean debugLogs = DEFAULT_DEBUG_LOGS;

    private static boolean loaded = false;

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
        LOGGER.info("[InventoryExchange] Loaded config: {} = {}, {} = {}",
                KEY_MAX_ROWS, getMaxInfiniteContainerRows(),
                KEY_DEBUG_LOGS, debugLogs);
    }

    private static void ensureLoaded() {
        if (!loaded) {
            load();
        }
    }

    private static void writeDefault(Path file) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            bw.write("# InventoryExchange Configuration");
            bw.newLine();
            bw.write("# Number of max rows for the infinite container (minimum " + MIN_ROWS + ").");
            bw.newLine();
            bw.write(KEY_MAX_ROWS + " = " + DEFAULT_MAX_ROWS);
            bw.newLine();
            bw.write("# Enable verbose debug logs (true/false).");
            bw.newLine();
            bw.write(KEY_DEBUG_LOGS + " = " + DEFAULT_DEBUG_LOGS);
            bw.newLine();
        }
    }

    private static void parse(Path file) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                    continue;
                }
                int eq = line.indexOf('=');
                if (eq < 0) continue;
                String key = line.substring(0, eq).trim().toLowerCase(Locale.ROOT);
                String value = line.substring(eq + 1).trim();
                if (KEY_MAX_ROWS.equals(key)) {
                    parseMaxRows(value);
                } else if (KEY_DEBUG_LOGS.equals(key)) {
                    parseDebugLogs(value);
                }
            }
        }
        maxInfiniteContainerRows = Math.max(MIN_ROWS, maxInfiniteContainerRows);
    }

    private static void parseMaxRows(String value) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed < MIN_ROWS) {
                LOGGER.warn("Config value {}={} below minimum {}, clamping.", KEY_MAX_ROWS, parsed, MIN_ROWS);
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
        if (v.equals("true") || v.equals("yes") || v.equals("1")) {
            debugLogs = true;
        } else if (v.equals("false") || v.equals("no") || v.equals("0")) {
            debugLogs = false;
        } else {
            LOGGER.warn("Invalid boolean for {}: '{}'. Using default {}", KEY_DEBUG_LOGS, value, DEFAULT_DEBUG_LOGS);
            debugLogs = DEFAULT_DEBUG_LOGS;
        }
    }
}