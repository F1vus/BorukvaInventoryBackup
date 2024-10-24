package net.fiv.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.sgui.api.gui.SimpleGui;
import lombok.Getter;
import net.fiv.BorukvaInventoryBackup;
import net.fiv.config.ModConfigs;
import net.fiv.data_base.BorukvaInventoryBackupDB;
import net.fiv.data_base.entities.DeathTable;
import net.fiv.data_base.entities.LoginTable;
import net.fiv.data_base.entities.LogoutTable;
import net.fiv.gui.*;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.collection.DefaultedList;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static net.fiv.BorukvaInventoryBackup.MOD_ID;

public class DatabaseManagerActor extends AbstractActor {
    @Getter
    private static BorukvaInventoryBackupDB borukvaInventoryBackupDB;

    public static Props props() {
        return Props.create(DatabaseManagerActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BActorMessages.SavePlayerDataOnPlayerDeath.class, this::onPlayerDeath)
                .match(BActorMessages.SavePlayerDataOnPlayerConnect.class, this::onPlayerConnect)
                .match(BActorMessages.SavePlayerDataOnPlayerLogout.class, this::onPlayerLogout)
                .match(BActorMessages.GetDeathTableMap.class, this::getDeathTableMap)
                .match(BActorMessages.GetLogoutTableMap.class, this::getLogoutTableMap)
                .match(BActorMessages.GetLoginTableMap.class, this::getLoginTableMap)
                .match(BActorMessages.GetInventoryHistory.class, this::getInventoryHistory)
                .match(BActorMessages.InitializeDatabase.class, this::initializeDatabase)
                .build();
    }

    private void initializeDatabase(BActorMessages.InitializeDatabase msg) {
        MinecraftServer server = msg.server();
        try {
            File worldPath = server.getSavePath(WorldSavePath.ROOT).toFile();
            File dataBaseFile = new File(worldPath, MOD_ID+".db");

            if(dataBaseFile.createNewFile()){
                BorukvaInventoryBackup.LOGGER.info("DataBase file successfully created!");
            }

            borukvaInventoryBackupDB = new BorukvaInventoryBackupDB();
        } catch (SQLException | IOException e) {
            BorukvaInventoryBackup.LOGGER.info("Faild connect to database");
            throw new RuntimeException("Error initializing database", e);
        }
    }

    public void getInventoryHistory(BActorMessages.GetInventoryHistory msg) {
        CommandContext<ServerCommandSource> context = msg.context();
            try {
                ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                String playerName = player.getName().getString();
                System.out.println("playerName" + playerName);

                if (!borukvaInventoryBackupDB.playerLoginTableExist(playerName)) {
                    context.getSource().sendMessage(Text.literal("Такого гравця не існує"));
                    return;
                }

                SimpleGui tableListGui = new TableListGui(player, playerName);
                tableListGui.open();
            } catch (SQLException e){
                BorukvaInventoryBackup.LOGGER.warn(e.getMessage());
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
    }

    private void onPlayerDeath(BActorMessages.SavePlayerDataOnPlayerDeath msg) {
        ServerPlayerEntity player = msg.player();
        DamageSource source = msg.source();

        DefaultedList<ItemStack> inventory = player.getInventory().main;
        DefaultedList<ItemStack> armor = player.getInventory().armor;
        List<ItemStack> offHand = new ArrayList<>();
        offHand.add(player.getOffHandStack());

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        String name = player.getName().getString();
        String world = player.getWorld().getRegistryKey().getValue().toString();
        String place = "%.2f %.2f %.2f".formatted(x, y, z);

        String deathTime = LocalDateTime.now().toString();
        String formattedDeathTime = deathTime.replace("T", " ").split("\\.")[0];

        String deathReason = source.getName();

        String inventr = InventoryGui.playerItems(inventory, player).toString();
        String armorString = InventoryGui.playerItems(armor, player).toString();
        String offHandString = InventoryGui.playerItems(offHand, player).toString();

        int xp = player.experienceLevel;
        try {
            borukvaInventoryBackupDB.addDataDeath(name, world, place, formattedDeathTime, deathReason, inventr, armorString, offHandString,xp);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void onPlayerConnect(BActorMessages.SavePlayerDataOnPlayerConnect msg) {
        ServerPlayerEntity player = msg.player();

        DefaultedList<ItemStack> inventory = player.getInventory().main;
        DefaultedList<ItemStack> armor = player.getInventory().armor;
        List<ItemStack> offHand = new ArrayList<>();
        offHand.add(player.getOffHandStack());

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        String place = "%.2f %.2f %.2f".formatted(x, y, z);
        String name = player.getName().getString();
        String world = player.getWorld().getRegistryKey().getValue().toString();

        String loginTime = LocalDateTime.now().toString();
        String formattedLoginTime = loginTime.replace("T", " ").split("\\.")[0];

        String inventr = InventoryGui.playerItems(inventory, player).toString();
        String armorString = InventoryGui.playerItems(armor, player).toString();
        String offHandString = InventoryGui.playerItems(offHand, player).toString();

        int xp = player.experienceLevel;

        try {
            borukvaInventoryBackupDB.addDataLogin(name, world, place, formattedLoginTime, inventr, armorString, offHandString,xp);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void onPlayerLogout(BActorMessages.SavePlayerDataOnPlayerLogout msg) {
        ServerPlayerEntity player = msg.player();

        DefaultedList<ItemStack> inventory = player.getInventory().main;
        DefaultedList<ItemStack> armor = player.getInventory().armor;
        List<ItemStack> offHand = new ArrayList<>();
        offHand.add(player.getOffHandStack());

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        String place = "%.2f %.2f %.2f".formatted(x, y, z);
        String name = player.getName().getString();
        String world = player.getWorld().getRegistryKey().getValue().toString();

        String loginTime = LocalDateTime.now().toString();
        String formattedLoginTime = loginTime.replace("T", " ").split("\\.")[0];

        String inventr = InventoryGui.playerItems(inventory, player).toString();
        String armorString = InventoryGui.playerItems(armor, player).toString();
        String offHandString = InventoryGui.playerItems(offHand, player).toString();

        int xp = player.experienceLevel;

        try {
            borukvaInventoryBackupDB.addDataLogout(name, world, place, formattedLoginTime, inventr, armorString, offHandString,xp);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void getDeathTableMap(BActorMessages.GetDeathTableMap msg) {
        ServerPlayerEntity player = msg.player();
        String playerName = msg.playerName();
        try{
            int maxRecords = ModConfigs.getCONFIG().getOrDefault("key.borukvaInventoryBackup.MAX_RECORDS", 100);
            double pagesCount = Math.ceil(maxRecords/44.0);
            List<DeathTable> deathTableList = borukvaInventoryBackupDB.getDeathData(playerName);
            List<DeathHistoryGui> simpleGuiLists = new LinkedList<>();

            while(pagesCount > 0){
                int endIndex = Math.min(44, deathTableList.size());
                DeathHistoryGui deathHistoryGui = new DeathHistoryGui(player);
                List<DeathTable> d = new ArrayList<>(deathTableList.subList(0, endIndex));
                deathHistoryGui.setDeathTableList(d);
                deathTableList.subList(0, endIndex).clear();
                simpleGuiLists.add(deathHistoryGui);
                pagesCount-=1;
            }

            simpleGuiLists.getFirst().addButtons();
            simpleGuiLists.getFirst().open();
        } catch (SQLException e){
            BorukvaInventoryBackup.LOGGER.info(e.getMessage());
        }

    }

    public void getLogoutTableMap(BActorMessages.GetLogoutTableMap msg) {
        ServerPlayerEntity player = msg.player();
        String playerName = msg.playerName();
        try{
            int maxRecords = ModConfigs.getCONFIG().getOrDefault("key.borukvaInventoryBackup.MAX_RECORDS", 100);

            double pagesCount = Math.ceil(maxRecords/44.0);

            List<LogoutTable> logoutTableList = borukvaInventoryBackupDB.getLogoutData(playerName);

            List<LogoutHistoryGui> simpleGuiLists = new LinkedList<>();

            while(pagesCount > 0){
                int endIndex = Math.min(44, logoutTableList.size());

                LogoutHistoryGui logoutHistoryGui = new LogoutHistoryGui(player);

                List<LogoutTable> d = new ArrayList<>(logoutTableList.subList(0, endIndex));

                logoutHistoryGui.setLogoutTableList(d);

                logoutTableList.subList(0, endIndex).clear();
                simpleGuiLists.add(logoutHistoryGui);

                pagesCount-=1;

            }

            simpleGuiLists.getFirst().addButtons();
            simpleGuiLists.getFirst().open();
        }catch (SQLException e){
            BorukvaInventoryBackup.LOGGER.info(e.getMessage());
        }

    }

    public void getLoginTableMap(BActorMessages.GetLoginTableMap msg) {
        ServerPlayerEntity player = msg.player();
        String playerName = msg.playerName();
        try{
            int maxRecords = ModConfigs.getCONFIG().getOrDefault("key.borukvaInventoryBackup.MAX_RECORDS", 100);
            double pagesCount = Math.ceil(maxRecords/44.0);
            List<LoginTable> loginTableList = borukvaInventoryBackupDB.getLoginData(playerName);
            List<LoginHistoryGui> simpleGuiLists = new LinkedList<>();

            while(pagesCount > 0){
                int endIndex = Math.min(44, loginTableList.size());
                LoginHistoryGui loginHistoryGui = new LoginHistoryGui(player);
                List<LoginTable> d = new ArrayList<>(loginTableList.subList(0, endIndex));
                loginHistoryGui.setLoginTableList(d);
                loginTableList.subList(0, endIndex).clear();
                simpleGuiLists.add(loginHistoryGui);
                pagesCount-=1;
            }

            simpleGuiLists.getFirst().addButtons();
            simpleGuiLists.getFirst().open();
        }   catch (SQLException e){
            BorukvaInventoryBackup.LOGGER.info(e.getMessage());
        }

    }
}
