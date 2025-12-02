package net.fiv.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.fiv.BorukvaInventoryBackup;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import static net.fiv.gui.InventoryGui.playerItems;

public class EnderChestGui extends SimpleGui {



    public EnderChestGui(ServerPlayerEntity player, String playerName,String enderChest, SimpleGui caller) {
        super(ScreenHandlerType.GENERIC_9X4, player, false);

        Map<Integer, ItemStack> enderChestMap = TableListGui.inventorySerialization(enderChest, player);

        addItems(enderChestMap, playerName,caller);
    }

    private void addItems(Map<Integer, ItemStack> enderChestMap, String playerName,SimpleGui caller){
        int i = 0;
        for(ItemStack item: enderChestMap.values()){
            this.setSlot(i, new GuiElementBuilder(item)
                    .setCount(item.getCount())
                    .build());
            i++;
        }

        this.setSlot(33, new GuiElementBuilder(Items.SHULKER_BOX)
                .setName(Text.literal("Backup player items to the box").formatted(Formatting.GREEN, Formatting.BOLD))
                .setLore(new ArrayList<>(List.of(Text.literal("clear your inventory before issuing").formatted(Formatting.RED, Formatting.BOLD))))
                .setCallback((index, type, action) -> {
                    InventoryGui.backUpPlayerItemsToChest(enderChestMap, playerName, this.player);
                    this.getPlayer().sendMessage(Text.literal("You have successfully restored items to box!").formatted(Formatting.GREEN, Formatting.BOLD));
                })
                .build());

        this.setSlot(35, new GuiElementBuilder(Items.PAPER)
                .setName(Text.literal("Backup player ender chest(recovery will be irreversible)").formatted(Formatting.RED, Formatting.BOLD))
                .setCallback((index, type, action) -> {
                    UUID uuid = InventoryGui.getOfflinePlayerProfile(playerName, player.getServer());

                    if(this.player.getServer().getPlayerManager().getPlayer(playerName) != null){
                        backUpPlayerItems(enderChestMap, this.player.getServer().getPlayerManager().getPlayer(playerName));
                        this.getPlayer().sendMessage(Text.literal("You have successfully restored items to an online player!").formatted(Formatting.GREEN, Formatting.BOLD));
                    } else {
                        saveOfflinePlayerEnderChest(uuid, enderChestMap, playerName);
                        this.getPlayer().sendMessage(Text.literal("You have successfully restored items to an offline player!").formatted(Formatting.GREEN, Formatting.BOLD));

                    }

                })
                .build());

        this.setSlot(27, new GuiElementBuilder(Items.EMERALD)
                .setName(Text.literal("Return back"))
                .setCallback((index, type, action) -> caller.open())
                .build());
    }

    private void backUpPlayerItems(Map<Integer, ItemStack> itemStackMap, ServerPlayerEntity player){
        int index = 0;
        EnderChestInventory enderChestInventory = player.getEnderChestInventory();

        InventoryGui.savePreRestorePlayerInventory(player.getName().getString(),
                playerItems(player.getEnderChestInventory().heldStacks, player).toString(),
                null,
                null,
                null,
                false,
                0
        );

        enderChestInventory.clear();
        //System.out.println("Back: "+itemStackMap);
        for(ItemStack itemStack: itemStackMap.values()){
            //System.out.println("Size"+playerInventory.size());
            enderChestInventory.setStack(index, itemStack);
            //System.out.println("index: "+index);
            index++;
        }
    }

    private void saveOfflinePlayerEnderChest(UUID uuid, Map<Integer, ItemStack> itemStackMap, String playerName) {
        File playerDataDir = this.player.getServer().getSavePath(WorldSavePath.PLAYERDATA).toFile();

//        System.out.println(playerDataDir);
        try {
            File file2 = new File(playerDataDir, uuid.toString() + ".dat");

            NbtCompound nbtCompound = NbtIo.readCompressed(new FileInputStream(file2), NbtSizeTracker.ofUnlimitedBytes());

            NbtList inventoryList = nbtCompound.getList("EnderItems").get();


            InventoryGui.savePreRestorePlayerInventory(playerName,
                    inventoryList.toString(),
                    null,
                    null,
                    null,
                    false,
                    0
            );


            inventoryList.clear();

            int index = 0;
            for (ItemStack itemStack : itemStackMap.values()) {
                NbtCompound nbt = InventoryGui.getItemStackNbt(itemStack, player.getRegistryManager().getOps(NbtOps.INSTANCE));

                nbt.putByte("Slot", (byte) index);

                inventoryList.add(index, nbt);

                index++;
            }
            //System.out.print("OffPlayer: "+inventoryList);

            nbtCompound.put("EnderItems", inventoryList);

            NbtIo.writeCompressed(nbtCompound, file2.toPath());
        } catch (Exception e) {
            BorukvaInventoryBackup.LOGGER.warn(e.getMessage());
        }

    }

}
