package net.fiv.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fiv.BorukvaInventoryBackup;
import net.fiv.config.ModConfigs;
import net.fiv.data_base.entities.DeathTable;
import net.fiv.gui.DeathHistoryGui;
import net.fiv.gui.TableListGui;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GetInventoryHistoryCommand {

    public static final Logger LOGGER = LoggerFactory.getLogger(BorukvaInventoryBackup.MOD_ID);

    public static void registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("getInventoryHistory").then(CommandManager
                    .argument("player", StringArgumentType.string())
                    .executes(GetInventoryHistoryCommand::run)));

        });
    }


    public static int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException{
        ServerPlayerEntity player = context.getSource().getPlayer();
        String playerName = StringArgumentType.getString(context, "player");
        addDeathTableMap(player, playerName);
        SimpleGui tableListGui = new TableListGui(player, playerName);
        tableListGui.open();
        return 1;
    }

    private static void addDeathTableMap(ServerPlayerEntity player, String playerName){
        try{
            int maxRecords = ModConfigs.getCONFIG().getOrDefault("key.borukvaInventoryBackup.MAX_RECORDS", 100);

            double pagesCount = Math.ceil(maxRecords/44.0);

            List<DeathTable> deathTableList = BorukvaInventoryBackup.getBorukvaDeathBackupDB().getDeathData(playerName);

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
        }catch (SQLException e){
            LOGGER.info(e.getMessage());
        }

    }

}
