package net.fiv.actor;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public final class BActorMessages {

    public record SavePlayerDataOnPlayerDeath(ServerPlayerEntity player, DamageSource source) {}
    public record SavePlayerDataOnPlayerConnect(ServerPlayerEntity player) {}
    public record SavePlayerDataOnPlayerLogout(ServerPlayerEntity player) {}
    public record GetDeathTableMap(ServerPlayerEntity player, String playerName) {}
    public record GetLogoutTableMap(ServerPlayerEntity player, String playerName) {}
    public record GetLoginTableMap(ServerPlayerEntity player, String playerName) {}
    public record GetInventoryHistory(CommandContext<ServerCommandSource> context) {}
    public record InitializeDatabase(MinecraftServer server) {}
}
