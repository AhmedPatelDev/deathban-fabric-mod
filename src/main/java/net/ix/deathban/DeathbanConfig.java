package net.ix.deathban;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DeathbanConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/deathban_config.json");

    // Default config values
    public static long defaultBanSeconds = 129600;
    public static boolean enableBroadcast = true;
    public static long broadcastIntervalTicks = 12000;

    // Load config from file
    public static void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                if (data != null) {
                    defaultBanSeconds = data.defaultBanSeconds;
                    enableBroadcast = data.enableBroadcast;
                    broadcastIntervalTicks = data.broadcastIntervalTicks;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Create a new config file with default values if it doesn't exist
            saveDefaultConfig();
        }
    }

    // Save the current config to file
    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            ConfigData data = new ConfigData();
            data.defaultBanSeconds = defaultBanSeconds;
            data.enableBroadcast = enableBroadcast;
            data.broadcastIntervalTicks = broadcastIntervalTicks;
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Create a new config file with default values
    private static void saveDefaultConfig() {
        try {
            // Create directories if they don't exist
            if (CONFIG_FILE.getParentFile() != null) {
                CONFIG_FILE.getParentFile().mkdirs();
            }

            // Create and save the default config
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                ConfigData data = new ConfigData();
                 // Set the default value
                data.defaultBanSeconds = 129600;
                data.enableBroadcast = true;
                data.broadcastIntervalTicks = 12000;
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Internal class to store config data
    private static class ConfigData {
        long defaultBanSeconds = 129600;
        boolean enableBroadcast = true;
        long broadcastIntervalTicks = 12000;
    }
}
