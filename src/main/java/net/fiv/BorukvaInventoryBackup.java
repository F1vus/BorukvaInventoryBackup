package net.fiv;

import lombok.Getter;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
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
    private static BorukvaInventoryBackupDB borukvaInventoryBackupDB;

	@Override
	public void onInitialize() {
		GetInventoryHistoryCommand.registerCommand();
		ModConfigs.registerConfigs();

		ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
	}

	private void onServerStarting(MinecraftServer server) {
		SERVER = server;

		try {
			File worldPath = server.getSavePath(WorldSavePath.ROOT).toFile();
			File dataBaseFile = new File(worldPath, MOD_ID+".db");

			if(dataBaseFile.createNewFile()){
				LOGGER.info("DataBase file successfully created!");
			}

			borukvaInventoryBackupDB = new BorukvaInventoryBackupDB();
		} catch (SQLException | IOException e) {
			LOGGER.info("Faild connect to database");
			throw new RuntimeException("Error initializing database", e);
		}
	}

}