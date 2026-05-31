package eu.goodyfx.system.randomlootchest.managers;

import eu.goodyfx.system.randomlootchest.RandomLootChest;
import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {

    @Getter
    private FileConfiguration config;
    private final File file;
    private final Logger logger;
    private final RandomLootChest system;

    public DatabaseManager(RandomLootChest subSystem) {
        this.system = subSystem;
        this.logger = subSystem.getPlugin().getLogger();
        file = new File(subSystem.getPlugin().getDataFolder(), "rlc/database.yml");
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not save database.yml", e);
        }

    }


    public void loadData() {
        try {
            this.config.load(this.file);
        } catch (InvalidConfigurationException | IOException e) {
            logger.log(Level.SEVERE, "Could not load database.yml!", e);
        }

    }

    public void reloadData() {
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }
}
