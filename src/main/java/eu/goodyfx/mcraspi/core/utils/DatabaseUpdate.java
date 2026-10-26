package eu.goodyfx.mcraspi.core.utils;

import eu.goodyfx.mcraspi.McRaspiSystem;

import javax.sql.DataSource;

public class DatabaseUpdate {

    private final DataSource dataSource;

    public DatabaseUpdate(McRaspiSystem plugin) {
        this.dataSource = plugin.getDatabaseManager().getDataSource();
        upgradeExecutor(plugin);
    }

    private void upgradeExecutor(McRaspiSystem plugin) {

    }

}
