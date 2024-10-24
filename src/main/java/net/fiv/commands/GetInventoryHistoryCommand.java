package net.fiv.commands;

import akka.actor.ActorRef;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fiv.BorukvaInventoryBackup;
import net.fiv.actor.BActorMessages;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class GetInventoryHistoryCommand {
    public static void registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                CommandManager.literal("getInventoryHistory")
                        .requires(Permissions.require("borukva.rollback.permission", 4))
                        .then(CommandManager
                                .argument("player", StringArgumentType.string())
                                .executes(GetInventoryHistoryCommand::getInventoryHistory))));
    }

    public static int getInventoryHistory(CommandContext<ServerCommandSource> context){
        BorukvaInventoryBackup.getDatabaseManagerActor().tell(
                new BActorMessages.GetInventoryHistory(context), ActorRef.noSender());
        return 1;
    }

    public static void getDeathTableMap(ServerPlayerEntity player, String playerName){
        BorukvaInventoryBackup.getDatabaseManagerActor().tell(
                new BActorMessages.GetDeathTableMap(player, playerName), ActorRef.noSender());
    }

    public static void getLogoutTableMap(ServerPlayerEntity player, String playerName){
        BorukvaInventoryBackup.getDatabaseManagerActor().tell(
                new BActorMessages.GetLogoutTableMap(player, playerName), ActorRef.noSender());
    }

    public static void getLoginTableMap(ServerPlayerEntity player, String playerName){
        BorukvaInventoryBackup.getDatabaseManagerActor().tell(
                new BActorMessages.GetLoginTableMap(player, playerName), ActorRef.noSender());
    }

}
