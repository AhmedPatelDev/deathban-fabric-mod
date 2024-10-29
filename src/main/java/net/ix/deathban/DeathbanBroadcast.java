package net.ix.deathban;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.UUID;

public class DeathbanBroadcast {

    static long broadcastIntervalTicks = DeathbanConfig.broadcastIntervalTicks;
    private static long ticksUntilNextBroadcast = broadcastIntervalTicks;

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
                String remainingTimeString = DeathbanUtils.formatTime(remainingTime);

                // Get username from UUID
                String username = DeathbanUtils.getUsernameFromUUID(server, playerUUID);

                // Broadcast the username and remaining time
                String message = "§6§l" + username + "§r: §a" + remainingTimeString;
                broadcastMessage(server, message);
            }

            // Reset the counter
            ticksUntilNextBroadcast = broadcastIntervalTicks;
        }
    }

    // Broadcast a message to all players on the server
    public static void broadcastMessage(MinecraftServer server, String message) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.sendMessage(Text.literal(message), false);
        }
    }
}
