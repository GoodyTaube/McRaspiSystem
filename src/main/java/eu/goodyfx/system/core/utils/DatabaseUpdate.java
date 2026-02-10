package eu.goodyfx.system.core.utils;

import eu.goodyfx.system.McRaspiSystem;
import org.bukkit.Bukkit;

import javax.sql.DataSource;
import java.sql.*;
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
        try (Connection connection = dataSource.getConnection()) {

            DatabaseMetaData meta = connection.getMetaData();

            try (ResultSet rs = meta.getColumns(null, null, "user_data", "coins")) {
                if (!rs.next()) {

                    try (Statement statement = connection.createStatement()) {
                        statement.executeUpdate(
                                "ALTER TABLE user_data " +
                                        "ADD COLUMN coins BIGINT NOT NULL DEFAULT 10"
                        );
                    }

                    plugin.getLogger().info("Spalte 'coins' wurde zur Tabelle user_data hinzugefügt.");
                }
            }

        } catch (SQLException exception) {
            plugin.getLogger().log(Level.SEVERE,
                    "Error while updating user_data (coins column)", exception);
        }
    }

}
