package eu.goodyfx.system.core.database;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.MojangPlayerWrapper;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

@Getter
@Setter
public class RaspiManagement {

    private final String uuid;
    private final String userName;
    private boolean banned = false;
    private String ban_message = null;
    private String ban_owner = null;
    private Long ban_expire = null;
    private boolean muted = false;
    private String mute_message = null;
    private String mute_owner = null;
    protected final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    protected DatabaseManager databaseManager = plugin.getDatabaseManager();
    private final String table = DatabaseTables.USER_MODERATION.getTableName();
    private static final String PERFORM_PUNISHMENT = "PERFORM_PUNISHMENT";
    private static final String REVOKE_PUNISHMENT = "REVOKE_PUNISHMENT";
    private static final String MUTE = "MUTE";
    private static final String BAN = "BAN";
    private static final String REVOKE_MESSAGE = REVOKE_PUNISHMENT + "::%s for %s";
    private static final String PERFORM_MESSAGE = PERFORM_PUNISHMENT + "::%s to %s by %s for %s";

    public RaspiManagement(UUID uuid) {
        this.uuid = uuid.toString();
        this.userName = MojangPlayerWrapper.getName(uuid);
    }

    /**
     * Setting Ban STATE for User in DB
     *
     * @param performer The Ban Performer
     * @param reason    The Ban Reason in DB format "STRING@STRING"
     */
    public void performBan(Player performer, String reason) {
        banned = true;
        ban_owner = performer.getName();
        ban_message = reason;
        ban_expire = null;
        plugin.getDebugger().info(String.format(PERFORM_MESSAGE, BAN, userName, performer.getName(), reason));
    }

    /**
     * Setting Ban STATE for User in DB
     *
     * @param performer The Ban Performer
     * @param reason    The Ban Reason in DB format "STRING@STRING"
     */
    public void performTempBan(Player performer, String reason, Long expire) {
        banned = true;
        ban_owner = performer.getName();
        ban_message = reason;
        ban_expire = expire;
        plugin.getDebugger().info(String.format(PERFORM_MESSAGE, BAN, userName, performer.getName(), reason));
    }

    /**
     * Removing Ban STATE from User in DB
     */
    public void performUnban() {
        banned = false;
        ban_owner = null;
        ban_message = null;
        ban_expire = null;
        plugin.getDebugger().info(String.format(REVOKE_MESSAGE, BAN, userName));
    }

    /**
     * Setting Mute STATE for User in DB
     *
     * @param performer The Mute performer
     * @param reason    The Mute Reason in DB format
     */
    public void performMute(Player performer, String reason) {
        muted = true;
        mute_message = reason;
        mute_owner = performer.getName();
        plugin.getDebugger().info(String.format(PERFORM_MESSAGE, MUTE, userName, performer.getName(), reason));
    }

    public void performUnMute() {
        muted = false;
        mute_owner = null;
        mute_message = null;
        plugin.getDebugger().info(String.format(REVOKE_MESSAGE, MUTE, userName));
    }

    /**
     * Try to write Data in DB
     */
    public void writeUser() {
        try (Connection connection = databaseManager.getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(String.format("INSERT INTO %s(uuid, name) VALUES(?, ?) ON DUPLICATE KEY UPDATE name = VALUES(name)", table))) {
            statement.setString(1, uuid);
            statement.setString(2, userName);
            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Writing error to user_moderation for %s", userName), e);
        }

        if (databaseManager.getFallBackManager().containOld(UUID.fromString(uuid))) {
            databaseManager.getFallBackManager().perform(this);
            updateUserData();
        }


    }

    public void updateUserData() {
        try (Connection connection = databaseManager.getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(String.format("UPDATE %s SET muted = ?, banned = ?, mute_reason = ?, ban_reason = ?, ban_performer = ?, mute_performer = ?, ban_expire = ? WHERE uuid = ?", table))) {
            statement.setBoolean(1, muted);
            statement.setBoolean(2, banned);
            statement.setString(3, mute_message);
            statement.setString(4, ban_message);
            statement.setString(5, ban_owner);
            statement.setString(6, mute_owner);
            statement.setObject(7, ban_expire, Types.BIGINT);
            statement.setString(8, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Error while writing user data for %s", userName), e);
        }
    }

    public void fetchData() {
        try (Connection connection = databaseManager.getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM user_moderation WHERE uuid = ?")) {
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                this.muted = resultSet.getBoolean("muted");
                this.banned = resultSet.getBoolean("banned");
                this.mute_message = resultSet.getString("mute_reason");
                this.ban_message = resultSet.getString("ban_reason");
                this.ban_owner = resultSet.getString("ban_performer");
                this.mute_owner = resultSet.getString("mute_performer");
                this.ban_expire = resultSet.getObject("ban_expire", Long.class);
                plugin.getDebugger().info(String.format("[UserManagement] Fetched userManagement for %s successfully.", userName));
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Error while Fetching user_management for %s", userName), e);
        }
    }


}
