package net.fiv.gui;

import akka.actor.ActorRef;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.fiv.BorukvaInventoryBackup;
import net.fiv.actor.BActorMessages;
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
import net.minecraft.util.Formatting;
import net.minecraft.util.UserCache;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class InventoryGui extends SimpleGui {

    public InventoryGui(ServerPlayerEntity player, String playerName, Map<Integer, ItemStack> itemStackMap, Map<Integer, ItemStack> enderChestMap,int xp, SimpleGui caller) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);

        addItems(itemStackMap, enderChestMap,xp, playerName,caller);
    }


    private void addItems(Map<Integer, ItemStack> itemStackMap, Map<Integer, ItemStack> enderChestMap,int xp, String playerName, SimpleGui caller){
        int i = 0;
        for(ItemStack item: itemStackMap.values()){
            this.setSlot(i, new GuiElementBuilder(item)
                    .setCount(item.getCount())
                    .build());
            i++;
        }



        this.setSlot(53, new GuiElementBuilder(Items.PAPER)
                .setName(Text.literal("Backup player inventory(recovery will be irreversible)").formatted(Formatting.RED, Formatting.BOLD))
                .setCallback((index, type, action) -> {
                    UUID uuid = getOfflinePlayerProfile(playerName, player.getServer());

                    if(this.player.getServer().getPlayerManager().getPlayer(playerName) != null){
                        backUpPlayerItems(itemStackMap, xp, this.player.getServer().getPlayerManager().getPlayer(playerName));
                        this.getPlayer().sendMessage(Text.literal("You have successfully restored items to an online player!").formatted(Formatting.GREEN, Formatting.BOLD));
                    } else {
                        saveOfflinePlayerInventory(uuid, xp,itemStackMap, playerName);
                        this.getPlayer().sendMessage(Text.literal("You have successfully restored items to an offline player!").formatted(Formatting.GREEN, Formatting.BOLD));

                    }

                })
                .build());

        this.setSlot(51, new GuiElementBuilder(Items.SHULKER_BOX)
                .setName(Text.literal("Backup player items to the box").formatted(Formatting.GREEN, Formatting.BOLD))
                .setLore(new ArrayList<>(List.of(Text.literal("clear your inventory before issuing").formatted(Formatting.RED, Formatting.BOLD))))
                .setCallback((index, type, action) -> {
                    backUpPlayerItemsToChest(itemStackMap, xp, this.player.getServer().getPlayerManager().getPlayer(playerName), this.player);
                    this.getPlayer().sendMessage(Text.literal("You have successfully restored items to box!").formatted(Formatting.GREEN, Formatting.BOLD));
                })
                .build());

        this.setSlot(47, new GuiElementBuilder(Items.ENDER_CHEST)
                .setName(Text.literal("Player ender chest").formatted(Formatting.DARK_PURPLE))
                .setCallback((index, type, action) -> new EnderChestGui(player, playerName,enderChestMap, this).open())
                .build());

        this.setSlot(46, new GuiElementBuilder(Items.EXPERIENCE_BOTTLE)
                .setName(Text.literal("XP level: "+xp).formatted(Formatting.YELLOW))
                .build());

        this.setSlot(45, new GuiElementBuilder(Items.EMERALD)
                .setName(Text.literal("Return back"))
                .setCallback((index, type, action) -> caller.open())
                .build());
    }

    protected static void savePreRestorePlayerInventory(String playerName, String inventory, String armor, String offHand, String enderChest, boolean isInventory,int xp){
        BorukvaInventoryBackup.getDatabaseManagerActor().tell(
                new BActorMessages.SavePlayerDataOnPlayerRestore(playerName, inventory, armor, offHand, enderChest, isInventory, xp), ActorRef.noSender());

    }

    protected static void savePreRestorePlayerInventory(String playerName, NbtList inventory, NbtList enderChest ,int xp){
        BorukvaInventoryBackup.getDatabaseManagerActor().tell(
                new BActorMessages.SavePlayerDataOnPlayerRestoreNbt(playerName, inventory, enderChest, true ,xp), ActorRef.noSender());

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

        savePreRestorePlayerInventory(player.getName().getString(),
                playerItems(playerInventory.main, player).toString(),
                playerItems(playerInventory.armor, player).toString(),
                playerItems(playerInventory.offHand, player).toString(),
                playerItems(player.getEnderChestInventory().heldStacks, player).toString(),
                true,
                xp
                );

        playerInventory.clear();
        //System.out.println("Back: "+itemStackMap);
        for(ItemStack itemStack: itemStackMap.values()){
            //System.out.println("Size"+playerInventory.size());
            if(index < 4){
                playerInventory.setStack(36+index, itemStack);
            } else if (index==4) {
                playerInventory.setStack(40, itemStack);
            } else {
                playerInventory.setStack(index-5, itemStack);
            }
            //System.out.println("index: "+index);
            index++;
        }
        player.setExperienceLevel(xp);

    }

    private void saveOfflinePlayerInventory(UUID uuid, int xp, Map<Integer, ItemStack> itemStackMap, String playerName) {
        File playerDataDir = this.player.getServer().getSavePath(WorldSavePath.PLAYERDATA).toFile();

//        System.out.println(playerDataDir);
        try {
            File file2 = new File(playerDataDir, uuid.toString() + ".dat");

            NbtCompound nbtCompound = NbtIo.readCompressed(new FileInputStream(file2), NbtSizeTracker.ofUnlimitedBytes());

            NbtList inventoryList = nbtCompound.getList("Inventory", 10);
            NbtList enderChestLists = nbtCompound.getList("EnderItems", 10);

            savePreRestorePlayerInventory(playerName, inventoryList, enderChestLists ,xp);

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

    public static void backUpPlayerItemsToChest(Map<Integer, ItemStack> itemStackMap, int xp, ServerPlayerEntity player, ServerPlayerEntity operatorPlayer){
        List<Integer> toRemove = new ArrayList<>();

        itemStackMap.forEach((index, item) -> {
            if (item.getItem() == Items.AIR) {
                toRemove.add(index);
            }
        });

        toRemove.forEach(itemStackMap::remove);

        List<ItemStack> list = itemStackMap.values().stream().toList();

        ItemStack chest;

        if(itemStackMap.size() > 27){

            chest = createChestItem(list.subList(27, list.size()), player.getName().getString()+" second inventory", operatorPlayer);

            operatorPlayer.dropStack(operatorPlayer.getServerWorld(), chest);
        }

        chest = createChestItem(list.subList(0, Math.min(27, list.size())), player.getName().getString()+" first inventory", operatorPlayer);

        operatorPlayer.dropStack(operatorPlayer.getServerWorld(), chest);
        if(xp != -1)
            player.setExperienceLevel(xp);

    }

    public static ItemStack createChestItem(List<ItemStack> items, String name, ServerPlayerEntity operatorPlayer) {
        NbtList containerList = new NbtList();

        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                NbtCompound slotTag = new NbtCompound();
                NbtCompound nbt = getItemStackNbt(stack, operatorPlayer.getRegistryManager().getOps(NbtOps.INSTANCE));
                slotTag.put("item", nbt);
                slotTag.putInt("slot", i); // скриня має слоти від 0 до 26
                containerList.add(slotTag);
            }
        }

        NbtCompound components = new NbtCompound();
        components.put("minecraft:container", containerList);
        components.putString("minecraft:item_name", "{\"text\":\"" + name + "\"}");

        NbtCompound blockEntityTag = new NbtCompound();
        blockEntityTag.put("components", components);
        blockEntityTag.putInt("count", 1);
        blockEntityTag.putString("id", "minecraft:chest");

        return ItemStack.fromNbt(operatorPlayer.getWorld().getRegistryManager(), blockEntityTag).get();
    }

}
