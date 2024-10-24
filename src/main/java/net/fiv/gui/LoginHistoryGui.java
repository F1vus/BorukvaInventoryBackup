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

    private static HashMap<String, Integer> playerIndices = new HashMap<>();

    private List<LoginTable> loginTableList = new ArrayList<>();

    public LoginHistoryGui(ServerPlayerEntity player) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);

    }

    @Override
    public boolean canPlayerClose() {
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

                        LoginHistoryGui loginHistoryGui = (LoginHistoryGui) guiList.get(currentIndex);
                        loginHistoryGui.addButtons();
                        loginHistoryGui.open();
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

                        LoginHistoryGui loginHistoryGui = (LoginHistoryGui) guiList.get(currentIndex);
                        loginHistoryGui.addButtons();
                        loginHistoryGui.open();
                    }
                })
                .build());


        for(int i=0; i<loginTableList.size(); i++){
            String inventory = loginTableList.get(i).getInventory();
            String armor = loginTableList.get(i).getArmor();
            String offHand = loginTableList.get(i).getOffHand();
            int xp = loginTableList.get(i).getXp();
            this.setSlot(i, new GuiElementBuilder(Items.CHEST)
                    .setName(Text.literal("Time: "+loginTableList.get(i).getDate()))
                    .addLoreLine(Text.literal("World: "+loginTableList.get(i).getWorld()))
                    .addLoreLine(Text.literal("Place: "+loginTableList.get(i).getPlace()))
                    .addLoreLine(Text.literal("XpLevel: "+loginTableList.get(i).getXp()))
                    .setCallback((index, type, action) -> {
                        Map<Integer, ItemStack> itemStackList = TableListGui.inventorySerialization(inventory, armor, offHand, player);
                        new InventoryGui(player, loginTableList.getFirst().getName(), itemStackList, xp).open();
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
