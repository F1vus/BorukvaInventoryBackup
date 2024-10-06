package net.fiv;

import lombok.Getter;
import net.fabricmc.api.ModInitializer;

import net.fiv.commands.GetInventoryHistoryCommand;
import net.fiv.config.ModConfigs;
import net.fiv.data_base.BorukvaInventoryBackupDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class BorukvaInventoryBackup implements ModInitializer {
	public static final String MOD_ID = "borukva_inventory_backup";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	@Getter
    private static BorukvaInventoryBackupDB borukvaInventoryBackupDB;

	@Override
	public void onInitialize() {
		GetInventoryHistoryCommand.registerCommand();
		ModConfigs.registerConfigs();
		try {
			borukvaInventoryBackupDB = new BorukvaInventoryBackupDB();
		} catch (SQLException e) {
			LOGGER.info("Faild connect to database");
			throw new RuntimeException("Error initializing database", e);
		}
	}

}