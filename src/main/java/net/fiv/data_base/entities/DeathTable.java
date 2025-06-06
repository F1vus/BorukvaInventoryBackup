package net.fiv.data_base.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DatabaseTable(tableName = "death_table")
public class DeathTable implements Table {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(dataType = DataType.STRING)
    private String name;

    @DatabaseField(dataType = DataType.STRING)
    private String world;

    @DatabaseField(dataType = DataType.STRING)
    private String place;

    @DatabaseField(dataType = DataType.STRING)
    private String date;

    @DatabaseField(dataType = DataType.STRING)
    private String reason;

    @DatabaseField(dataType = DataType.LONG_STRING)
    private String inventory;

    @DatabaseField(dataType = DataType.LONG_STRING)
    private String armor;

    @DatabaseField(dataType = DataType.LONG_STRING)
    private String offHand;

    @DatabaseField(dataType = DataType.LONG_STRING)
    private String enderChest;

    @DatabaseField(dataType = DataType.INTEGER)
    private int xp;

    public DeathTable(String name, String world, String place, String date, String reason,String inventory,
                       String armor, String offHand, String enderChest, int xp) {
        this.name = name;
        this.world = world;
        this.place = place;
        this.date = date;
        this.reason = reason;
        this.inventory = inventory;
        this.armor = armor;
        this.offHand = offHand;
        this.enderChest = enderChest;
        this.xp = xp;
    }

    public DeathTable(){
    }

}

