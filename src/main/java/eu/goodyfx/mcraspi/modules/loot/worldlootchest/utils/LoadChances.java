package eu.goodyfx.mcraspi.modules.loot.worldlootchest.utils;

import eu.goodyfx.mcraspi.modules.loot.worldlootchest.RandomLootChest;
import eu.goodyfx.mcraspi.modules.loot.worldlootchest.managers.DatabaseManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class LoadChances {

    private final RandomLootChest system;
    private final DatabaseManager databaseManager;

    public LoadChances(RandomLootChest randomLootChest) {
        this.system = randomLootChest;
        this.databaseManager = randomLootChest.getDatabaseManager();
    }


    private ConfigurationSection itemDB() {
        return databaseManager.getConfig().getConfigurationSection("ItemDatabase");
    }

    public void save() {
        databaseManager.save();
    }

    public void loadItems() {
        this.clear();

        for (int i = 0; i < 100000 && this.itemDB().isConfigurationSection(String.valueOf(i)); ++i) {
            ItemStack item = Objects.requireNonNull(this.itemDB().getConfigurationSection(String.valueOf(i))).getItemStack("item");
            int chance = Objects.requireNonNull(this.itemDB().getConfigurationSection(String.valueOf(i))).getInt("chance");

            for (int j = 0; j < chance; ++j) {
                for (int k = 0; k < 32000; ++k) {
                    if (!system.getItems().containsKey(k)) {
                        system.getItems().put(k, item);
                        break;
                    }
                }
            }
        }

    }

    public void clear() {
        system.getItems().clear();
    }

}
