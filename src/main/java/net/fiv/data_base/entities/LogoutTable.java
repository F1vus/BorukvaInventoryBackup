package net.fiv.data_base.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "logout_table")
public class LogoutTable implements Table{

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

    public LogoutTable(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }

    public int getXp() {
        return xp;
    }

    public String getArmor() {
        return armor;
    }

    public void setArmor(String armor) {
        this.armor = armor;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOffHand() {
        return offHand;
    }

    public void setOffHand(String offHand) {
        this.offHand = offHand;
    }
}
