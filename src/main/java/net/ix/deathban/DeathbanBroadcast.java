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

    // Called every server tick (20 times per second)
    private static void onServerTick(MinecraftServer server) {
        ticksUntilNextBroadcast--;

        if (ticksUntilNextBroadcast <= 0) {
            // Begin the broadcast message with the header
            StringBuilder broadcastBuilder = new StringBuilder();
            broadcastBuilder.append("§k-§r §c§lList of banned players: §r§k-§r\n");

            // Get the list of banned players
            Map<UUID, Long> bannedPlayers = DeathbanBanManager.getBannedPlayers();

            // Iterate over the banned players and add each player's info to the message
            for (Map.Entry<UUID, Long> entry : bannedPlayers.entrySet()) {
                UUID playerUUID = entry.getKey();
                Long banEndTime = entry.getValue();

                // Calculate remaining time
                long remainingTime = banEndTime - System.currentTimeMillis() / 1000; // Convert to seconds
                String remainingTimeString = DeathbanUtils.formatTime(remainingTime);

                // Get username from UUID
                String username = DeathbanUtils.getUsernameFromUUID(server, playerUUID);

                // Append each player's details to the message
                broadcastBuilder.append("§6§l").append(username).append("§r: §a").append(remainingTimeString).append("\n");
            }

            // Broadcast the entire message at once
            broadcastMessage(server, broadcastBuilder.toString());

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
