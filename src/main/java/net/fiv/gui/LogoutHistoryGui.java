package net.fiv.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import lombok.Setter;
import net.fiv.data_base.entities.LogoutTable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

@Setter
public class LogoutHistoryGui extends SimpleGui {
    private int page;

    private List<LogoutTable> logoutTableList;

    public LogoutHistoryGui(ServerPlayerEntity player, int page, List<LogoutTable> logoutTables) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);

        this.logoutTableList = logoutTables;
        this.page = page;

        addButtons();
    }

    @Override
    public boolean canPlayerClose() {
        return true;
    }


    private void addButtons(){
        int firstIndex = this.page * 45;

        int lastIndex = Math.min(firstIndex + 45, this.logoutTableList.size());

        for(int i=firstIndex; i<lastIndex; i++){

            int inventory_index = i-firstIndex;

            if(inventory_index>44) break;

            String inventory = this.logoutTableList.get(firstIndex+inventory_index).getInventory();
            String armor = this.logoutTableList.get(firstIndex+inventory_index).getArmor();
            String offHand = this.logoutTableList.get(firstIndex+inventory_index).getOffHand();
            int xp = this.logoutTableList.get(firstIndex+inventory_index).getXp();
            this.setSlot(inventory_index, new GuiElementBuilder(Items.CHEST)
                    .setName(Text.literal("Time: "+this.logoutTableList.get(firstIndex+inventory_index).getDate()))
                    .addLoreLine(Text.literal("World: "+this.logoutTableList.get(firstIndex+inventory_index).getWorld()))
                    .addLoreLine(Text.literal("Place: "+this.logoutTableList.get(firstIndex+inventory_index).getPlace()))
                    .addLoreLine(Text.literal("XpLevel: "+this.logoutTableList.get(firstIndex+inventory_index).getXp()))
                    .setCallback((index, type, action) -> {
                        Map<Integer, ItemStack> itemStackList = TableListGui.inventorySerialization(inventory, armor, offHand, player);
                        new InventoryGui(player, this.logoutTableList.getFirst().getName(), itemStackList, xp, this).open();
                    })
                    .build());


        }

        if (lastIndex < this.logoutTableList.size()) {
            this.setSlot(53, new GuiElementBuilder(Items.ARROW)
                    .setName(Text.literal("Next Page"))
                    .setCallback((index, type, action) -> {
                        new LogoutHistoryGui(player, page+1, this.logoutTableList).open();
                    })
                    .build());
        }

        this.setSlot(49, new GuiElementBuilder(Items.EMERALD)
                .setName(Text.literal("Back to tables list"))
                .setCallback((index, type, action) -> {
                    new TableListGui(player, logoutTableList.getFirst().getName()).open();
                })
                .build());

        if (page > 0) {
            this.setSlot(45, new GuiElementBuilder(Items.ARROW)
                    .setName(Text.literal("Previous Page"))
                    .setCallback((index, type, action) -> {
                        new LogoutHistoryGui(player, page-1, this.logoutTableList).open();
                    })
                    .build());
        }
//        System.out.println(TableListGui.activeTables);
    }

}
