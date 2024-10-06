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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Setter
public class LogoutHistoryGui extends SimpleGui {
    private static HashMap<String, Integer> playerIndices = new HashMap<>();

    private List<LogoutTable> logoutTableList = new ArrayList<>();

    public LogoutHistoryGui(ServerPlayerEntity player) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);

    }

    @Override
    public boolean canPlayerClose() {
        TableListGui.activeTables.remove(this.player.getName().getString());
        return true;
    }


    public void addButtons(){
        this.setSlot(53, new GuiElementBuilder(Items.ARMOR_STAND)
                .setName(Text.literal("NextPage"))
                .setCallback((index, type, action) -> {
                    List<SimpleGui> guiList = TableListGui.activeTables.get(player.getName().getString());
                    if (guiList != null && !guiList.isEmpty()) {
                        String playerName = player.getName().getString();

                        int currentIndex = getCurrentIndex(guiList.size());
                        playerIndices.put(playerName, currentIndex);

                        LogoutHistoryGui logoutHistoryGui = (LogoutHistoryGui) guiList.get(currentIndex);
                        logoutHistoryGui.addButtons();
                        logoutHistoryGui.open();
                    }
                })
                .build());

        this.setSlot(52, new GuiElementBuilder(Items.ARMOR_STAND)
                .setName(Text.literal("PreviousPage"))
                .setCallback((index, type, action) -> {
                    List<SimpleGui> guiList = TableListGui.activeTables.get(player.getName().getString());
                    if (guiList != null && !guiList.isEmpty()) {
                        String playerName = player.getName().getString();

                        int currentIndex = getCurrentIndex(guiList.size());
                        playerIndices.put(playerName, currentIndex);

                        LogoutHistoryGui logoutHistoryGui = (LogoutHistoryGui) guiList.get(currentIndex);
                        logoutHistoryGui.addButtons();
                        logoutHistoryGui.open();
                    }
                })
                .build());


        for(int i=0; i<logoutTableList.size(); i++){
            String inventory = logoutTableList.get(i).getInventory();
            String armor = logoutTableList.get(i).getArmor();
            String offHand = logoutTableList.get(i).getOffHand();
            int xp = logoutTableList.get(i).getXp();
            this.setSlot(i, new GuiElementBuilder(Items.CHEST)
                    .setName(Text.literal("Time: "+logoutTableList.get(i).getDate()))
                    .addLoreLine(Text.literal("World: "+logoutTableList.get(i).getWorld()))
                    .addLoreLine(Text.literal("Place: "+logoutTableList.get(i).getPlace()))
                    .addLoreLine(Text.literal("XpLevel: "+logoutTableList.get(i).getXp()))
                    .setCallback((index, type, action) -> {
                        List<ItemStack> itemStackList = TableListGui.inventorySerialization(inventory, armor, offHand);
                        new InventoryGui(player, logoutTableList.getFirst().getName(), itemStackList, xp).open();
                    })
                    .build());
        }
//        System.out.println(TableListGui.activeTables);
    }

    private int getCurrentIndex(int guiListSize){
        String playerName = player.getName().getString();
        int currentIndex = playerIndices.getOrDefault(playerName, 0);
        currentIndex--;
        if (currentIndex < 0) {
            currentIndex = guiListSize - 1;
            return currentIndex;
        }
        return currentIndex;
    }
}
