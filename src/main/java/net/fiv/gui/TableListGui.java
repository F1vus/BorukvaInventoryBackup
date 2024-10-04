package net.fiv.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.fiv.commands.GetInventoryHistoryCommand;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.*;

public class TableListGui extends SimpleGui {

    public static HashMap<String, List<SimpleGui>> activeTables = new HashMap<>();

    public TableListGui(ServerPlayerEntity player, String playerName) {
        super(ScreenHandlerType.GENERIC_9X1, player, false);
        this.setTitle(Text.literal(playerName+"'s tables list"));

        addButtons(playerName);
    }

    @Override
    public boolean canPlayerClose() {
        //activeTables.remove(this.player.getName().getString());
        return true;
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

    protected static List<ItemStack> inventorySerialization(String inventory, String armor, String offHand){
        List<ItemStack> itemsToGive = new ArrayList<>();

        String inventory1 = inventory.replace("[", "");
        String inventory2 = inventory1.replace("]", "");
        String armor1 = armor.replace("[", "");
        String armor2 = armor1.replace("]", "");

        List<String> items = new ArrayList<>(List.of(inventory2.split(", ")));
        List<String> armors = new ArrayList<>(List.of(armor2.split(", ")));

        for(String armorTag: armors){
            String[] tagAndNumber = armorTag.split(" ");

            itemsToGive.add(new ItemStack(Registries.ITEM.get(Identifier.of(tagAndNumber[1])), Integer.parseInt(tagAndNumber[0])));
        }

        String[] offHandItem = offHand.split(" ");
        itemsToGive.add(new ItemStack(Registries.ITEM.get(Identifier.of(offHandItem[1])), Integer.parseInt(offHandItem[0])));

        for(String item: items){
            String[] tagAndNumber = item.split(" ");
            itemsToGive.add(new ItemStack(Registries.ITEM.get(Identifier.of(tagAndNumber[1])), Integer.parseInt(tagAndNumber[0])));
        }

        return itemsToGive;
    }

}