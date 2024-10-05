package net.fiv.mixin;

import net.fiv.BorukvaInventoryBackup;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.sql.SQLException;
import java.time.LocalDateTime;

@Mixin(ServerPlayerEntity.class)
public class OnPlayerDeathMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger(BorukvaInventoryBackup.MOD_ID);

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
            PlayerEntity player = (PlayerEntity) (Object) this;

            DefaultedList<ItemStack> inventory = player.getInventory().main;
            DefaultedList<ItemStack> armor = player.getInventory().armor;
            ItemStack offHand = player.getOffHandStack();

            double x = player.getX();
            double y = player.getY();
            double z = player.getZ();

            String name = player.getName().getString();
            String world = player.getWorld().getRegistryKey().getValue().toString();
            String place = "%.2f %.2f %.2f".formatted(x, y, z);

            String deathTime = LocalDateTime.now().toString();
            String formattedDeathTime = deathTime.replace("T", " ").split("\\.")[0];

            String deathReason = source.getName();
            String inventr = inventory.toString();
            String armorString = armor.toString();
            String offHandString = offHand.toString();

            int xp = player.experienceLevel;
            try {
                BorukvaInventoryBackup.getBorukvaDeathBackupDB()
                        .addDataDeath(name, world, place, formattedDeathTime, deathReason, inventr, armorString, offHandString,xp);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

    }
}
