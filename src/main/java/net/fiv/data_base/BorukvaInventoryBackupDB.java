package net.fiv.data_base;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import net.fiv.BorukvaInventoryBackup;
import net.fiv.config.ModConfigs;
import net.fiv.data_base.entities.DeathTable;
import net.fiv.data_base.entities.LoginTable;
import net.fiv.data_base.entities.LogoutTable;
import net.fiv.data_base.entities.Table;

import java.sql.SQLException;
import java.util.List;


public class BorukvaInventoryBackupDB {
    private final Dao<DeathTable, String> deathTableDao;
    private final Dao<LoginTable, String> loginTableDao;
    private final Dao<LogoutTable, String> logoutTableDao;

    public BorukvaInventoryBackupDB() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource("jdbc:h2:./"+BorukvaInventoryBackup.MOD_ID);

        TableUtils.createTableIfNotExists(connectionSource, DeathTable.class);
        TableUtils.createTableIfNotExists(connectionSource, LoginTable.class);
        TableUtils.createTableIfNotExists(connectionSource, LogoutTable.class);

        deathTableDao = DaoManager.createDao(connectionSource, DeathTable.class);
        loginTableDao = DaoManager.createDao(connectionSource, LoginTable.class);
        logoutTableDao = DaoManager.createDao(connectionSource, LogoutTable.class);
        
    }

    public void addDataDeath(String name, String world, String place,
                                   String date, String reason, String inventory, String armor, String offHand, String enderChest, int xp) throws SQLException {

        deleteOldestRecord(name, deathTableDao);

        DeathTable deathTable = new DeathTable(name, world, place, date, reason, inventory, armor, offHand, enderChest,xp);

        deathTableDao.create(deathTable);
    }

    public void addDataLogin(String name, String world, String place,
                             String date, String inventory, String armor, String offHand, String enderChest,int xp) throws SQLException{

        deleteOldestRecord(name, loginTableDao);

        LoginTable loginTable = new LoginTable(name , world, place, date, inventory, armor, offHand, enderChest,xp);

        loginTableDao.create(loginTable);
    }

    public void addDataLogout(String name, String world, String place,
                             String date, String inventory, String armor, String offHand, String enderChest,int xp) throws SQLException{

        deleteOldestRecord(name, logoutTableDao);

        LogoutTable logoutTable = new LogoutTable(name, world, place, date, inventory, armor, offHand, enderChest,xp);

        logoutTableDao.create(logoutTable);
    }

    public List<DeathTable> getDeathData(String playerName) throws SQLException{
        List<DeathTable> results = deathTableDao.queryForEq("name", playerName);
        if (results != null && !results.isEmpty()) {
            return results;
        }
        return null;
    }

    public List<LoginTable> getLoginData(String playerName) throws SQLException{
        List<LoginTable> results = loginTableDao.queryForEq("name", playerName);
        if (results != null && !results.isEmpty()) {
            return results;
        }
        return null;
    }

    public List<LogoutTable> getLogoutData(String playerName) throws SQLException{
        List<LogoutTable> results = logoutTableDao.queryForEq("name", playerName);
        if (results != null && !results.isEmpty()) {
            return results;
        }
        return null;
    }

    private void deleteOldestRecord(String playerName, Dao dao) throws SQLException{
        List<Table> results = dao.queryForEq("name", playerName);
        if(results != null && !results.isEmpty()){
            int maxRecords = ModConfigs.getCONFIG().getOrDefault("key.borukvaInventoryBackup.MAX_RECORDS", 100);
            if (results.size() >= maxRecords) {
                int recordsForDelete = results.size() - maxRecords;
                int index = 0;
                while(recordsForDelete >= 0){
                    Table oldestRecord = results.get(index);
                    dao.delete(oldestRecord);
                    index++;
                    recordsForDelete--;
                }
            }
        }
    }


    public boolean playerLoginTableExist(String playerName) throws SQLException{
        List<LoginTable> results = loginTableDao.queryForEq("name", playerName);
        return results != null && !results.isEmpty();
    }

}
