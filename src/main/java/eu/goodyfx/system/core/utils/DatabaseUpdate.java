package eu.goodyfx.system.core.utils;

import eu.goodyfx.system.McRaspiSystem;
import org.bukkit.Bukkit;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public class DatabaseUpdate {

    private final DataSource dataSource;

    public DatabaseUpdate(McRaspiSystem plugin) {
        this.dataSource = plugin.getDatabaseManager().getDataSource();
        upgradeExecutor(plugin);
    }

    private void upgradeExecutor(McRaspiSystem plugin) {
        Bukkit.getAsyncScheduler().runNow(plugin, scheduledTask -> {
            coins(plugin);
            plugin.getLogger().info("'coins' in user_data hinzugefügt.");
        });
    }

    private void coins(McRaspiSystem plugin) {
        String execute = "ALTER TABLE user_data ADD COLUMN IF NOT EXISTS coins BIGINT NOT NULL DEFAULT 10";
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(execute)) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.SEVERE, "Error while Update Coins in user_data", exception);
        }
    }

}
