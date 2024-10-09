package net.fiv.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.sgui.api.gui.SimpleGui;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fiv.BorukvaInventoryBackup;
import net.fiv.config.ModConfigs;
import net.fiv.data_base.entities.DeathTable;
import net.fiv.data_base.entities.LoginTable;
import net.fiv.data_base.entities.LogoutTable;
import net.fiv.gui.*;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GetInventoryHistoryCommand {
    public static void registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("getInventoryHistory").then(CommandManager
                .argument("player", StringArgumentType.string())
                .executes(GetInventoryHistoryCommand::run))));
    }


    public static int run(CommandContext<ServerCommandSource> context){
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (Permissions.check(context.getSource(), "borukva.rollback.permission", 4)) {
            String playerName = StringArgumentType.getString(context, "player");
//            System.out.println(InventoryGui.getOfflinePlayerProfile(playerName, player.getServer()));
            try {
                if (!BorukvaInventoryBackup.getBorukvaInventoryBackupDB().playerLoginTableExist(playerName)) {
                    if (player != null) {
                        player.sendMessage(Text.literal("Такого гравця не існує"));
                    }
                    return 0;
                }

                SimpleGui tableListGui = new TableListGui(player, playerName);
                tableListGui.open();
                return 1;
            } catch (SQLException e){
                BorukvaInventoryBackup.LOGGER.warn(e.getMessage());
                return 0;
            }
        } else{
            context.getSource().sendMessage(Text.literal("Unknown command. Type \"/help\" for help."));
        }


        return 0;
    }

    public static void addDeathTableMap(ServerPlayerEntity player, String playerName){
        try{
            int maxRecords = ModConfigs.getCONFIG().getOrDefault("key.borukvaInventoryBackup.MAX_RECORDS", 100);

            double pagesCount = Math.ceil(maxRecords/44.0);

            List<DeathTable> deathTableList = BorukvaInventoryBackup.getBorukvaInventoryBackupDB().getDeathData(playerName);

            List<SimpleGui> simpleGuiLists = new LinkedList<>();

            while(pagesCount > 0){
                int endIndex = Math.min(44, deathTableList.size());

                DeathHistoryGui deathHistoryGui = new DeathHistoryGui(player);

                List<DeathTable> d = new ArrayList<>(deathTableList.subList(0, endIndex));

                deathHistoryGui.setDeathTableList(d);

                deathTableList.subList(0, endIndex).clear();
                simpleGuiLists.add(deathHistoryGui);

                pagesCount-=1;

            }

            TableListGui.activeTables.put(player.getName().getString(), simpleGuiLists);
        } catch (SQLException e){
            BorukvaInventoryBackup.LOGGER.info(e.getMessage());
        }

    }

    public static void addLogoutTableMap(ServerPlayerEntity player, String playerName){
        try{
            int maxRecords = ModConfigs.getCONFIG().getOrDefault("key.borukvaInventoryBackup.MAX_RECORDS", 100);

            double pagesCount = Math.ceil(maxRecords/44.0);

            List<LogoutTable> logoutTableList = BorukvaInventoryBackup.getBorukvaInventoryBackupDB().getLogoutData(playerName);

            List<SimpleGui> simpleGuiLists = new LinkedList<>();

            while(pagesCount > 0){
                int endIndex = Math.min(44, logoutTableList.size());

                LogoutHistoryGui logoutHistoryGui = new LogoutHistoryGui(player);

                List<LogoutTable> d = new ArrayList<>(logoutTableList.subList(0, endIndex));

                logoutHistoryGui.setLogoutTableList(d);

                logoutTableList.subList(0, endIndex).clear();
                simpleGuiLists.add(logoutHistoryGui);

                pagesCount-=1;

            }

            TableListGui.activeTables.put(player.getName().getString(), simpleGuiLists);
        }catch (SQLException e){
            BorukvaInventoryBackup.LOGGER.info(e.getMessage());
        }

    }

    public static void addLoginTableMap(ServerPlayerEntity player, String playerName){
        try{
            int maxRecords = ModConfigs.getCONFIG().getOrDefault("key.borukvaInventoryBackup.MAX_RECORDS", 100);

            double pagesCount = Math.ceil(maxRecords/44.0);

            List<LoginTable> loginTableList = BorukvaInventoryBackup.getBorukvaInventoryBackupDB().getLoginData(playerName);

            List<SimpleGui> simpleGuiLists = new LinkedList<>();

            while(pagesCount > 0){
                int endIndex = Math.min(44, loginTableList.size());

                LoginHistoryGui loginHistoryGui = new LoginHistoryGui(player);

                List<LoginTable> d = new ArrayList<>(loginTableList.subList(0, endIndex));

                loginHistoryGui.setLoginTableList(d);

                loginTableList.subList(0, endIndex).clear();
                simpleGuiLists.add(loginHistoryGui);

                pagesCount-=1;

            }

            TableListGui.activeTables.put(player.getName().getString(), simpleGuiLists);
        }catch (SQLException e){
            BorukvaInventoryBackup.LOGGER.info(e.getMessage());
        }

    }

}
