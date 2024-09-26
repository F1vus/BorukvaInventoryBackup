package net.fiv.mixin;

import net.fiv.BorukvaInventoryBackup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.PlayerManager;
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

@Mixin(PlayerManager.class)
public class OnPlayerLogoutMixin {

    private static final Logger LOGGER = LoggerFactory.getLogger(BorukvaInventoryBackup.MOD_ID);

    @Inject(method = "remove", at = @At("HEAD"))
    private void playerRemoveMixin(ServerPlayerEntity player, CallbackInfo ci){
        DefaultedList<ItemStack> inventory = player.getInventory().main;
        DefaultedList<ItemStack> armor = player.getInventory().armor;
        ItemStack offHand = player.getOffHandStack();

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        String place = "%.2f %.2f %.2f".formatted(x, y, z);
        String name = player.getName().getString();
        String world = player.getWorld().getRegistryKey().getValue().toString();

        String loginTime = LocalDateTime.now().toString();
        String formattedLoginTime = loginTime.replace("T", " ").split("\\.")[0];

        String inventr = inventory.toString();
        String armorString = armor.toString();
        String offHandString = offHand.toString();

        float xp = player.getXpToDrop(player.getServer().getOverworld(), player);

        try {
            BorukvaInventoryBackup.getBorukvaDeathBackupDB()
                    .addDataLogout(name, world, place, formattedLoginTime, inventr, armorString, offHandString,xp);
            LOGGER.info("Information about "+name+" write to DB");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
