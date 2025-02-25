package net.fiv.data_base.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DatabaseTable(tableName = "pre_restore_table")
public class PreRestoreTable implements Table {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(dataType = DataType.STRING)
    private String name;

    @DatabaseField(dataType = DataType.STRING)
    private String date;

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

    @DatabaseField(dataType = DataType.BOOLEAN)
    private boolean tableType;

    public PreRestoreTable(String name, String date, String inventory, String armor, String offHand, String enderChest, boolean tableType, int xp){
        this.name = name;
        this.date = date;
        this.inventory = inventory;
        this.armor = armor;
        this.offHand = offHand;
        this.enderChest = enderChest;
        this.xp = xp;
        this.tableType = tableType;

    }

    public PreRestoreTable(){
    }

}

