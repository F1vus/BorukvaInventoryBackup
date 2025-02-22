package net.fiv.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.sgui.api.gui.SimpleGui;
import lombok.Getter;
import net.fiv.BorukvaInventoryBackup;
import net.fiv.config.ModConfigs;
import net.fiv.data_base.BorukvaInventoryBackupDB;
import net.fiv.data_base.entities.DeathTable;
import net.fiv.data_base.entities.LoginTable;
import net.fiv.data_base.entities.LogoutTable;
import net.fiv.data_base.entities.PreRestoreTable;
import net.fiv.gui.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
                .match(BActorMessages.SavePlayerDataOnPlayerRestore.class, this::onPlayerRestore)
                .match(BActorMessages.SavePlayerDataOnPlayerRestoreNbt.class, this::onPlayerRestoreNbt)
                .match(BActorMessages.GetDeathTableMap.class, this::getDeathTableMap)
                .match(BActorMessages.GetLogoutTableMap.class, this::getLogoutTableMap)
                .match(BActorMessages.GetLoginTableMap.class, this::getLoginTableMap)
                .match(BActorMessages.GetPreRestoreTableMap.class, this::getPreRestoreTableMap)
                .match(BActorMessages.GetInventoryHistory.class, this::getInventoryHistory)
                .match(BActorMessages.InitializeDatabase.class, this::initializeDatabase)
                .build();
    }

    private void initializeDatabase(BActorMessages.InitializeDatabase msg) {
//        MinecraftServer server = msg.server();
        try {
//            File worldPath = server.getSavePath(WorldSavePath.ROOT).toFile();
//            File dataBaseFile = new File(worldPath, MOD_ID+".db");
//
//            if(dataBaseFile.createNewFile()){
//                BorukvaInventoryBackup.LOGGER.info("DataBase file successfully created!");
//            }
            String dbType = ModConfigs.getCONFIG().getOrDefault("key.borukvaInventoryBackup.DATABASE", "H2");

            if("h2".equalsIgnoreCase(dbType)){

                borukvaInventoryBackupDB = new BorukvaInventoryBackupDB();
            } else {
                String url = ModConfigs.getCONFIG().getOrDefault("key.borukvaInventoryBackup.DB_URL", "");
                String user = ModConfigs.getCONFIG().getOrDefault("key.borukvaInventoryBackup.DB_USER", "");
                String password = ModConfigs.getCONFIG().getOrDefault("key.borukvaInventoryBackup.DB_PASSWORD", "");

                borukvaInventoryBackupDB = new BorukvaInventoryBackupDB(url, user, password);
            }

            BorukvaInventoryBackup.LOGGER.info("DataBase file successfully initialized!");
        } catch (SQLException e) {
            BorukvaInventoryBackup.LOGGER.info("Fail connect to database!");
            throw new SQLExceptionWrapper(e);
        }
    }



    public void getInventoryHistory(BActorMessages.GetInventoryHistory msg) {
        CommandContext<ServerCommandSource> context = msg.context();
        try {
            ServerPlayerEntity player = context.getSource().getPlayer();
            String playerName = StringArgumentType.getString(context, "player");
            //System.out.println("playerName" + playerName);

            if (!borukvaInventoryBackupDB.playerLoginTableExist(playerName)) {
                context.getSource().sendMessage(Text.literal("There is no such player!").formatted(Formatting.RED, Formatting.BOLD));
                return;
            }

            SimpleGui tableListGui = new TableListGui(player, playerName);
            tableListGui.open();
        } catch (SQLException e){
            BorukvaInventoryBackup.LOGGER.warn(e.getMessage());
            throw new SQLExceptionWrapper(e);
        }
    }

    private void onPlayerDeath(BActorMessages.SavePlayerDataOnPlayerDeath msg) {
        ServerPlayerEntity player = msg.player();
        DamageSource source = msg.source();

        DefaultedList<ItemStack> inventory = player.getInventory().main;
        DefaultedList<ItemStack> armor = player.getInventory().armor;
        DefaultedList<ItemStack> enderChest = player.getEnderChestInventory().heldStacks;
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
        String enderChestString =  InventoryGui.playerItems(enderChest, player).toString();
        //System.out.println("DeathEnder: "+enderChestString);

        int xp = player.experienceLevel;
        try {
            borukvaInventoryBackupDB.addDataDeath(name, world, place, formattedDeathTime, deathReason, inventr, armorString, offHandString, enderChestString, xp);
        } catch (SQLException e) {
            throw new SQLExceptionWrapper(e);
        }

    }

    private void onPlayerConnect(BActorMessages.SavePlayerDataOnPlayerConnect msg) {
        ServerPlayerEntity player = msg.player();

        DefaultedList<ItemStack> inventory = player.getInventory().main;
        //System.out.println("DefaultedList: "+inventory);
        DefaultedList<ItemStack> armor = player.getInventory().armor;
        DefaultedList<ItemStack> enderChest = player.getEnderChestInventory().heldStacks;
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
        String enderChestString =  InventoryGui.playerItems(enderChest, player).toString();
        //System.out.println("Inventr:"+inventr);

        int xp = player.experienceLevel;

        try {
            borukvaInventoryBackupDB.addDataLogin(name, world, place, formattedLoginTime, inventr, armorString, offHandString,enderChestString,xp);
        } catch (SQLException e) {
            throw new SQLExceptionWrapper(e);
        }
    }

    private void onPlayerLogout(BActorMessages.SavePlayerDataOnPlayerLogout msg) {
        ServerPlayerEntity player = msg.player();

        DefaultedList<ItemStack> inventory = player.getInventory().main;
        DefaultedList<ItemStack> armor = player.getInventory().armor;
        DefaultedList<ItemStack> enderChest = player.getEnderChestInventory().heldStacks;
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
        String enderChestString =  InventoryGui.playerItems(enderChest, player).toString();

        int xp = player.experienceLevel;

        try {
            borukvaInventoryBackupDB.addDataLogout(name, world, place, formattedLoginTime, inventr, armorString, offHandString, enderChestString,xp);
        } catch (SQLException e) {
            throw new SQLExceptionWrapper(e);
        }
    }

    private void onPlayerRestore(BActorMessages.SavePlayerDataOnPlayerRestore msg){
        String loginTime = LocalDateTime.now().toString();
        String formattedLoginTime = loginTime.replace("T", " ").split("\\.")[0];

        try{
            borukvaInventoryBackupDB.addDataPreRestore(msg.playerName(), formattedLoginTime, msg.inventory(), msg.armor(), msg.offHand(), msg.enderChest(), msg.xp());
        }catch (SQLException e){
            throw new SQLExceptionWrapper(e);
        }
    }

    private void onPlayerRestoreNbt(BActorMessages.SavePlayerDataOnPlayerRestoreNbt msg){
        NbtList mainInventory = new NbtList();
        NbtList armor = new NbtList();
        NbtList offHand = new NbtList();

        for(NbtElement nbtElement: msg.inventory()) {
            NbtCompound itemNbt = (NbtCompound) nbtElement;
            if(itemNbt.getByte("Slot") >= (byte) 100){
              armor.add(itemNbt);
            } else if(itemNbt.getByte("Slot") >= (byte) -106){
                offHand.add(itemNbt);
            } else {
                mainInventory.add(itemNbt);
            }

        }

        String loginTime = LocalDateTime.now().toString();
        String formattedLoginTime = loginTime.replace("T", " ").split("\\.")[0];

        try{
            borukvaInventoryBackupDB.addDataPreRestore(msg.playerName(), formattedLoginTime, mainInventory.toString(), armor.toString(), offHand.toString(), msg.enderChest().toString(), msg.xp());
        }catch (SQLException e){
            throw new SQLExceptionWrapper(e);
        }
    }


    public void getDeathTableMap(BActorMessages.GetDeathTableMap msg) {
        ServerPlayerEntity player = msg.player();
        String playerName = msg.playerName();
        try{
            List<DeathTable> deathTableList = borukvaInventoryBackupDB.getDeathData(playerName);
            if(deathTableList == null){
                player.sendMessage(Text.literal("There are no records of this player in the death database!"));

            } else {
                new DeathHistoryGui(player, 0, deathTableList).open();
            }
        } catch (SQLException e){
            BorukvaInventoryBackup.LOGGER.info(e.getMessage());
            throw new SQLExceptionWrapper(e);
        }

    }

    public void getLogoutTableMap(BActorMessages.GetLogoutTableMap msg) {
        ServerPlayerEntity player = msg.player();
        String playerName = msg.playerName();
        try{
            List<LogoutTable> logoutTableList = borukvaInventoryBackupDB.getLogoutData(playerName);
            if(logoutTableList == null){
                player.sendMessage(Text.literal("There are no records of this player in the logout database!"));

            } else {
                new LogoutHistoryGui(player, 0, logoutTableList).open();
            }

        } catch (SQLException e){
            BorukvaInventoryBackup.LOGGER.info(e.getMessage());
            throw new SQLExceptionWrapper(e);
        }

    }

    public void getLoginTableMap(BActorMessages.GetLoginTableMap msg) {
        ServerPlayerEntity player = msg.player();
        String playerName = msg.playerName();

        try{
            List<LoginTable> loginTableList = borukvaInventoryBackupDB.getLoginData(playerName);

            new LoginHistoryGui(player, 0, loginTableList).open();
        } catch (SQLException e){
            BorukvaInventoryBackup.LOGGER.info(e.getMessage());
            throw new SQLExceptionWrapper(e);
        }

    }

    public void getPreRestoreTableMap(BActorMessages.GetPreRestoreTableMap msg){
        ServerPlayerEntity player = msg.player();
        String playerName = msg.playerName();
        try{
            List<PreRestoreTable> preRestoreTableList = borukvaInventoryBackupDB.getPreRestoreData(playerName);
            if(preRestoreTableList == null){
                player.sendMessage(Text.literal("There are no records of this player in the pre_restore database!"));

            } else {
                new PreRestoreGui(player, 0, preRestoreTableList).open();
            }

        } catch (SQLException e){
            BorukvaInventoryBackup.LOGGER.info(e.getMessage());
            throw new SQLExceptionWrapper(e);
        }
    }
}