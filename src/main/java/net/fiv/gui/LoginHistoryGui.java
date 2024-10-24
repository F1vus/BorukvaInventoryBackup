package net.fiv.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import lombok.Setter;
import net.fiv.data_base.entities.LoginTable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

@Setter
public class LoginHistoryGui extends SimpleGui {
    private int page;

    private List<LoginTable> loginTableList;

    public LoginHistoryGui(ServerPlayerEntity player, int page, List<LoginTable> loginTables) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);

        this.loginTableList = loginTables;
        this.page = page;

        addButtons();
    }

    @Override
    public boolean canPlayerClose() {
        return true;
    }


    public void addButtons(){
        int firstIndex = this.page * 45;
        int lastIndex = Math.min(firstIndex + 45, this.loginTableList.size());

        for(int i=0; i<this.loginTableList.size(); i++){
            int inventory_index = i-firstIndex;

            String inventory = this.loginTableList.get(inventory_index).getInventory();
            String armor = this.loginTableList.get(inventory_index).getArmor();
            String offHand = this.loginTableList.get(inventory_index).getOffHand();
            int xp = this.loginTableList.get(inventory_index).getXp();
            this.setSlot(inventory_index, new GuiElementBuilder(Items.CHEST)
                    .setName(Text.literal("Time: "+this.loginTableList.get(inventory_index).getDate()))
                    .addLoreLine(Text.literal("World: "+this.loginTableList.get(inventory_index).getWorld()))
                    .addLoreLine(Text.literal("Place: "+this.loginTableList.get(inventory_index).getPlace()))
                    .addLoreLine(Text.literal("XpLevel: "+this.loginTableList.get(inventory_index).getXp()))
                    .setCallback((index, type, action) -> {
                        Map<Integer, ItemStack> itemStackList = TableListGui.inventorySerialization(inventory, armor, offHand, player);
                        new InventoryGui(player, this.loginTableList.getFirst().getName(), itemStackList, xp).open();
                    })
                    .build());
            if(inventory_index>45) break;
        }


        if (lastIndex < this.loginTableList.size()) {
            this.setSlot(53, new GuiElementBuilder(Items.ARROW)
                    .setName(Text.literal("Next Page"))
                    .setCallback((index, type, action) -> {
                        new LoginHistoryGui(player, page + 1, this.loginTableList).open();
                    })
                    .build());
        }

        if (page > 0) {
            this.setSlot(45, new GuiElementBuilder(Items.ARROW)
                    .setName(Text.literal("Previous Page"))
                    .setCallback((index, type, action) -> {
                        new LoginHistoryGui(player, page - 1, this.loginTableList).open();
                    })
                    .build());
        }
//        System.out.println(TableListGui.activeTables);
    }


}
