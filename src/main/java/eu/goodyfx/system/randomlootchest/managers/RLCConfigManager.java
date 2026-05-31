package eu.goodyfx.system.randomlootchest.managers;

import eu.goodyfx.system.randomlootchest.RandomLootChest;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class RLCConfigManager {

    private final File file;
    @Getter
    private FileConfiguration config;


    public RLCConfigManager(RandomLootChest subSystem) {
        this.file = new File(subSystem.getPlugin().getDataFolder(), "rlc/config.yml");
        if (!this.file.exists()) {
            file.mkdirs();
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }


}
