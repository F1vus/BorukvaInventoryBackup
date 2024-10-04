package net.fiv.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.fiv.BorukvaInventoryBackup;
import net.fiv.data_base.entities.DeathTable;
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

public class DeathHistoryGui extends SimpleGui {

    public static final Logger LOGGER = LoggerFactory.getLogger(BorukvaInventoryBackup.MOD_ID);

    private static HashMap<String, Integer> playerIndices = new HashMap<>();

    private List<DeathTable> deathTableList = new ArrayList<>();

    public DeathHistoryGui(ServerPlayerEntity player) {
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

                        DeathHistoryGui deathHistoryGui = (DeathHistoryGui) guiList.get(currentIndex);
                        deathHistoryGui.addButtons();
                        deathHistoryGui.open();
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

                        DeathHistoryGui deathHistoryGui = (DeathHistoryGui) guiList.get(currentIndex);
                        deathHistoryGui.addButtons();
                        deathHistoryGui.open();
                    }
                })
                .build());


        for(int i=0; i<deathTableList.size(); i++){
            String inventory = deathTableList.get(i).getInventory();
            String armor = deathTableList.get(i).getArmor();
            String offHand = deathTableList.get(i).getOffHand();
            this.setSlot(i, new GuiElementBuilder(Items.CHEST)
                    .setName(Text.literal("Time: "+deathTableList.get(i).getDate()))
                    .addLoreLine(Text.literal("Death reason: "+deathTableList.get(i).getReason()))
                    .addLoreLine(Text.literal("World: "+deathTableList.get(i).getWorld()))
                    .addLoreLine(Text.literal("Place: "+deathTableList.get(i).getPlace()))
                    .setCallback((index, type, action) -> {
                        List<ItemStack> itemStackList = TableListGui.inventorySerialization(inventory, armor, offHand);
                         new InventoryGui(player, deathTableList.getFirst().getName(), itemStackList).open();
                    })
                    .build());
        }
    }

    public void setDeathTableList(List<DeathTable> deathTableList){
        this.deathTableList = deathTableList;
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


