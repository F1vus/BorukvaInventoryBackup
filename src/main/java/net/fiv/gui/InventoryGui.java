package net.fiv.gui;

import com.mojang.authlib.GameProfile;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.fabricmc.loader.api.FabricLoader;
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

import java.io.File;
import java.nio.file.Path;
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
                   UUID uuid = getOfflinePlayerProfile(playerName);
                })
                .build());
    }

    private UUID getOfflinePlayerProfile(String playerName) {
        MinecraftServer server = player.getServer();
        if (server == null) return null;

        UserCache cache = server.getUserCache();

        Optional<GameProfile> optionalGameProfile = cache.findByName(playerName);

        if (optionalGameProfile.isPresent()){
            GameProfile gameProfile = optionalGameProfile.get();
            return gameProfile.getId();
        }
        return null;
    }


    private void backUpPlayerItems(List<ItemStack> itemStackList, ServerPlayerEntity offlinePlayer){
        int index = 0;
        PlayerInventory playerInventory = offlinePlayer.getInventory();
        for(ItemStack itemStack: itemStackList){
            if(index < 4){
                playerInventory.insertStack(36+index, itemStack);
            } else if (index==4) {
                playerInventory.insertStack(40, itemStack);
            } else if (index>4) {
                playerInventory.insertStack(index-5, itemStack);
            }
            index++;
        }

    }

    public NbtList getPlayerInventoryNbt(UUID playerUuid) {
        Optional<NbtCompound> nbtCompound = loadPlayerData(playerUuid);

        if (nbtCompound.isPresent()) {
            NbtCompound playerData = nbtCompound.get();
            if (playerData.contains("Inventory", 9)) { // 9 - тип NbtList
                return playerData.getList("Inventory", 10); // 10 - тип NbtCompound
            }
        }
        return null;
    }

    private Optional<NbtCompound> loadPlayerData(UUID uuid) {
        Path serverDirectory = FabricLoader.getInstance().getGameDir();

        File file = new File(serverDirectory.toString()+"world/playerdata/", uuid.toString() + ".dat");

        Path path = Path.of(serverDirectory+"/world/playerdata/");

        try {
            return Optional.of(NbtIo.readCompressed(Path.of(file.toURI()), NbtSizeTracker.ofUnlimitedBytes()));
        } catch (Exception var5) {
            BorukvaInventoryBackup.LOGGER.warn("Не має nbt гравця");
        }


        return Optional.empty();
    }
}
