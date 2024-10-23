package net.fiv;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import lombok.Getter;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fiv.actor.DatabaseManagerActor;
import net.fiv.commands.GetInventoryHistoryCommand;
import net.fiv.config.ModConfigs;
import net.fiv.data_base.BorukvaInventoryBackupDB;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class BorukvaInventoryBackup implements ModInitializer {
	public static final String MOD_ID = "borukva_inventory_backup";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static MinecraftServer SERVER;
	@Getter
	private static ActorSystem actorSystem;
	@Getter
	private static ActorRef databaseManagerActor;

	@Override
	public void onInitialize() {
		GetInventoryHistoryCommand.registerCommand();
		ModConfigs.registerConfigs();

		actorSystem = ActorSystem.create("BorukvaInventoryBackupActorSystem");
		databaseManagerActor = actorSystem.actorOf(DatabaseManagerActor.props(), "databaseManagerActor");

		ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
	}

	private void onServerStarting(MinecraftServer server) {
		SERVER = server;
	}

}