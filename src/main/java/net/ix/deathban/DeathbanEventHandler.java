package net.ix.deathban;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class DeathbanEventHandler {

    static long defaultBanSeconds = DeathbanConfig.defaultBanSeconds;

    // Register all event listeners
    public static void registerEvents() {
        registerDeathEvent();
        registerJoinEvent();
    }

    // Register death event listener
    private static void registerDeathEvent() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {

            if (entity instanceof ServerPlayerEntity player) {
                // Ban the player for the default duration
                DeathbanBanManager.banPlayer(player, defaultBanSeconds);

                // Get banned time
                long remainingBanTime = DeathbanBanManager.getRemainingBanTime(player);
                String formattedTime = formatTime(remainingBanTime);

                // Notify the player and kick them from the server
                player.networkHandler.disconnect(Text.literal("You died! (" + formattedTime + " remaining)"));
            }

        });
    }

    // Register join event listener
    private static void registerJoinEvent() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {

            ServerPlayerEntity player = handler.player;
            long remainingBanTime = DeathbanBanManager.getRemainingBanTime(player);
            if (remainingBanTime > 0) {
                String formattedTime = formatTime(remainingBanTime);

                // Schedule a kick to fix the "fake disconnect" issue.
                DeathbanTaskScheduler.scheduleTask(() -> {
                    if (player.networkHandler != null && !player.isDisconnected()) {
                        player.networkHandler.disconnect(Text.literal("You are banned for " + formattedTime + " due to a recent death!"));
                    }
                }, 40); // 40 ticks = 2 seconds (should be enough time for the server to register that the player has connected.)
            }

        });
    }

    // Format time in a user-friendly way
    public static String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;

        return String.format("%d hours, %d minutes, and %d seconds", hours, minutes, remainingSeconds);
    }
}
