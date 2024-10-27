package net.ix.deathban;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.UserCache;

import java.util.Optional;
import java.util.UUID;

// *sigh* yes, a "utility" class. Cry me a river.

public class DeathbanUtils {

    // Format time in a user-friendly way
    public static String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;

        return String.format("%d hours, %d minutes, and %d seconds", hours, minutes, remainingSeconds);
    }

    public static UUID getOfflinePlayerUUID(ServerCommandSource source, String username) {
        MinecraftServer server = source.getServer();
        UserCache userCache = server.getUserCache();

        // Retrieve the GameProfile from the user cache using the username
        Optional<GameProfile> profileOptional = userCache.findByName(username);

        // If a profile is found, return the UUID; otherwise, return null
        return profileOptional.map(GameProfile::getId).orElse(null);
    }

    public static String getUsernameFromUUID(MinecraftServer server, UUID playerUUID) {
        UserCache userCache = server.getUserCache();

        // Retrieve the GameProfile from the user cache using the username
        Optional<GameProfile> profileOptional = userCache.getByUuid(playerUUID);

        // If a profile is found, return the UUID; otherwise, return null
        return profileOptional.map(GameProfile::getName).orElse(null);
    }
}
