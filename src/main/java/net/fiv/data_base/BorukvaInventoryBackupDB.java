package net.fiv.data_base;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
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
        ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:mods/borukva_death_backup/database.db");
        TableUtils.createTableIfNotExists(connectionSource, DeathTable.class);
        TableUtils.createTableIfNotExists(connectionSource, LoginTable.class);
        TableUtils.createTableIfNotExists(connectionSource, LogoutTable.class);

        deathTableDao = DaoManager.createDao(connectionSource, DeathTable.class);
        loginTableDao = DaoManager.createDao(connectionSource, LoginTable.class);
        logoutTableDao = DaoManager.createDao(connectionSource, LogoutTable.class);
    }

    public void addDataDeath(String name, String world, String place,
                                   String date, String reason, String inventory, String armor, String offHand,int xp) throws SQLException {
        DeathTable deathTable = new DeathTable();

        deleteOldestRecord(name, deathTableDao);

        deathTable.setName(name);
        deathTable.setWorld(world);
        deathTable.setPlace(place);
        deathTable.setDate(date);
        deathTable.setReason(reason);
        deathTable.setInventory(inventory);
        deathTable.setArmor(armor);
        deathTable.setOffHand(offHand);
        deathTable.setXp(xp);
        deathTableDao.create(deathTable);
    }

    public void addDataLogin(String name, String world, String place,
                             String date, String inventory, String armor, String offHand, int xp) throws SQLException{
        LoginTable loginTable = new LoginTable();

        deleteOldestRecord(name, loginTableDao);

        loginTable.setName(name);
        loginTable.setWorld(world);
        loginTable.setPlace(place);
        loginTable.setDate(date);
        loginTable.setInventory(inventory);
        loginTable.setArmor(armor);
        loginTable.setOffHand(offHand);
        loginTable.setXp(xp);
        loginTableDao.create(loginTable);
    }

    public void addDataLogout(String name, String world, String place,
                             String date, String inventory, String armor, String offHand, int xp) throws SQLException{
        LogoutTable logoutTable = new LogoutTable();


        deleteOldestRecord(name, logoutTableDao);

        logoutTable.setName(name);
        logoutTable.setWorld(world);
        logoutTable.setPlace(place);
        logoutTable.setDate(date);
        logoutTable.setInventory(inventory);
        logoutTable.setArmor(armor);
        logoutTable.setOffHand(offHand);
        logoutTable.setXp(xp);
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
                Table oldestRecord = results.getFirst();
                dao.delete(oldestRecord);
            }
        }
    }

    public boolean playerLoginTableExist(String playerName) throws SQLException{
        List<LoginTable> results = loginTableDao.queryForEq("name", playerName);
        if (results != null && !results.isEmpty()) {
            return true;
        }
        return false;
    }

}
