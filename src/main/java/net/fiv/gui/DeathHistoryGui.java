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

    private static HashMap<String, Integer> playerIndices = new HashMap<>();

    private List<DeathTable> deathTableList = new ArrayList<>();

    public DeathHistoryGui(ServerPlayerEntity player) {
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
            int xp = deathTableList.get(i).getXp();
            this.setSlot(i, new GuiElementBuilder(Items.CHEST)
                    .setName(Text.literal("Time: "+deathTableList.get(i).getDate()))
                    .addLoreLine(Text.literal("Death reason: "+deathTableList.get(i).getReason()))
                    .addLoreLine(Text.literal("World: "+deathTableList.get(i).getWorld()))
                    .addLoreLine(Text.literal("Place: "+deathTableList.get(i).getPlace()))
                    .addLoreLine(Text.literal("XpLevel: "+deathTableList.get(i).getXp()))
                    .setCallback((index, type, action) -> {
                         Map<Integer, ItemStack> itemStackList = TableListGui.inventorySerialization(inventory, armor, offHand, player);
                         new InventoryGui(player, deathTableList.getFirst().getName(), itemStackList, xp).open();
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


