package net.fiv.actor;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ActorMessages {

    public record savePlayerDataOnDeath(ServerPlayerEntity player, DamageSource source) {}
}
