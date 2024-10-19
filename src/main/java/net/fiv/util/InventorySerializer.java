package net.fiv.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class InventorySerializer {

    private static final Gson gson = new Gson();

    public static NbtCompound deserializeInventory(String json) {
        NbtCompound inventoryTag = new NbtCompound();
        NbtList inventoryList = new NbtList();
        //System.out.println("Json: "+json);
        JsonArray jsonArray = gson.fromJson(json, JsonArray.class);
        //System.out.println("JsonArray: "+jsonArray);

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonElement element = jsonArray.get(i);
            if (element.isJsonObject()) {
                JsonObject itemObject = element.getAsJsonObject();
                if (itemObject.has("count") && itemObject.has("id")) { // Only add items with count > 0
                    NbtCompound itemTag = new NbtCompound();
                    itemTag.putByte("Slot", (byte) i);
                    itemTag.putInt("Count", itemObject.get("count").getAsInt());
                    itemTag.putString("id", itemObject.get("id").getAsString());

                    if (itemObject.has("components") && itemObject.get("components").isJsonObject()) {
                        NbtCompound componentsTag =  deserializeComponents(itemObject.getAsJsonObject("components"));
                        if (!componentsTag.isEmpty()) { // Only add components if they exist
                            itemTag.put("components", componentsTag);
                        }
                    }

                    inventoryList.add(itemTag);
                }
            }
        }

        inventoryTag.put("Inventory", inventoryList);
        //System.out.println("SeriInvTag: "+inventoryTag);
        return inventoryTag;
    }



    private static NbtCompound deserializeComponents(JsonObject componentsObject) {
        NbtCompound componentsTag = new NbtCompound();

        for (String key : componentsObject.keySet()) {
            JsonElement value = componentsObject.get(key);

            if (value.isJsonPrimitive()) {
                if (value.getAsJsonPrimitive().isString()) {
                    componentsTag.putString(key, value.getAsString());
                } else if (value.getAsJsonPrimitive().isNumber()) {
                    componentsTag.putInt(key, value.getAsInt()); // Assumes numbers are integers for now. Adjust if needed.
                }
            } else if (value.isJsonObject()) {
                componentsTag.put(key, deserializeComponents(value.getAsJsonObject()));
            } else if (key.equals("minecraft:enchantments") && value.isJsonObject()) {  // Handle enchantments specifically
                JsonObject enchantmentsObject = value.getAsJsonObject();
                if (enchantmentsObject.has("levels") && enchantmentsObject.get("levels").isJsonObject()) {
                    componentsTag.put(key, deserializeComponents(enchantmentsObject.getAsJsonObject("levels")));
                }
            }
        }

        return componentsTag;
    }

}
