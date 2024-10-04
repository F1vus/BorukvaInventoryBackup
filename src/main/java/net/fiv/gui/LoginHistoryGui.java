package net.fiv.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.fiv.BorukvaInventoryBackup;
import net.fiv.data_base.entities.LoginTable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoginHistoryGui extends SimpleGui {

    public static final Logger LOGGER = LoggerFactory.getLogger(BorukvaInventoryBackup.MOD_ID);

    private static HashMap<String, Integer> playerIndices = new HashMap<>();

    private List<LoginTable> loginTableList = new ArrayList<>();

    public LoginHistoryGui(ServerPlayerEntity player) {
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
            this.setSlot(i, new GuiElementBuilder(Items.CHEST)
                    .setName(Text.literal("Time: "+loginTableList.get(i).getDate()))
                    .addLoreLine(Text.literal("World: "+loginTableList.get(i).getWorld()))
                    .addLoreLine(Text.literal("Place: "+loginTableList.get(i).getPlace()))
                    .setCallback((index, type, action) -> {
                        List<ItemStack> itemStackList = TableListGui.inventorySerialization(inventory, armor, offHand);
                        new InventoryGui(player, loginTableList.getFirst().getName(), itemStackList).open();
                    })
                    .build());
        }
    }

    public void setLoginTableList(List<LoginTable> loginTableList){
        this.loginTableList = loginTableList;
    }

    private int getCurrentIndex(int guiListSize){
        String playerName = player.getName().getString();
        int currentIndex = playerIndices.getOrDefault(playerName, 1);
        currentIndex--;
        if (currentIndex < 1) {
            currentIndex = guiListSize - 1;
            return currentIndex;
        }
        return currentIndex;
    }


}
