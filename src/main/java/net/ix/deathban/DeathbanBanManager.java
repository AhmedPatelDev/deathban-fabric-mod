package net.ix.deathban;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeathbanBanManager {
    private static final Logger LOGGER = Logger.getLogger(DeathbanBanManager.class.getName());
    private static final File BAN_FILE = Paths.get("deathbanned-players.json").toFile();
    private static final Gson GSON = new Gson();

    // In-memory storage for banned players with unban timestamps
    private static final Map<UUID, Long> bannedPlayers = new HashMap<>();

    // Static block to load banned players on initialization
    static {
        loadBannedPlayers();
    }

    public static Map<UUID, Long> getBannedPlayers() {
        return bannedPlayers;
    }

    // Load banned players from file
    public static void loadBannedPlayers() {
        if (BAN_FILE.exists()) {
            bannedPlayers.clear();
            try (FileReader reader = new FileReader(BAN_FILE)) {
                Type type = new TypeToken<Map<UUID, Long>>() {}.getType();
                Map<UUID, Long> loadedData = GSON.fromJson(reader, type);
                bannedPlayers.putAll(loadedData);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to load banned players file", e);
            }
        }
    }

    // Save banned players to file
    private static void saveBannedPlayers() {
        try (FileWriter writer = new FileWriter(BAN_FILE)) {
            GSON.toJson(bannedPlayers, writer);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save banned players file", e);
        }
    }

    // Ban a player for a specific duration in hours
    public static void banPlayer(ServerPlayerEntity player, long durationSeconds) {
        UUID playerId = player.getUuid();
        long unbanTime = Instant.now().getEpochSecond() + (durationSeconds);
        bannedPlayers.put(playerId, unbanTime);
        saveBannedPlayers();
    }

    // Ban a player for a specific duration in hours
    public static void banPlayer(UUID playerId, long durationSeconds) {
        long unbanTime = Instant.now().getEpochSecond() + (durationSeconds);
        bannedPlayers.put(playerId, unbanTime);
        saveBannedPlayers();
    }

    // Unban a player
    public static void unbanPlayer(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        if (bannedPlayers.remove(playerId) != null) {
            saveBannedPlayers();
        }
    }

    // Unban a player
    public static void unbanPlayer(UUID playerId) {
        if (bannedPlayers.remove(playerId) != null) {
            saveBannedPlayers();
        }
    }

    // Check if a player is banned and return remaining ban time in seconds
    public static long getRemainingBanTime(ServerPlayerEntity player) {
        // Re-import banned players file for manual changes
        loadBannedPlayers();

        UUID playerId = player.getUuid();
        if (bannedPlayers.containsKey(playerId)) {
            long unbanTime = bannedPlayers.get(playerId);
            long currentTime = Instant.now().getEpochSecond();

            // If the current time is past the unban time, remove the player from the banned list
            if (currentTime >= unbanTime) {
                unbanPlayer(player);
                return 0; // Not banned
            }
            return unbanTime - currentTime; // Remaining ban time
        }
        return 0; // Not banned
    }

    // Check if a player is banned
    public static boolean isPlayerBanned(ServerPlayerEntity player) {
        return getRemainingBanTime(player) > 0;
    }
}
