package eu.goodyfx.system.core.database;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.MojangPlayerWrapper;
import eu.goodyfx.system.core.utils.Settings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

@Getter
@Setter
public class UserSettings {

    private final String uuid;
    private final String username;

    private boolean afk = false;
    private boolean auto_afk = false;
    private boolean opt_chat = false;
    private boolean server_messages = false;

    private final String table = DatabaseTables.USER_SETTINGS.getTableName();
    private final McRaspiSystem plugin;
    private final DatabaseManager databaseManager;

    private final Map<Settings, Boolean> settingsMap = new HashMap<>();


    public UserSettings(UUID uuid) {
        this.plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
        this.databaseManager = plugin.getDatabaseManager();
        this.uuid = uuid.toString();
        this.username = MojangPlayerWrapper.getName(uuid);
    }

    public void fetch() {
        try (Connection connection = databaseManager.getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(String.format("SELECT * FROM %s WHERE uuid = ?", table))) {
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                afk = resultSet.getBoolean("afk");
                auto_afk = resultSet.getBoolean("auto_afk");
                opt_chat = resultSet.getBoolean("opt_chat");
                server_messages = resultSet.getBoolean("server_messages");
                plugin.getDebugger().info(String.format("[UserSettings] Fetched userSettings for %s successfully.", username));
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

    public void update() {
        try (Connection connection = databaseManager.getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(String.format("UPDATE %s SET afk = ?, auto_afk = ?, opt_chat = ?, server_messages = ? WHERE uuid = ?", table))) {
            statement.setBoolean(1, afk);
            statement.setBoolean(2, auto_afk);
            statement.setBoolean(3, opt_chat);
            statement.setBoolean(4, server_messages);
            statement.setString(5, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Failed to update Data in %s for %s", table, username), e);
        }
    }


}
