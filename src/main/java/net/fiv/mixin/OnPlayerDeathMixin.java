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
            PlayerEntity player = (PlayerEntity) (Object) this;

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
