package eu.goodyfx.system.core.database;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.MojangPlayerWrapper;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

@Getter
@Setter
public class RaspiUser {

    private String uuid;
    private String username;
    private Long first_join = System.currentTimeMillis();
    private String allowed_by = null;
    private Long lastSeen = -1L;
    private Integer onlineHours = 0;
    private String color = null;
    private String prefix = null;
    private String denied_by = null;
    private String deny_reason = null;
    private Boolean state = null;
    private String allowed_since = null;
    private Integer voting = 0;
    private Boolean played_before =null;

    protected final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    private final DatabaseManager databaseManager = plugin.getDatabaseManager();
    private final String table = DatabaseTables.USER_DATA.getTableName();

    public RaspiUser(UUID uuid) {
        this.uuid = uuid.toString();
        this.username = MojangPlayerWrapper.getName(uuid);
    }



    public boolean allowedToPlay() {
        return allowed_since != null;
    }

    public void fetch() {
        try (Connection connection = databaseManager.getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM user_data WHERE uuid = ?")) {
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                first_join = resultSet.getObject("first_doc", Long.class);
                denied_by = resultSet.getString("denied_by");
                deny_reason = resultSet.getString("deny_reason");
                allowed_since = resultSet.getString("allowed_since");
                allowed_by = resultSet.getString("allowed_by");
                state = resultSet.getObject("request_state", Boolean.class);
                lastSeen = resultSet.getObject("last_seen", Long.class);
                onlineHours = resultSet.getInt("online_hours");
                color = resultSet.getString("color");
                prefix = resultSet.getObject("prefix", String.class);
                voting = resultSet.getObject("voting", Integer.class);
                plugin.getDebugger().info(String.format("[RaspiUser] Fetched userData for %s successfully.", username));
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Error while Fetching user Data for %s in %s", username, table), e);
        }
    }

    public void write() {
        try (Connection connection = databaseManager.getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(String.format("INSERT INTO %s(uuid, username, first_doc) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE username = VALUES(username)", table))) {
            statement.setString(1, uuid);
            statement.setString(2, username);
            statement.setLong(3, System.currentTimeMillis());
            statement.executeUpdate();
            plugin.getDebugger().info(String.format("Created Data for %s", username));
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Error while Writing data for %s in %s", username, table), e);
        }

        if (databaseManager.getFallBackManager().containOld(UUID.fromString(uuid))) {
            databaseManager.getFallBackManager().perform(this);
            if (allowed_since != null) {
                state = true;
            }
            updateUserData();
        }
    }

    public void updateUserData() {
        try (Connection connection = databaseManager.getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(String.format("UPDATE %s SET first_doc = ?, denied_by = ?, deny_reason = ?, allowed_since = ?, allowed_by = ?, request_state = ?, last_seen = ?, online_hours = ?, color = ?, prefix = ?, voting = ?  WHERE uuid = ?", table))) {
            statement.setObject(1, first_join, JDBCType.BIGINT);
            statement.setObject(2, denied_by, JDBCType.VARCHAR);
            statement.setObject(3, deny_reason, JDBCType.VARCHAR);
            statement.setObject(4, allowed_since, JDBCType.VARCHAR);
            statement.setObject(5, allowed_by, JDBCType.VARCHAR);
            statement.setObject(6, state, JDBCType.BOOLEAN);
            statement.setObject(7, lastSeen, JDBCType.BIGINT);
            statement.setObject(8, onlineHours, JDBCType.INTEGER);
            statement.setObject(9, color, JDBCType.VARCHAR);
            statement.setObject(10, prefix, JDBCType.VARCHAR);
            statement.setObject(11, voting, JDBCType.INTEGER);
            statement.setObject(12, uuid, JDBCType.VARCHAR);
            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Error while Updating data for %s in %s", username, table), e);
        }
    }


}
