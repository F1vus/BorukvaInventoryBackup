package net.fiv.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.fiv.BorukvaInventoryBackup;
import net.minecraft.component.ComponentChanges;
import net.minecraft.entity.player.PlayerEntity;
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
import java.util.*;

public class
InventoryGui extends SimpleGui {

    public InventoryGui(ServerPlayerEntity player, String playerName, Map<Integer, ItemStack> itemStackMap, int xp) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);

        addItems(itemStackMap, xp, playerName);
    }


    private void addItems(Map<Integer, ItemStack> itemStackMap, int xp, String playerName){
        int i = 0;
        for(ItemStack item: itemStackMap.values()){
            this.setSlot(i, new GuiElementBuilder(item.getItem())
                    .build());
            i++;
        }

        this.setSlot(53, new GuiElementBuilder(Items.PAPER)
                .setName(Text.literal("Backup player inventory"))
                .setCallback((index, type, action) -> {
                    UUID uuid = getOfflinePlayerProfile(playerName, player.getServer());

                    if(this.player.getServer().getPlayerManager().getPlayer(playerName) != null){
                        backUpPlayerItems(itemStackMap, xp,this.player.getServer().getPlayerManager().getPlayer(playerName));
                    } else {
                        saveOfflinePlayerInventory(uuid, xp,itemStackMap);
                    }

                })
                .build());

        this.setSlot(52, new GuiElementBuilder(Items.PAPER)
                .setName(Text.literal("Go back"))
                .setCallback((index, type, action) -> {
                    TableListGui.activeTables.get(player.getName().getString()).getFirst().open();

                })
                .build());
    }

    public static UUID getOfflinePlayerProfile(String playerName, MinecraftServer server) {
        if (server == null) return null;

        UserCache cache = server.getUserCache();

        if (cache == null) return null;

        Optional<GameProfile> optionalGameProfile = cache.findByName(playerName);

        if (optionalGameProfile.isPresent()){
            GameProfile gameProfile = optionalGameProfile.get();
            return gameProfile.getId();
        }
        return null;
    }


    private void backUpPlayerItems(Map<Integer, ItemStack> itemStackMap, int xp,ServerPlayerEntity player){

        int index = 0;
        PlayerInventory playerInventory = player.getInventory();
        //System.out.println("Back: "+itemStackMap);
        for(ItemStack itemStack: itemStackMap.values()){
            if(index < 4){
                playerInventory.insertStack(36+index, itemStack);
            } else if (index==4) {
                playerInventory.insertStack(40, itemStack);
            } else {
                playerInventory.insertStack(index-5, itemStack);
            }
            //System.out.println("index: "+index);
            index++;
        }
        player.setExperienceLevel(xp);

    }

    private void saveOfflinePlayerInventory(UUID uuid, int xp, Map<Integer, ItemStack> itemStackMap) {
        File playerDataDir = this.player.getServer().getSavePath(WorldSavePath.PLAYERDATA).toFile();

//        System.out.println(playerDataDir);
        try {
            File file2 = new File(playerDataDir, uuid.toString() + ".dat");

            NbtCompound nbtCompound = NbtIo.readCompressed(new FileInputStream(file2), NbtSizeTracker.ofUnlimitedBytes());

            NbtList inventoryList = nbtCompound.getList("Inventory", 10);
            inventoryList.clear();

            int index = 0;
            for (ItemStack itemStack : itemStackMap.values()) {
                NbtCompound nbt = getItemStackNbt(itemStack, player.getRegistryManager().getOps(NbtOps.INSTANCE));

                byte slotByte;

                if (index < 4) {
                    slotByte = (byte) (100 + index);
                } else if (index == 4) {
                    slotByte = -106;
                } else {
                    slotByte = (byte) (index - 5);
                }

                nbt.putByte("Slot", slotByte);

                inventoryList.add(index, nbt);

                index++;
            }
            //System.out.print("OffPlayer: "+inventoryList);

            nbtCompound.put("Inventory", inventoryList);
            nbtCompound.putInt("XpLevel", xp);
            NbtIo.writeCompressed(nbtCompound, file2.toPath());
        } catch (Exception e) {
            BorukvaInventoryBackup.LOGGER.warn(e.getMessage());
        }

    }

    public static NbtCompound getItemStackNbt(ItemStack stack, DynamicOps<NbtElement> ops) {
        DataResult<NbtElement> result = ComponentChanges.CODEC.encodeStart(ops, stack.getComponentChanges());
        result.ifError(e -> {
        });

        NbtCompound nbtCompound = new NbtCompound();

        NbtElement nbtElement = result.getOrThrow();

        if (nbtElement != null)
            nbtCompound.put("components", nbtElement);

        nbtCompound.putInt("count", stack.getCount());
        nbtCompound.putString("id", stack.getItem().toString());

        return nbtCompound;
    }

    public static ArrayList<String> playerItems(List<ItemStack> inventory, PlayerEntity player){

        ArrayList<String> playerItems = new ArrayList<>();

        for(ItemStack itemStack: inventory){
            NbtCompound nbt = getItemStackNbt(itemStack, player.getRegistryManager().getOps(NbtOps.INSTANCE));

            playerItems.add(nbt.toString());
        }

        return playerItems;

    }

}
