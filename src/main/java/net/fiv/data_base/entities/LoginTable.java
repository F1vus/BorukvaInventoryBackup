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

    @DatabaseField(dataType = DataType.STRING)
    private String inventory;

    @DatabaseField(dataType = DataType.STRING)
    private String armor;

    @DatabaseField(dataType = DataType.STRING)
    private String offHand;

    @DatabaseField(dataType = DataType.INTEGER)
    private int xp;

    public LoginTable(String name, String world, String place, String date,
                      String inventory, String armor, String offHand, int xp){
        this.name = name;
        this.world = world;
        this.place = place;
        this.date = date;
        this.inventory = inventory;
        this.armor = armor;
        this.offHand = offHand;
        this.xp = xp;
        System.out.println("piwo");
    }

    public LoginTable(){
    }

}
