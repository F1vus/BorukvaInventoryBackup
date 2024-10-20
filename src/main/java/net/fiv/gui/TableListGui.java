package net.fiv.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.fiv.commands.GetInventoryHistoryCommand;
import net.fiv.util.InventorySerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.*;
import java.util.List;

public class TableListGui extends SimpleGui {

    public static HashMap<String, List<SimpleGui>> activeTables = new HashMap<>();

    public TableListGui(ServerPlayerEntity player, String playerName) {
        super(ScreenHandlerType.GENERIC_9X1, player, false);
        this.setTitle(Text.literal(playerName+"'s tables list"));

        addButtons(playerName);
    }

    private void addButtons(String playerName){
        this.setSlot(3, new GuiElementBuilder(Items.CHEST)
                .setName(Text.literal("Історія входів").formatted(Formatting.GREEN, Formatting.BOLD))
                .setCallback((index, type, action) -> {
                    GetInventoryHistoryCommand.addLoginTableMap(player, playerName);
                    LoginHistoryGui loginHistoryGui = (LoginHistoryGui) activeTables.get(this.player.getName().getString()).getFirst();

                    loginHistoryGui.addButtons();
                    loginHistoryGui.open();
                        })

                .build());

        this.setSlot(4, new GuiElementBuilder(Items.CHEST)
                .setName(Text.literal("Історія виходів").formatted(Formatting.YELLOW, Formatting.BOLD))
                .setCallback((index, type, action) -> {
                    GetInventoryHistoryCommand.addLogoutTableMap(player, playerName);
                    LogoutHistoryGui logoutHistoryGui = (LogoutHistoryGui) activeTables.get(this.player.getName().getString()).getFirst();

                    logoutHistoryGui.addButtons();
                    logoutHistoryGui.open();
                })

                .build());

        this.setSlot(5, new GuiElementBuilder(Items.CHEST)
                .setName(Text.literal("Історія смертей").formatted(Formatting.RED, Formatting.BOLD))
                .setCallback((index, type, action) -> {
                    GetInventoryHistoryCommand.addDeathTableMap(player, playerName);
                    DeathHistoryGui deathHistoryGui = (DeathHistoryGui) activeTables.get(this.player.getName().getString()).getFirst();

                    deathHistoryGui.addButtons();
                    deathHistoryGui.open();
                })

                .build());
    }

    protected static Map<Integer, ItemStack> inventorySerialization(String inventory, String armor, String offHand, ServerPlayerEntity player){
        World world = player.getWorld();

        Map<Integer, ItemStack> itemsToGive = new HashMap<>();

        NbtCompound nbtCompoundArmor = InventorySerializer.deserializeInventory(armor);
       // System.out.println("armor: "+armor);
        NbtList nbtListArmor = nbtCompoundArmor.getList("Inventory", 10);

        int index = 0;
        for(NbtElement nbtElement: nbtListArmor){
            NbtCompound itemNbt = (NbtCompound)nbtElement;

            //System.out.println("SlotByte: "+itemNbt.getByte("Slot"));
            ItemStack itemStack;
            //System.out.println("BLOCKTAG: "+itemNbt.getString("id")); //
            if(itemNbt.contains("components")){

                itemStack = ItemStack.fromNbt(world.getRegistryManager(), nbtElement).get();

            } else if(Registries.ITEM.get(Identifier.of(itemNbt.getString("id"))).equals(Items.AIR)){
                itemStack = new ItemStack(Items.AIR);
            } else{
                itemStack = new ItemStack(Registries.ITEM.get(Identifier.of(itemNbt.getString("id"))), itemNbt.getInt("Count"));
            }

            itemsToGive.put(index, itemStack);
            index++;
        }

        NbtCompound nbtCompoundOffHand = InventorySerializer.deserializeInventory(offHand);
       // System.out.println("OffHand: "+nbtCompoundOffHand);
        NbtList nbtListOffHand = nbtCompoundOffHand.getList("Inventory", 10);


        for(NbtElement nbtElement: nbtListOffHand){
            NbtCompound itemNbt = (NbtCompound)nbtElement;

            //System.out.println("SlotByte: "+itemNbt.getByte("Slot"));
            //System.out.println(itemNbt);

            ItemStack itemStack;

            //System.out.println("BLOCKTAG: "+itemNbt.getString("id")); //
            if(itemNbt.contains("components")){

                itemStack = ItemStack.fromNbt(world.getRegistryManager(), nbtElement).get();
            } else if(Registries.ITEM.get(Identifier.of(itemNbt.getString("id"))).equals(Items.AIR)){
                itemStack = new ItemStack(Items.AIR);

            }else {
                itemStack = new ItemStack(Registries.ITEM.get(Identifier.of(itemNbt.getString("id"))), itemNbt.getInt("Count"));
            }

            itemsToGive.put(index, itemStack);
            index++;
        }


        NbtCompound nbtCompoundInventory = InventorySerializer.deserializeInventory(inventory);
        NbtList nbtListInventory = nbtCompoundInventory.getList("Inventory", 10);
       // System.out.println("inv: "+inventory);

        for(NbtElement nbtElement: nbtListInventory){
            NbtCompound itemNbt = (NbtCompound)nbtElement;

            //System.out.println("SlotByte: "+itemNbt.getByte("Slot"));
            //System.out.println(itemNbt);
            ItemStack itemStack;

            //System.out.println("BLOCKTAG: "+itemNbt.getString("id")); //
            if(itemNbt.contains("components")){
                itemStack = ItemStack.fromNbt(world.getRegistryManager(), nbtElement).get();
            } else if(Registries.ITEM.get(Identifier.of(itemNbt.getString("id"))).equals(Items.AIR)){
                itemStack = new ItemStack(Items.AIR);

            }else {
                itemStack = new ItemStack(Registries.ITEM.get(Identifier.of(itemNbt.getString("id"))), itemNbt.getInt("Count"));
            }

            itemsToGive.put(index, itemStack);
            index++;
        }

        return itemsToGive;
    }

}