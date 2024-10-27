package net.ix.deathban;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deathban implements ModInitializer {
	public static final String MOD_ID = "deathban";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// Load configuration
		DeathbanConfig.loadConfig();

		// Load banned players on server start
		DeathbanBanManager.loadBannedPlayers();

		// Register the event listener for player deaths
		DeathbanEventHandler.registerEvents();

		// Register the broadcast event
		DeathbanBroadcast.registerBroadcast();

		// Register commands
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, idk) -> {
			DeathbanCommands.register(dispatcher);
		});
	}
}