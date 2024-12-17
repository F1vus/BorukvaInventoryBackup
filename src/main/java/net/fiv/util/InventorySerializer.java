package net.fiv.util;

import com.google.gson.*;
import com.google.gson.internal.LazilyParsedNumber;
import net.minecraft.nbt.*;

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
        System.out.println("Components"+componentsObject);
        for (String key : componentsObject.keySet()) {
            JsonElement value = componentsObject.get(key);

            if (value.isJsonPrimitive()) {
                JsonPrimitive primitive = value.getAsJsonPrimitive();

                if (primitive.isNumber()) {
                    System.out.println("Int: "+primitive.getAsInt());
                    componentsTag.putInt(key, primitive.getAsInt());
                } else if (primitive.isString()) {

                    String strnum = primitive.getAsString().toLowerCase();
                    System.out.println("Str: "+strnum);
                    if (strnum.endsWith("b")) {
                        strnum = strnum.substring(0, strnum.length() - 1);
                        if (strnum.startsWith("0b")) {
                            componentsTag.putByte(key, Byte.parseByte(strnum.substring(2), 2));
                        } else {
                            componentsTag.putByte(key, Byte.parseByte(strnum));
                        }
                    } else if (strnum.endsWith("f")) {
                        strnum = strnum.substring(0, strnum.length() - 1);
                        componentsTag.putFloat(key, Float.parseFloat(strnum));
                    } else if (strnum.endsWith("d")) {
                        strnum = strnum.substring(0, strnum.length() - 1);
                        componentsTag.putDouble(key, Double.parseDouble(strnum));
                    } else {
                        componentsTag.putString(key, primitive.getAsString());
                    }
                }


            } else if (value.isJsonObject()) {
                System.out.println("Object: "+value.getAsJsonObject());
                componentsTag.put(key, deserializeComponents(value.getAsJsonObject()));
            } else if (key.equals("minecraft:enchantments") && value.isJsonObject()) {
                System.out.println("ObjectJson: "+value.getAsJsonObject());
                JsonObject enchantmentsObject = value.getAsJsonObject();
                if (enchantmentsObject.has("levels") && enchantmentsObject.get("levels").isJsonObject()) {
                    componentsTag.put(key, deserializeComponents(enchantmentsObject.getAsJsonObject("levels")));
                }
            } else if(value.isJsonArray()){
                JsonArray jsonArray = value.getAsJsonArray();
                System.out.println("Ar: "+jsonArray.get(0).getAsString());
                if(jsonArray.get(0).getAsString().equals("I")){
                    jsonArray.remove(0);
                    int[] x = new int[jsonArray.size()];
                    int indx = 0;
                    for(JsonElement num: jsonArray){
                        x[indx] = num.getAsInt();
                        System.out.println("I: "+x[indx]);
                        indx++;
                    }

                    componentsTag.putIntArray(key, x);
                } else if(jsonArray.get(0).getAsString().equals("B")){
                    jsonArray.remove(0);
                    byte[] x = new byte[jsonArray.size()];
                    int indx = 0;
                    for(JsonElement num: jsonArray){
                        x[indx] = num.getAsByte();
                        System.out.println("B: "+x[indx]);
                        indx++;
                    }

                    componentsTag.putByteArray(key, x);
                } else if(jsonArray.get(0).getAsString().equals("L")){
                    jsonArray.remove(0);
                    long[] x = new long[jsonArray.size()];
                    int indx = 0;
                    for(JsonElement num: jsonArray){
                        x[indx] = num.getAsLong();
                        System.out.println("L: "+x[indx]);
                        indx++;
                    }

                    componentsTag.putLongArray(key, x);
                } else {
                    String strnum = jsonArray.get(0).getAsString();
                    NbtList nbtList = new NbtList();

                    System.out.println("Str: "+strnum);
                    if (strnum.endsWith("d")) {
                        int indx = 0;
                        for(JsonElement elem: jsonArray){
                            String el = elem.getAsString();
                            el = el.substring(0, el.length() - 1);
                            nbtList.addElement(indx,NbtDouble.of(Double.parseDouble(el)));
                            indx++;
                        }

                    } else if (strnum.contains("f")) {
                        int indx = 0;
                        for(JsonElement elem: jsonArray){
                            String el = elem.getAsString();
                            el = el.substring(0, el.length() - 1);
                            nbtList.addElement(indx,NbtFloat.of(Float.parseFloat(el)));
                            indx++;
                        }
                    } else {
                        int indx = 0;
                        for(JsonElement elem: jsonArray){
                            nbtList.addElement(indx, NbtString.of(elem.getAsString()));
                            indx++;
                        }
                     }
                    componentsTag.put(key, nbtList);
                }
            }
        }
        System.out.println("Return: "+componentsTag);
        return componentsTag;
    }

}
