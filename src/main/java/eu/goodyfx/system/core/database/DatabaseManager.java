package eu.goodyfx.system.core.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.Raspi;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

@Getter
public class DatabaseManager {

    protected final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    protected final FileConfiguration config;
    public HikariDataSource dataSource;
    public FallBackManager fallBackManager = new FallBackManager();

    private void setupDataSource() {
        String host = config.getString("raspi.database.host");
        String username = config.getString("raspi.database.username");
        String password = config.getString("raspi.database.password");
        int port = config.getInt("raspi.database.port");
        String database = config.getString("raspi.database.database");

        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC", host, port, database));
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(20);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setMaxLifetime(1800000);
        hikariConfig.setLeakDetectionThreshold(15000);
        dataSource = new HikariDataSource(hikariConfig);
        dBDefaults();

    }

    private final Map<UUID, Boolean> userExistCacheUserData = new ConcurrentHashMap<>();


    public boolean userExistInTable(UUID uuid, DatabaseTables table) {
        if (table.equals(DatabaseTables.USER_DATA)) {
            if (userExistCacheUserData.containsKey(uuid)) {
                return userExistCacheUserData.get(uuid);
            }
        }
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(String.format("SELECT 1 FROM %s WHERE uuid = ? LIMIT 1", table.getTableName()))) {
            Raspi.debugger().info(String.format("[ExistCheck] for %s in %s", uuid, table.name()));
            statement.setString(1, uuid.toString());
            ResultSet re = statement.executeQuery();
            boolean exist = re.next();
            userExistCacheUserData.put(uuid, exist);
            Raspi.debugger().info(String.format("[ExistCheck] %s exist value: %s", uuid, exist));
            return exist;
        } catch (SQLException e) {
            String name = Bukkit.getOfflinePlayer(uuid).getName();
            plugin.getLogger().log(Level.SEVERE, String.format("[ExistCheck] failed to check %s in %s", name != null ? name : uuid, table.getTableName()));
            return false;
        }
    }


    public DatabaseManager() {
        this.config = plugin.getConfig();
        if (!checkConfigForDB()) {
            return;
        }

        setupDataSource();
    }

    private boolean checkConfigForDB() {
        return config.contains("raspi.database");
    }


    private void dBDefaults() {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement(); InputStream stream = plugin.getResource("Defaults.sql"); Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8)) {
            scanner.useDelimiter(";");
            while (scanner.hasNext()) {
                String sql = scanner.next().trim();
                if (!sql.isEmpty()) {
                    statement.execute(sql);
                }
            }
            plugin.getLogger().info("Init RaspiDB DONE.");
        } catch (SQLException | IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while Configure DB", e);
        }
    }

    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.getLogger().info("Bye Bye RaspiDB // DB CLOSED");
        }
    }

    public boolean ping() {
        if (dataSource != null && !dataSource.isClosed()) {
            try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT 1")) {
                statement.executeQuery();
                plugin.getLogger().info("RaspiDB is ALIVE!");
                return true;
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "RaspiDB is DEAD!", e);
                return false;
            }
        } else {
            return false;
        }
    }

}
