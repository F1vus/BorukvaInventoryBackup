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
@DatabaseTable(tableName = "pre_restore_table")
public class PreRestoreTable extends BaseEntity {

    @DatabaseField(dataType = DataType.BOOLEAN)
    private boolean tableType;

    public PreRestoreTable(String name, String date, String inventory, String armor, String offHand, String enderChest, boolean tableType,int xp) {
        super(name, date, inventory, armor, offHand, enderChest, xp);
        this.tableType = tableType;
    }
}

