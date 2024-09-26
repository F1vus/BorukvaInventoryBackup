package net.fiv.config;

import com.mojang.datafixers.util.Pair;
import net.fiv.BorukvaInventoryBackup;

public class ModConfigs {
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
    }

    public static SimpleConfig getCONFIG(){
        return CONFIG;
    }
}