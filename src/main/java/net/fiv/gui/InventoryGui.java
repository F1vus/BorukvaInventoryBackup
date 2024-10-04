package net.fiv.gui;

import com.mojang.authlib.GameProfile;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.fiv.BorukvaInventoryBackup;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.UserCache;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InventoryGui extends SimpleGui {

    public InventoryGui(ServerPlayerEntity player, String playerName, List<ItemStack> itemStackList) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);

        addItems(itemStackList, playerName);
    }

    private void addItems(List<ItemStack> itemStackList, String playerName){
        int i = 0;
        for(ItemStack item: itemStackList){
            this.setSlot(i, new GuiElementBuilder(item.getItem())
                    .build());
            i++;
        }

        this.setSlot(53, new GuiElementBuilder(Items.PAPER)
                .setName(Text.literal("Backup player inventory"))
                .setCallback((index, type, action) -> {
                    UUID uuid = getOfflinePlayerProfile(playerName, player.getServer());

                    if(this.player.getServer().getPlayerManager().getPlayer(playerName) != null){
                        backUpPlayerItems(itemStackList, this.player.getServer().getPlayerManager().getPlayer(playerName));
                    } else {
                        saveOfflinePlayerInventory(uuid, itemStackList);
                    }

                })
                .build());
    }

    public static UUID getOfflinePlayerProfile(String playerName, MinecraftServer server) {
        if (server == null) return null;

        UserCache cache = server.getUserCache();

        Optional<GameProfile> optionalGameProfile = cache.findByName(playerName);

        if (optionalGameProfile.isPresent()){
            GameProfile gameProfile = optionalGameProfile.get();
            return gameProfile.getId();
        }
        return null;
    }


    private void backUpPlayerItems(List<ItemStack> itemStackList, ServerPlayerEntity player){
        int index = 0;
        PlayerInventory playerInventory = player.getInventory();
        for(ItemStack itemStack: itemStackList){
            if(index < 4){
                playerInventory.insertStack(36+index, itemStack);
            } else if (index==4) {
                playerInventory.insertStack(40, itemStack);
            } else {
                playerInventory.insertStack(index-5, itemStack);
            }

            index++;
        }

    }

    private void saveOfflinePlayerInventory(UUID uuid, List<ItemStack> itemStackList) {
        File playerDataDir = this.player.getServer().getSavePath(WorldSavePath.PLAYERDATA).toFile();

        System.out.println(playerDataDir);
        try {
            File file2 = new File(playerDataDir, uuid.toString() + ".dat");

            NbtCompound nbtCompound = NbtIo.readCompressed(new FileInputStream(file2), NbtSizeTracker.ofUnlimitedBytes());

            NbtList inventoryList = nbtCompound.getList("Inventory", 10);
            inventoryList.clear();
            System.out.println("InventoryList: " + inventoryList);
            int index = 0;
            for (ItemStack itemStack : itemStackList) {
                NbtCompound nbtItem = new NbtCompound();
                int count = itemStack.getCount();
                byte slotByte;

                if (index < 4) {
                    slotByte = (byte) (100 + index);
                } else if (index == 4) {
                    slotByte = -106;
                } else {
                    slotByte = (byte) (index - 5);
                }

                nbtItem.putInt("count", count);
                nbtItem.putByte("Slot", slotByte);
                nbtItem.putString("id", itemStack.getItem().toString());

                inventoryList.add(index, nbtItem);
                System.out.println(inventoryList);

                index++;
            }

            nbtCompound.put("Inventory", inventoryList);
            NbtIo.writeCompressed(nbtCompound, file2.toPath());
        } catch (Exception e) {
            BorukvaInventoryBackup.LOGGER.warn(e.getMessage());
        }

    }

}
