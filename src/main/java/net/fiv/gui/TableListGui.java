package net.fiv.gui;

import com.mojang.serialization.DataResult;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.fiv.BorukvaInventoryBackup;
import net.fiv.commands.GetInventoryHistoryCommand;
import net.fiv.util.InventorySerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TableListGui extends SimpleGui {


    public TableListGui(ServerPlayerEntity player, String playerName) {
        super(ScreenHandlerType.GENERIC_9X1, player, false);
        this.setTitle(Text.literal(playerName+"'s tables list"));

        addButtons(playerName);
    }

    private void addButtons(String playerName){
        this.setSlot(2, new GuiElementBuilder(Items.CHEST)
                .setName(Text.literal("Login history").formatted(Formatting.GREEN, Formatting.BOLD))
                .setCallback((index, type, action) -> GetInventoryHistoryCommand.getLoginTableMap(player, playerName))

                .build());

        this.setSlot(3, new GuiElementBuilder(Items.CHEST)
                .setName(Text.literal("Logout history").formatted(Formatting.YELLOW, Formatting.BOLD))
                .setCallback((index, type, action) -> GetInventoryHistoryCommand.getLogoutTableMap(player, playerName))

                .build());

        this.setSlot(5, new GuiElementBuilder(Items.CHEST)
                .setName(Text.literal("Death history").formatted(Formatting.RED, Formatting.BOLD))
                .setCallback((index, type, action) -> GetInventoryHistoryCommand.getDeathTableMap(player, playerName))

                .build());

        this.setSlot(6, new GuiElementBuilder(Items.CHEST)
                .setName(Text.literal("Backups history").formatted(Formatting.DARK_GRAY, Formatting.BOLD))
                .setCallback((index, type, action) -> GetInventoryHistoryCommand.getPreRestoreTableMap(player, playerName))

                .build());
    }

    protected static Map<Integer, ItemStack> inventorySerialization(String inventory, String armor, String offHand, ServerPlayerEntity player){
        Map<Integer, ItemStack> itemsToGive = new HashMap<>();
        ServerWorld world= player.getWorld();

        NbtCompound nbtCompoundArmor = InventorySerializer.deserializeInventory(armor);
        //System.out.println("armor: "+armor);
        NbtList nbtListArmor = nbtCompoundArmor.getList("Inventory").get();
        //System.out.println("NbtArmor "+ nbtListArmor.toString());

        int index = 0;
        for(NbtElement nbtElement: nbtListArmor){
            NbtCompound itemNbt = (NbtCompound) nbtElement;

            //System.out.println("SlotByte: "+itemNbt.getByte("Slot"));
            ItemStack itemStack;
            //System.out.println("BLOCKTAG: "+itemNbt.getString("id")); //
            if(!itemNbt.getString("id").get().equals("minecraft:air")){

                DataResult<ItemStack> result =
                        ItemStack.CODEC.parse(world.getRegistryManager().getOps(NbtOps.INSTANCE), itemNbt);

                itemStack = result.result().orElseGet(() -> new ItemStack(Items.AIR));

            } else if(itemNbt.getString("id").get().equals("minecraft:air")){
                itemStack = new ItemStack(Items.AIR);
            } else{
                itemStack = new ItemStack(Registries.ITEM.get(Identifier.of(itemNbt.getString("id").get())), itemNbt.getInt("count").get());
            }

            itemsToGive.put(index, itemStack);
            index++;
        }

        NbtCompound nbtCompoundOffHand = InventorySerializer.deserializeInventory(offHand);
       // System.out.println("OffHand: "+nbtCompoundOffHand);
        NbtList nbtListOffHand = nbtCompoundOffHand.getList("Inventory").get();


        for(NbtElement nbtElement: nbtListOffHand){
            NbtCompound itemNbt = (NbtCompound) nbtElement;

            //System.out.println("SlotByte: "+itemNbt.getByte("Slot"));
            //System.out.println(itemNbt);

            ItemStack itemStack;

            //System.out.println("BLOCKTAG: "+itemNbt.getString("id")); //
            if(!itemNbt.getString("id").get().equals("minecraft:air")){

                DataResult<ItemStack> result =
                        ItemStack.CODEC.parse(world.getRegistryManager().getOps(NbtOps.INSTANCE), itemNbt);

                itemStack = result.result().orElseGet(() -> new ItemStack(Items.AIR));

            } else if(itemNbt.getString("id").get().equals("minecraft:air")){
                itemStack = new ItemStack(Items.AIR);

            }else {
                itemStack = new ItemStack(Registries.ITEM.get(Identifier.of(itemNbt.getString("id").get())), itemNbt.getInt("count").get());
            }

            itemsToGive.put(index, itemStack);
            index++;
        }


        NbtCompound nbtCompoundInventory = InventorySerializer.deserializeInventory(inventory);
        NbtList nbtListInventory = nbtCompoundInventory.getList("Inventory").get();
       // System.out.println("inv: "+inventory);

        for(NbtElement nbtElement: nbtListInventory){
            NbtCompound itemNbt = (NbtCompound)nbtElement;

            //System.out.println("SlotByte: "+itemNbt.getByte("Slot"));
            //System.out.println(itemNbt);
            ItemStack itemStack;

            //System.out.println("BLOCKTAG: "+itemNbt.getString("id")); //
            if(!itemNbt.getString("id").get().equals("minecraft:air")){
                DataResult<ItemStack> result =
                        ItemStack.CODEC.parse(world.getRegistryManager().getOps(NbtOps.INSTANCE), itemNbt);

                itemStack = result.result().orElseGet(() -> new ItemStack(Items.AIR));

            } else if(itemNbt.getString("id").get().equals("minecraft:air")){
                itemStack = new ItemStack(Items.AIR);
            }else {
                itemStack = new ItemStack(Registries.ITEM.get(Identifier.of(itemNbt.getString("id").get())), itemNbt.getInt("count").get());
            }

            itemsToGive.put(index, itemStack);
            index++;
        }

        return itemsToGive;
    }

    protected static Map<Integer, ItemStack> inventorySerialization(String enderChest, ServerPlayerEntity player) {
        try {
            World world = player.getWorld();

            NbtCompound nbtCompound = InventorySerializer.deserializeInventory(enderChest);
            NbtList inventoryList = nbtCompound
                    .getList("Inventory")
                    .orElseThrow(() -> new IllegalArgumentException("No Inventory tag found"));

            AtomicInteger index = new AtomicInteger(0);

            return inventoryList.stream()
                    .map(el -> el.asCompound().orElse(null))
                    .filter(Objects::nonNull)
                    .map(itemNbt -> {
                        String id = getStringOr(itemNbt, "id", "minecraft:air");

                        ItemStack stack;

                        if (!id.equals("minecraft:air")) {
                            DataResult<ItemStack> result =
                                    ItemStack.CODEC.parse(world.getRegistryManager().getOps(NbtOps.INSTANCE), itemNbt);

                            stack = result.result().orElseGet(() -> new ItemStack(Items.AIR));
                        } else {
                            stack = new ItemStack(Items.AIR);
                        }

                        return Map.entry(index.getAndIncrement(), stack);
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        } catch (Exception e) {
            BorukvaInventoryBackup.LOGGER.error("Exception when try to serialize ender chest: "+e.getMessage());
            return Collections.emptyMap();
        }
    }

    private static String getStringOr(NbtCompound nbt, String key, String def) {
        return nbt.getString(key).orElse(def);
    }
}