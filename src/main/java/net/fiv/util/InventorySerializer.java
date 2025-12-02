package net.fiv.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fiv.BorukvaInventoryBackup;
import net.minecraft.nbt.*;

public class InventorySerializer {

    public static NbtCompound deserializeInventory(String json) {
        NbtCompound inventoryTag = new NbtCompound();

        try{
            json = "{" + "Inventory: "+ json + "}";

            inventoryTag = net.minecraft.nbt.StringNbtReader.readCompound(json);

            return validateComponents(inventoryTag);
        } catch (CommandSyntaxException e){
            BorukvaInventoryBackup.LOGGER.error(e.getMessage());
        }

        return inventoryTag;
    }

    private static NbtCompound validateComponents(NbtCompound compound){
        NbtList oldList = compound.getList("Inventory").get();

        for(int i=0; i<oldList.size(); i++){
            NbtCompound elem = (NbtCompound)oldList.get(i);
            if(elem.contains("count") && elem.contains("id")){
                elem.putByte("Slot", (byte) i);
            }
            if(elem.getCompound("components").isEmpty()){
                elem.remove("components");
            }

            oldList.set(i, elem);
        }

        compound.remove("Inventory");
        compound.put("Inventory", oldList);
        return compound;
    }


}
