package net.fiv.commands;

import akka.actor.ActorRef;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fiv.BorukvaInventoryBackup;
import net.fiv.actor.BActorMessages;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class GetInventoryHistoryCommand {
    public static void registerCommandOfflinePlayer() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                CommandManager.literal("binvbackup")
                        .requires(Permissions.require("borukva.rollback", 4))
                        .then(CommandManager
                                .argument("player", StringArgumentType.string())
                                .suggests(PLAYER_NAME_SUGGESTIONS)
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

    public static void getPreRestoreTableMap(ServerPlayerEntity player, String playerName){
        BorukvaInventoryBackup.getDatabaseManagerActor().tell(
                new BActorMessages.GetPreRestoreTableMap(player, playerName), ActorRef.noSender());
    }

    public static final SuggestionProvider<ServerCommandSource> PLAYER_NAME_SUGGESTIONS = (context, builder) -> {
        MinecraftServer server = context.getSource().getServer();

        // Suggest player names
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            builder.suggest(player.getName().getString());
        }

        return builder.buildFuture();
    };

}
