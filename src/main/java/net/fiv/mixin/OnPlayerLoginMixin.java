package net.fiv.mixin;

import net.fiv.BorukvaInventoryBackup;
import net.fiv.gui.InventoryGui;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerManager.class)
public class OnPlayerLoginMixin {
    @Inject(method = "onPlayerConnect", at = @At("HEAD"))
    private void playerConnectMixin(ClientConnection connection, ServerPlayerEntity player,
                                    ConnectedClientData clientData, CallbackInfo ci){
        DefaultedList<ItemStack> inventory = player.getInventory().main;
        DefaultedList<ItemStack> armor = player.getInventory().armor;
        List<ItemStack> offHand = new ArrayList<>();
        offHand.add(player.getOffHandStack());

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        String place = "%.2f %.2f %.2f".formatted(x, y, z);
        String name = player.getName().getString();
        String world = player.getWorld().getRegistryKey().getValue().toString();

        String loginTime = LocalDateTime.now().toString();
        String formattedLoginTime = loginTime.replace("T", " ").split("\\.")[0];

        String inventr = InventoryGui.playerItems(inventory, player).toString();
        String armorString = InventoryGui.playerItems(armor, player).toString();
        String offHandString = InventoryGui.playerItems(offHand, player).toString();

        int xp = player.experienceLevel;

        try {
            BorukvaInventoryBackup.getBorukvaInventoryBackupDB()
                    .addDataLogin(name, world, place, formattedLoginTime, inventr, armorString, offHandString,xp);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
