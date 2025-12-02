package net.fiv.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import lombok.Setter;
import net.fiv.BorukvaInventoryBackup;
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
        int tableSize = this.logoutTableList.size();
        int lastIndex = Math.min(firstIndex + 45, tableSize);

        for(int i=firstIndex; i<lastIndex; i++){

            int inventory_index = i-firstIndex;

            if(inventory_index>44) break;

            String inventory = this.logoutTableList.get(tableSize-i-1).getInventory();
            String armor = this.logoutTableList.get(tableSize-i-1).getArmor();
            String offHand = this.logoutTableList.get(tableSize-i-1).getOffHand();
            String enderChest = this.logoutTableList.get(tableSize-i-1).getEnderChest();

            int xp = this.logoutTableList.get(tableSize-i-1).getXp();
            this.setSlot(inventory_index, new GuiElementBuilder(Items.CHEST)
                    .setName(Text.literal("Time: "+this.logoutTableList.get(tableSize-i-1).getDate()))
                    .addLoreLine(Text.literal("World: "+this.logoutTableList.get(tableSize-i-1).getWorld()))
                    .addLoreLine(Text.literal("Place: "+this.logoutTableList.get(tableSize-i-1).getPlace()))
                    .addLoreLine(Text.literal("XpLevel: "+this.logoutTableList.get(tableSize-i-1).getXp()))
                    .setCallback((index, type, action) -> {
                        Map<Integer, ItemStack> itemStackList = TableListGui.inventorySerialization(inventory, armor, offHand, player);

                        if(itemStackList.isEmpty()){
                            BorukvaInventoryBackup.LOGGER.error("Can't create InventoryGUI because itemStackList is null");
                            return;
                        }

                        new InventoryGui(player, this.logoutTableList.getFirst().getName(), itemStackList, enderChest, xp, this).open();
                    })
                    .build());


        }

        if (lastIndex < this.logoutTableList.size()) {
            this.setSlot(53, new GuiElementBuilder(Items.ARROW)
                    .setName(Text.literal("Next Page"))
                    .setCallback((index, type, action) -> new LogoutHistoryGui(player, page+1, this.logoutTableList).open())
                    .build());
        }

        this.setSlot(49, new GuiElementBuilder(Items.EMERALD)
                .setName(Text.literal("Back to tables list"))
                .setCallback((index, type, action) -> new TableListGui(player, logoutTableList.getFirst().getName()).open())
                .build());

        if (page > 0) {
            this.setSlot(45, new GuiElementBuilder(Items.ARROW)
                    .setName(Text.literal("Previous Page"))
                    .setCallback((index, type, action) -> new LogoutHistoryGui(player, page-1, this.logoutTableList).open())
                    .build());
        }
//        System.out.println(TableListGui.activeTables);
    }

}
