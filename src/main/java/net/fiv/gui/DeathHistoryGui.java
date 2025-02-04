package net.fiv.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import lombok.Setter;
import net.fiv.data_base.entities.DeathTable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

@Setter
public class DeathHistoryGui extends SimpleGui {

    private int page;

    private List<DeathTable> deathTableList;

    public DeathHistoryGui(ServerPlayerEntity player, int page, List<DeathTable> deathTables) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);

        this.deathTableList = deathTables;
        this.page = page;

        addButtons();

    }

    @Override
    public boolean canPlayerClose() {
        return true;
    }


    private void addButtons(){
        int firstIndex = this.page * 45;
        int tableSize = this.deathTableList.size();
        int lastIndex = Math.min(firstIndex + 45, tableSize);


        for(int i=firstIndex; i<lastIndex; i++){
            int inventory_index = i-firstIndex;

            if(inventory_index>44) break;
            //System.out.println("Size: "+tableSize+" Ref: "+(tableSize-i));
            String inventory = this.deathTableList.get(tableSize-i-1).getInventory();
            String armor = this.deathTableList.get(tableSize-i-1).getArmor();
            String offHand = this.deathTableList.get(tableSize-i-1).getOffHand();
            int xp = this.deathTableList.get(tableSize-i-1).getXp();
            this.setSlot(inventory_index, new GuiElementBuilder(Items.CHEST)
                    .setName(Text.literal("Time: "+this.deathTableList.get(tableSize-i-1).getDate()))
                    .addLoreLine(Text.literal("Death reason: "+this.deathTableList.get(tableSize-i-1).getReason()))
                    .addLoreLine(Text.literal("World: "+this.deathTableList.get(tableSize-i-1).getWorld()))
                    .addLoreLine(Text.literal("Place: "+this.deathTableList.get(tableSize-i-1).getPlace()))
                    .addLoreLine(Text.literal("XpLevel: "+this.deathTableList.get(tableSize-i-1).getXp()))
                    .setCallback((index, type, action) -> {
                        Map<Integer, ItemStack> itemStackList = TableListGui.inventorySerialization(inventory, armor, offHand, player);
                        new InventoryGui(player, this.deathTableList.getFirst().getName(), itemStackList, xp, this).open();
                    })
                    .build());

        }

        if (lastIndex < this.deathTableList.size()) {
            this.setSlot(53, new GuiElementBuilder(Items.ARROW)
                    .setName(Text.literal("Next Page"))
                    .setCallback((index, type, action) -> {
                        new DeathHistoryGui(player, page + 1, this.deathTableList).open();
                    })
                    .build());
        }

        this.setSlot(49, new GuiElementBuilder(Items.EMERALD)
                .setName(Text.literal("Back to tables list"))
                .setCallback((index, type, action) -> {
                    new TableListGui(player, deathTableList.getFirst().getName()).open();
                })
                .build());

        if (page > 0) {
            this.setSlot(45, new GuiElementBuilder(Items.ARROW)
                    .setName(Text.literal("Previous Page"))
                    .setCallback((index, type, action) -> {
                        new DeathHistoryGui(player, page - 1, this.deathTableList).open();
                    })
                    .build());
        }
//        System.out.println(TableListGui.activeTables);
    }

}


