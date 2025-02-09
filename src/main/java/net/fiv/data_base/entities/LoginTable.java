package net.fiv.data_base.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@DatabaseTable(tableName = "login_table")
public class LoginTable implements Table{

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

    @DatabaseField(dataType = DataType.STRING, width = 100000)
    private String inventory;

    @DatabaseField(dataType = DataType.STRING, width = 10000)
    private String armor;

    @DatabaseField(dataType = DataType.STRING, width = 10000)
    private String offHand;

    @DatabaseField(dataType = DataType.STRING, width = 10000)
    private String enderChest;

    @DatabaseField(dataType = DataType.INTEGER)
    private int xp;

    public LoginTable(String name, String world, String place, String date,
                      String inventory, String armor, String offHand, String enderChest,int xp){
        this.name = name;
        this.world = world;
        this.place = place;
        this.date = date;
        this.inventory = inventory;
        this.armor = armor;
        this.offHand = offHand;
        this.enderChest = enderChest;
        this.xp = xp;
    }

    public LoginTable(){
    }

}
