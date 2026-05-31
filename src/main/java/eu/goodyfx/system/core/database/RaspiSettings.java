package eu.goodyfx.system.core.database;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.utils.MojangPlayerWrapper;
import eu.goodyfx.system.core.utils.Settings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

@Getter
@Setter
public class RaspiSettings {

    private final String uuid;
    private final String username;

    private final String table = DatabaseTables.USER_SETTINGS.getTableName();
    private final McRaspiSystem plugin;
    private final DatabaseManager databaseManager;

    private final Map<Settings, Boolean> settingsMap = new EnumMap<>(Settings.class);


    public RaspiSettings(UUID uuid) {
        this.plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
        this.databaseManager = plugin.getDatabaseManager();
        this.uuid = uuid.toString();
        this.username = MojangPlayerWrapper.getName(uuid);

        //Standard values for all Settings BEFORE fetch
        for (Settings setting : Settings.values()) {
            settingsMap.put(setting, false);
        }

    }

    /**
     * Helper Method to get Player Setting
     *
     * @param setting The Requested Setting
     * @return TRUE if Setting is set.
     */
    public boolean get(Settings setting) {
        return settingsMap.getOrDefault(setting, false);
    }

    public void set(Settings setting, boolean value) {
        settingsMap.put(setting, value);
    }


    public void fetch() {
        try (Connection connection = databaseManager.getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(String.format("SELECT * FROM %s WHERE uuid = ?", table))) {
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                for (Settings setting : Settings.values()) {
                    boolean val = resultSet.getBoolean(setting.getDb_column());
                    settingsMap.put(setting, val);
                }
                plugin.getDebugger().debug(String.format("[RaspiSettings] Fetched userSettings for %s successfully.", username));
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("[UserSetting] Failed to Fetch userSettings for %s in %s", username, table), e);
        }
    }

    public void write() {
        try (Connection connection = databaseManager.getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(String.format("INSERT INTO %s(uuid, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE name = VALUES(name)", table))) {
            statement.setString(1, uuid);
            statement.setString(2, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Failed to write settings to %s for %s", table, username), e);
        }

        if (databaseManager.getFallBackManager().containOld(UUID.fromString(uuid))) {
            databaseManager.getFallBackManager().perform(this);
            update();
        }

    }

    /**
     * Dynamic DB Update Method
     */
    public void update() {
        StringBuilder sqlStringBuilder = new StringBuilder("UPDATE ").append(table).append(" SET ");
        for (Settings setting : Settings.values()) {
            sqlStringBuilder.append(setting.getDb_column()).append(" = ?, ");
        }
        //Letzte , entfernen
        sqlStringBuilder.setLength(sqlStringBuilder.length() - 2);
        sqlStringBuilder.append(" WHERE uuid = ?");
        try (Connection connection = databaseManager.getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(sqlStringBuilder.toString())) {
            int index = 1;
            for (Settings setting : Settings.values()) {
                statement.setBoolean(index++, get(setting));
            }
            statement.setString(index, uuid);
            statement.executeUpdate();
            Raspi.debugger().debug(String.format("Updated User Settings for %s", username), "MYSQL");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Failed to update Data in %s for %s", table, username), e);
        }
    }


}
