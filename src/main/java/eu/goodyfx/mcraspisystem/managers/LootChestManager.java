package eu.goodyfx.mcraspisystem.managers;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import org.bukkit.Location;

import java.util.Objects;

public class LootChestManager {

    private UtilityFileManager config;

    public LootChestManager(McRaspiSystem system) {
        this.config = new UtilityFileManager(system, "lootChest.yml");
    }

    private final String CONFIG_PATH = "Chests";

    public void set(Location location) {
        config.setLocation(CONFIG_PATH, "chest-" + getChestNumber(), location);
    }

    /**
     * Retrieves the next available chest number.
     *
     * This method checks if the loot chest configuration exists. If it does, it calculates
     * the next chest number by counting the existing keys within a specific configuration
     * section and adds one to this count. If the configuration does not exist, it returns 0.
     *
     * @return the next available chest number if the configuration exists, otherwise 0.
     */
    public Integer getChestNumber() {
        if (Boolean.TRUE.equals(config.exist())) {
            return Objects.requireNonNull(config.getConfig().getConfigurationSection(CONFIG_PATH)).getKeys(false).size() + 1;
        } else {
            return 0;
        }
    }

    public void remove(Location location) {

    }

    public void openGUI() {

    }


}
