package net.fiv.config;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import net.fiv.BorukvaInventoryBackup;

public class ModConfigs {
    @Getter
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static String TEST;
    public static int SOME_INT;
    public static double SOME_DOUBLE;
    public static int MAX_DAMAGE_DOWSING_ROD;

    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(BorukvaInventoryBackup.MOD_ID + "config").provider(configs).request();

    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("key.borukvaInventoryBackup.MAX_RECORDS", 100), "int");
        configs.addKeyValuePair(new Pair<>("key.borukvaInventoryBackup.DATABASE_TYPE", "H2"), "Select DB: H2(default) or MySQL");
        configs.addKeyValuePair(new Pair<>("key.borukvaInventoryBackup.DB_URL", "jdbc:mysql://localhost:3306/mydatabase"), "URL to MySQL db. Not work if choice H2");
        configs.addKeyValuePair(new Pair<>("key.borukvaInventoryBackup.DB_USER", "user"), "USER to MySQL db. Not work if choice H2");
        configs.addKeyValuePair(new Pair<>("key.borukvaInventoryBackup.DB_PASSWORD", "password"), "PASSWORD to MySQL db. Not work if choice H2");
    }

}