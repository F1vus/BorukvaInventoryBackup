package net.fiv.data_base;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import net.fiv.BorukvaInventoryBackup;
import net.fiv.config.ModConfigs;
import net.fiv.data_base.entities.*;

import java.sql.SQLException;
import java.util.List;


public class BorukvaInventoryBackupDB {
    private Dao<DeathTable, String> deathTableDao;
    private Dao<LoginTable, String> loginTableDao;
    private Dao<LogoutTable, String> logoutTableDao;
    private Dao<PreRestoreTable, String> preRestoreTableDao;
    private final JdbcConnectionSource connectionSource;

    public BorukvaInventoryBackupDB() throws SQLException {
        connectionSource = new JdbcConnectionSource("jdbc:h2:./"+BorukvaInventoryBackup.MOD_ID);
        init();

        modifyTableForH2();
    }

    public BorukvaInventoryBackupDB(String url, String user, String password) throws SQLException {
        connectionSource = new JdbcConnectionSource(url, user, password);
        init();
    }

    private void init() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DeathTable.class);
        TableUtils.createTableIfNotExists(connectionSource, LoginTable.class);
        TableUtils.createTableIfNotExists(connectionSource, LogoutTable.class);
        TableUtils.createTableIfNotExists(connectionSource, PreRestoreTable.class);

        deathTableDao = DaoManager.createDao(connectionSource, DeathTable.class);
        loginTableDao = DaoManager.createDao(connectionSource, LoginTable.class);
        logoutTableDao = DaoManager.createDao(connectionSource, LogoutTable.class);
        preRestoreTableDao = DaoManager.createDao(connectionSource, PreRestoreTable.class);
    }


    public void addDataDeath(String name, String world, String place,
                             String date, String reason, String inventory, String armor, String offHand, String enderChest, int xp) throws SQLException {

        deleteOldestRecord(name, deathTableDao);

        DeathTable deathTable = new DeathTable(name, world, place, date, inventory, armor, offHand, enderChest, xp, reason);

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

    public void addDataPreRestore(String name, String date, String inventory, String armor, String offHand, String enderChest, boolean isInventory,int xp) throws SQLException{
        deleteOldestRecord(name, preRestoreTableDao);

        PreRestoreTable preRestoreTable = new PreRestoreTable(name, date, inventory, armor, offHand, enderChest, isInventory,xp);

        preRestoreTableDao.create(preRestoreTable);
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

    public List<PreRestoreTable> getPreRestoreData(String playerName) throws SQLException{
        List<PreRestoreTable> results = preRestoreTableDao.queryForEq("name", playerName);
        if (results != null && !results.isEmpty()) {
            return results;
        }
        return null;
    }

    private <T extends BaseEntity> void deleteOldestRecord(String playerName, Dao<T, String> dao) throws SQLException{
        List<T> results = dao.queryForEq("name", playerName);

        if (results != null && !results.isEmpty()) {
            int maxRecords = ModConfigs.getCONFIG()
                    .getOrDefault("key.borukvaInventoryBackup.MAX_RECORDS", 100);

            if (results.size() >= maxRecords) {
                int recordsForDelete = results.size() - maxRecords;
                int index = 0;
                while (recordsForDelete > 0) {
                    T oldestRecord = results.get(index);
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

    private void modifyTableForH2() throws SQLException {

        String[] alterStatementsDeath = {
                "ALTER TABLE death_table ALTER COLUMN inventory VARCHAR(2000000);",  // 2 000 000
                "ALTER TABLE death_table ALTER COLUMN armor VARCHAR(1000000);",  // 1 000 000
                "ALTER TABLE death_table ALTER COLUMN offHand VARCHAR(300000);", // 300 000
                "ALTER TABLE death_table ALTER COLUMN enderChest VARCHAR(2000000);" // 2 000 000
        };
        String[] alterStatementsLogin = {
                "ALTER TABLE login_table ALTER COLUMN inventory VARCHAR(2000000);", // 2 000 000
                "ALTER TABLE login_table ALTER COLUMN armor VARCHAR(1000000);", // 1 000 000
                "ALTER TABLE login_table ALTER COLUMN offHand VARCHAR(300000);", // 300 000
                "ALTER TABLE login_table ALTER COLUMN enderChest VARCHAR(2000000);", // 2 000 000
        };
        String[] alterStatementsLogout = {
                "ALTER TABLE logout_table ALTER COLUMN inventory VARCHAR(2000000);", // 2 000 000
                "ALTER TABLE logout_table ALTER COLUMN armor VARCHAR(1000000);", // 1 000 000
                "ALTER TABLE logout_table ALTER COLUMN offHand VARCHAR(300000);", // 300 000
                "ALTER TABLE logout_table ALTER COLUMN enderChest VARCHAR(2000000);", // 2 000 000

        };

        String[] alterStatementsPreRestore = {
                "ALTER TABLE pre_restore_table ALTER COLUMN inventory VARCHAR(2000000);", // 2 000 000
                "ALTER TABLE pre_restore_table ALTER COLUMN armor VARCHAR(1000000);", // 1 000 000
                "ALTER TABLE pre_restore_table ALTER COLUMN offHand VARCHAR(300000);", // 300 000
                "ALTER TABLE pre_restore_table ALTER COLUMN enderChest VARCHAR(2000000);", // 2 000 000

        };

        for(String sql: alterStatementsDeath) {
            deathTableDao.executeRawNoArgs(sql);
        }
        for(String sql: alterStatementsLogin) {
            loginTableDao.executeRawNoArgs(sql);
        }
        for(String sql: alterStatementsLogout) {
            logoutTableDao.executeRawNoArgs(sql);
        }
        for(String sql: alterStatementsPreRestore){
            preRestoreTableDao.executeRawNoArgs(sql);
        }
    }
    public void closeDbConnection() throws Exception {
        connectionSource.close();
    }
}
