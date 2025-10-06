package eu.goodyfx.system.core.database;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.MojangPlayerWrapper;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

@Getter
@Setter
public class RaspiUsernames {

    private final String uuid;
    private String userName;
    private List<String> usernames = new ArrayList<>();
    private DataSource dataSource;
    private McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    public RaspiUsernames(UUID uuid) {
        this.uuid = uuid.toString();
        String userName = MojangPlayerWrapper.getName(uuid);
        if (userName == null) {
            return;
        }
        this.userName = userName;
        this.dataSource = plugin.getDatabaseManager().dataSource;

        fetch();

        if (!usernames.contains(userName)) {
            update();
            usernames.add(userName);
        }
    }


    public void update() {
        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("INSERT IGNORE INTO username_history(uuid, username) VALUES(?, ?);")) {
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, userName);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while write usernames to " + userName, e);
        }
    }

    private void fetch() {
        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("SELECT username, aufgenommen_am FROM username_history WHERE uuid = ? ORDER BY aufgenommen_am ASC;")) {
            preparedStatement.setString(1, uuid);
            ResultSet set = preparedStatement.executeQuery();
            while (set.next()) {
                usernames.add(set.getString("username"));
            }
            plugin.getDebugger().info(String.format("[UserNames] Fetched userNames for %s successfully.", userName));
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[UserName] Failed to Fetch userNames for " + userName, e);
        }
    }

}
