package eu.goodyfx.mcraspisystem.managers;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.exceptions.AllReadyExistException;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Getter
@SuppressWarnings("unused")
public class TraderDB {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    private static final String DATABASE_PATH = "traderDB.yml";
    private static final String DB_START = "trader.";
    private static final String DB_TRADER_SPECIAL_KEY = "trader.%s.special";
    private static final String DB_TRADER_NAME = "trader.%s.name";
    private static final String DB_TRADER_SHOP = "trader.%s.shop";
    public static final String DB_SHOP_ITEM_1 = "trader.%s.shop.%s.buy1";
    public static final String DB_SHOP_ITEM_2 = "trader.%s.shop.%s.buy2";
    public static final String DB_SHOP_RES = "trader.%s.shop.%s.result";


    private final File file = new File(plugin.getDataFolder(), DATABASE_PATH);
    private FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

    public TraderDB() {
        File file1 = new File(plugin.getDataFolder(), DATABASE_PATH);
        if (!file1.exists()) {
            plugin.saveResource(DATABASE_PATH, false);
        }
    }

    public void reload() {
        configuration = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), DATABASE_PATH));
    }

    public void create(String name) throws AllReadyExistException {
        String display = name;
        name = name.toLowerCase();
        if (!configuration.contains(DB_START) || !configuration.contains(DB_START + name.toLowerCase())) {
            configuration.set(getDatabasePath(DB_TRADER_NAME, name), display);
            configuration.set(getDatabasePath(DB_TRADER_SPECIAL_KEY, name), generateKey());
            save();

        } else {
            throw new AllReadyExistException(String.format("Trader: %s existiert bereits.", name));
        }
    }

    public void addItemToShop(String traderName, ItemStack result, ItemStack... buyItems) {
        int id = 0;
        if (configuration.contains(getDatabasePath(DB_TRADER_SHOP, traderName))) {
            id = Objects.requireNonNull(configuration.getConfigurationSection(getDatabasePath(DB_TRADER_SHOP, traderName))).getKeys(false).size();
        }
        configuration.set(String.format(DB_SHOP_RES, traderName, id), result);
        configuration.set(String.format(DB_SHOP_ITEM_1, traderName, id), buyItems[0]);
        if (buyItems.length == 2) {
            configuration.set(String.format(DB_SHOP_ITEM_2, traderName, id), buyItems[1]);
        }
        save();
    }

    public void setItemToShop(String traderName, Integer id, ItemStack result, ItemStack... buyItems) {
        configuration.set(String.format(DB_SHOP_RES, traderName, id), result);
        configuration.set(String.format(DB_SHOP_ITEM_1, traderName, id), buyItems[0]);
        if (buyItems.length == 2) {
            configuration.set(String.format(DB_SHOP_ITEM_2, traderName, id), buyItems[1]);
        }
        save();
    }

    public ItemStack getItemStack(String traderName, String path, int id) {
        return configuration.getItemStack(String.format(path, traderName, id));
    }

    public List<String> getShopIds(String trader) {
        if (configuration.contains(getDatabasePath(DB_TRADER_SHOP, trader))) {
            return Objects.requireNonNull(configuration.getConfigurationSection(getDatabasePath(DB_TRADER_SHOP, trader))).getKeys(false).stream().toList();
        }
        return List.of();
    }


    public void remove(String traderName) {
        traderName = traderName.toLowerCase();
        if (configuration.contains(DB_START) && configuration.contains(DB_START + traderName)) {
            configuration.set(getDatabasePath(DB_TRADER_NAME, traderName), null);
            configuration.set(getDatabasePath(DB_TRADER_SPECIAL_KEY, traderName), null);
            configuration.set(DB_START + traderName, null);
            save();
        }
    }

    public String getUUID(String name) {
        return configuration.getString(getDatabasePath(DB_TRADER_SPECIAL_KEY, name.toLowerCase()));
    }

    public String getTraderName(String name) {
        return configuration.getString(getDatabasePath(DB_TRADER_NAME, name.toLowerCase()));
    }


    public String getDatabasePath(String rawPath, String name) {
        return String.format(rawPath, name);
    }

    private String generateKey() {
        return String.format("Trader%s", getTraders().size() + 1);
    }

    public List<String> getTraders() {
        if (configuration.getConfigurationSection(DB_START) != null) {
            return Objects.requireNonNull(configuration.getConfigurationSection(DB_START)).getKeys(false).stream().toList();
        }
        return List.of();
    }

    public String getTraderByID(String traderUID) {
        for (String trader : getTraders()) {
            if (Objects.requireNonNull(configuration.getString(getDatabasePath(DB_TRADER_SPECIAL_KEY, trader))).equalsIgnoreCase(traderUID)) {
                return trader;
            }
        }
        return "";
    }

    public boolean traderExist(String name) {
        return configuration.contains(DB_START + name.toLowerCase());
    }

    public void addRecipe(String traderName, ItemStack result, ItemStack... buy) {
        traderName = traderName.toLowerCase();
        configuration.set(getDatabasePath(DB_TRADER_SHOP, traderName), "");
    }

    public boolean shopExist(String trader) {
        return configuration.contains(getDatabasePath(DB_TRADER_SHOP, trader));
    }


    public boolean shopItemExist(String trader, String id, String path) {
        return configuration.contains(String.format(path, trader, id));
    }

    public void removeRecipe(String trader, Integer id) {
        configuration.set(String.format(DB_SHOP_ITEM_1, trader, id), null);
        configuration.set(String.format(DB_SHOP_ITEM_2, trader, id), null);
        configuration.set(String.format(DB_SHOP_RES, trader, id), null);
        configuration.set(String.format(DB_TRADER_SHOP + ".%s", trader, id), null);
        save();
    }


    private void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            plugin.getLogger().info("Failed to Save TRADER DB");
        }
    }


}


