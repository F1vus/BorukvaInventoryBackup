package net.fiv;

import net.fabricmc.api.ModInitializer;

import net.fiv.commands.GetInventoryHistoryCommand;
import net.fiv.config.ModConfigs;
import net.fiv.data_base.BorukvaInventoryBackupDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class BorukvaInventoryBackup implements ModInitializer {
	public static final String MOD_ID = "borukva_inventory_backup";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static BorukvaInventoryBackupDB borukvaInventoryBackupDB;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		GetInventoryHistoryCommand.registerCommand();
		ModConfigs.registerConfigs();
		try {
			// Ensure the plugin's data folder exists

			borukvaInventoryBackupDB = new BorukvaInventoryBackupDB();
		} catch (SQLException e) {
			e.printStackTrace();
			LOGGER.info("Faild connect to database");
		}
		LOGGER.info("Hello Fabric world!");
	}

	public static BorukvaInventoryBackupDB getBorukvaDeathBackupDB(){
		return borukvaInventoryBackupDB;
	}
}