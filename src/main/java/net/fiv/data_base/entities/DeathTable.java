package net.fiv.data_base.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "death_table")
public class DeathTable extends BaseEntity {

    @DatabaseField(dataType = DataType.STRING)
    private String world;

    @DatabaseField(dataType = DataType.STRING)
    private String place;

    @DatabaseField(dataType = DataType.STRING)
    private String reason;

    public DeathTable(String name, String world, String place, String date, String inventory, String armor, String offHand, String enderChest, int xp, String reason) {
        super(name, date, inventory, armor, offHand, enderChest, xp);
        this.world = world;
        this.place = place;
        this.reason = reason;
    }
}

