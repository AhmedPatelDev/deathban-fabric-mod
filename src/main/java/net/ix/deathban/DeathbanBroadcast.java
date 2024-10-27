package net.ix.deathban;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import net.minecraft.util.UserCache;

import static net.ix.deathban.DeathbanEventHandler.formatTime;

public class DeathbanBroadcast {

    // TODO: add these to the config
    private static final int BROADCAST_INTERVAL_TICKS = (60 * 10) * 20; // 600 seconds (10 minutes) * 20 ticks per second
    private static int ticksUntilNextBroadcast = BROADCAST_INTERVAL_TICKS;

    // Register the server tick event for broadcasting
    public static void registerBroadcast() {
        ServerTickEvents.END_SERVER_TICK.register(DeathbanBroadcast::onServerTick);
    }

    // TODO: Dont broadcast for each individual row, rather, concat into one broadcast
    // Called every server tick (20 times per second)
    private static void onServerTick(MinecraftServer server) {
        ticksUntilNextBroadcast--;

        if (ticksUntilNextBroadcast <= 0) {
            // Broadcast the message to all players
            broadcastMessage(server, "§k-§r §c§lList of banned players: §r§k-§r");

            // Get the list of banned players
            Map<UUID, Long> bannedPlayers = DeathbanBanManager.getBannedPlayers();

            // Iterate over the banned players and broadcast their usernames and remaining ban time
            for (Map.Entry<UUID, Long> entry : bannedPlayers.entrySet()) {
                UUID playerUUID = entry.getKey();
                Long banEndTime = entry.getValue();

                // Calculate remaining time
                long remainingTime = banEndTime - System.currentTimeMillis() / 1000; // Convert to seconds
                // TODO: formattime needs a better place
                String remainingTimeString = formatTime(remainingTime);

                // Get username from UUID
                String username = getUsernameFromUUID(server, playerUUID);

                // Broadcast the username and remaining time
                String message = "§6§l" + username + "§r: §a" + remainingTimeString;
                broadcastMessage(server, message);
            }

            // Reset the counter
            ticksUntilNextBroadcast = BROADCAST_INTERVAL_TICKS;
        }
    }

    // TODO: this needs a better place
    private static String getUsernameFromUUID(MinecraftServer server, UUID playerUUID) {
        UserCache userCache = server.getUserCache();

        // Retrieve the GameProfile from the user cache using the username
        Optional<GameProfile> profileOptional = userCache.getByUuid(playerUUID);

        // If a profile is found, return the UUID; otherwise, return null
        return profileOptional.map(GameProfile::getName).orElse(null);
    }

    // Broadcast a message to all players on the server
    public static void broadcastMessage(MinecraftServer server, String message) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.sendMessage(Text.literal(message), false);
        }
    }
}
