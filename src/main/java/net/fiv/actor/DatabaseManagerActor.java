package net.fiv.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import lombok.Getter;
import net.fiv.BorukvaInventoryBackup;
import net.fiv.data_base.BorukvaInventoryBackupDB;
import net.fiv.gui.InventoryGui;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.collection.DefaultedList;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static net.fiv.BorukvaInventoryBackup.MOD_ID;

public class DatabaseManagerActor extends AbstractActor {
    @Getter
    private static BorukvaInventoryBackupDB borukvaInventoryBackupDB;

    public DatabaseManagerActor() {
        initializeDatabase();
    }

    public static Props props() {
        return Props.create(DatabaseManagerActor.class);
    }

    @Override
    public Receive createReceive() {
        return null;
    }

    private void initializeDatabase() {
        try {
            File worldPath = BorukvaInventoryBackup.SERVER.getSavePath(WorldSavePath.ROOT).toFile();
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

    private void onDeath(ServerPlayerEntity player, DamageSource source) {

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
            BorukvaInventoryBackup.getBorukvaInventoryBackupDB()
                    .addDataDeath(name, world, place, formattedDeathTime, deathReason, inventr, armorString, offHandString,xp);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
