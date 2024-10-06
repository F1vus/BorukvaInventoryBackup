package net.fiv.data_base.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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

}
