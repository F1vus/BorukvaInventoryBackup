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
@DatabaseTable(tableName = "login_table")
public class LoginTable extends BaseEntity {

    @DatabaseField(dataType = DataType.STRING)
    private String world;

    @DatabaseField(dataType = DataType.STRING)
    private String place;

    public LoginTable(String name, String world, String place, String date, String inventory, String armor, String offHand, String enderChest, int xp) {
        super(name, date, inventory, armor, offHand, enderChest, xp);
        this.world = world;
        this.place = place;
    }
}
