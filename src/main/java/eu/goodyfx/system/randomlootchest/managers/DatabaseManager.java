package eu.goodyfx.system.randomlootchest.managers;

import eu.goodyfx.system.randomlootchest.RandomLootChest;
import eu.goodyfx.system.randomlootchest.tasks.SpawnTimerTask;
import eu.goodyfx.system.randomlootchest.utils.GeneratedChest;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
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

    public DatabaseManager(RandomLootChest subSystem) {
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

    public void saveChests() {
        ConfigurationSection section = config.getConfigurationSection("Chests");
        if (section == null) {
            return;
        }
        int counter = 0;
        for (GeneratedChest chest : SpawnTimerTask.getChests()) {
            Location loc = (Location) chest.getLocation();
            assert loc != null;
            config.createSection("Chest" + counter);
            section.getConfigurationSection("Chest" + counter).set("World", loc.getWorld().getName());
            section.getConfigurationSection("Chest" + counter).set("X", loc.getBlockX());
            section.getConfigurationSection("Chest" + counter).set("Y", loc.getBlockY());
            section.getConfigurationSection("Chest" + counter).set("Z", loc.getBlockZ());
            section.getConfigurationSection("Chest" + counter).set("TimeToDelete", chest.getKillTime());
            save();
            ++counter;
        }

        save();
    }


    public void loadChest() {
        ConfigurationSection section = config.getConfigurationSection("Chests");
        if (section == null) {
            return;
        }
        for (String s : section.getKeys(true)) {
            if (s != null && !s.contains(".")) {
                World world = Bukkit.getWorld(section.getConfigurationSection(s).getString("World"));
                int x = section.getConfigurationSection(s).getInt("X");
                int y = section.getConfigurationSection(s).getInt("Y");
                int z = section.getConfigurationSection(s).getInt("Z");
                Location loc = new Location(world, (double) x, (double) y, (double) z);
                long currentTime = section.getConfigurationSection(s).getLong("TimeToDelete");
                SpawnTimerTask.getLoaded_Chest().put(loc, currentTime);
                section.set(s, (Object) null);
            }
        }

        save();
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
