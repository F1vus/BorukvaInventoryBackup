package net.fiv.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fiv.BorukvaInventoryBackup;
import net.minecraft.nbt.*;

public class InventorySerializer {

    public static NbtCompound deserializeInventory(String json) {
        NbtCompound inventoryTag = new NbtCompound();
        //System.out.println("Json "+json);

        try{
            //System.out.println("Json: "+json);
           // json = json.substring( 1, json.length() - 1 );
            json = "{" + "Inventory: "+ json + "}";
            //System.out.println("JsonAFT: "+json);

            inventoryTag = net.minecraft.nbt.StringNbtReader.readCompound(json);

            //System.out.println("NBT: "+inventoryTag.toString());


            return validateComponents(inventoryTag);
        } catch (CommandSyntaxException e){
            BorukvaInventoryBackup.LOGGER.error(e.getMessage());
        }

        //System.out.println("SeriInvTag: "+inventoryTag);
        return inventoryTag;
    }

    private static NbtCompound validateComponents(NbtCompound compound){
        NbtList oldList = compound.getList("Inventory").get();
        //System.out.println("CompudBEF "+compound);
        for(int i=0; i<oldList.size(); i++){
            NbtCompound elem = (NbtCompound)oldList.get(i);
            if(elem.contains("count") && elem.contains("id")){
                elem.putInt("Count", elem.getInt("count").get());
                elem.remove("count");
                elem.putByte("Slot", (byte) i);
            }
            if(elem.getCompound("components").isEmpty()){
                elem.remove("components");
            }

            oldList.set(i, elem);
        }
        compound.remove("Inventory");
        compound.put("Inventory", oldList);
        //System.out.println("CompoudAFT "+compound);
        return compound;
    }


}
