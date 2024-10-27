package net.ix.deathban;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import java.util.UUID;

public class DeathbanCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            CommandManager.literal("deathban")
                // Restrict to operators with permission level 2 or higher
                .requires(source -> source.hasPermissionLevel(2))

                // /deathban ban {username} {hours}
                .then(CommandManager.literal("ban")
                    .then(CommandManager.argument("username", StringArgumentType.string())
                    .then(CommandManager.argument("hours", IntegerArgumentType.integer(1))
                    .executes(context -> {
                        String username = StringArgumentType.getString(context, "username");
                        int hours = IntegerArgumentType.getInteger(context, "hours");
                        return banPlayer(context.getSource(), username, hours);
                    }))))

                // /deathban unban {username}
                .then(CommandManager.literal("unban")
                    .then(CommandManager.argument("username", StringArgumentType.string())
                    .executes(context -> {
                        String username = StringArgumentType.getString(context, "username");
                        return unbanPlayer(context.getSource(), username);
                    })))

        );
    }

    private static int banPlayer(ServerCommandSource source, String username, int hours) {
        ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(username);

        long hoursFormatted = (long) hours * 60 * 60;

        // If the player doesn't exist in the server
        if(player == null) {
            UUID playerID = DeathbanUtils.getOfflinePlayerUUID(source, username);

            // Player has never joined the server
            if(playerID == null) {
                source.sendFeedback(() -> Text.literal("User does not exist."), false);
                return Command.SINGLE_SUCCESS;
            }

            // If the player has joined the server before, continue and ban.
            source.sendFeedback(() -> Text.literal("Banned " + username + " for " + hours + " hours."), false);
            DeathbanBanManager.banPlayer(playerID, hoursFormatted);
            return Command.SINGLE_SUCCESS;
        }

        // If the player does exist in the server
        DeathbanBanManager.banPlayer(player, hoursFormatted); // Use hours to set ban duration
        player.networkHandler.disconnect(Text.literal("You died! (" + hours + " hours remaining)"));
        source.sendFeedback(() -> Text.literal("Banned " + username + " for " + hours + " hours."), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int unbanPlayer(ServerCommandSource source, String username) {
        UUID playerID = DeathbanUtils.getOfflinePlayerUUID(source, username);
        DeathbanBanManager.unbanPlayer(playerID);
        source.sendFeedback(() -> Text.literal("Unbanned " + username), false);
        return Command.SINGLE_SUCCESS;
    }
}
