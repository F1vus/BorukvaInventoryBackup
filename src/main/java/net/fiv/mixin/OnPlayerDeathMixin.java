package net.fiv.mixin;

import net.fiv.BorukvaInventoryBackup;

import net.fiv.gui.InventoryGui;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mixin(ServerPlayerEntity.class)
public class OnPlayerDeathMixin {
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
            ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

            BorukvaInventoryBackup.getDatabaseManagerActor().tell(
                    new EntityActorMessages.TickNewAiAndContinue(getSelf(), entity),
                    getSelf()
    }
}
