package net.fiv.data_base.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BaseEntity {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(dataType = DataType.STRING)
    private String name;

    @DatabaseField
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

    public BaseEntity(String name, String date, String inventory, String armor, String offHand, String enderChest, int xp) {
        this.name = name;
        this.date = date;
        this.inventory = inventory;
        this.armor = armor;
        this.offHand = offHand;
        this.enderChest = enderChest;
        this.xp = xp;
    }
}
