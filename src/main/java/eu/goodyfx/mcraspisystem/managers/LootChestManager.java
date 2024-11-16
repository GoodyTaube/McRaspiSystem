package eu.goodyfx.mcraspisystem.managers;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.utils.ItemBuilder;
import eu.goodyfx.mcraspisystem.utils.LootChestItemParser;
import eu.goodyfx.mcraspisystem.utils.LootChestMenuItems;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class LootChestManager {

    private File file;

    public McRaspiSystem plugin;


    public LootChestManager(McRaspiSystem plugin) {
        this.file = new File(plugin.getDataFolder(), "lootChest.json");
        this.plugin = plugin;

    }

    private void reload() {
        this.file = new File(plugin.getDataFolder(), "lootChest.json");
    }

    private List<LootChestItemParser> loadData(LootChestMenuItems itemsType) {
        if (!file.exists()) {
            return null;
        }
        reload();
        List<LootChestItemParser> finalItems = new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> jsonData = gson.fromJson(reader, type);
            List<Map<String, Object>> seltenList = jsonData.get(itemsType.getDatabaseName()) != null ? (List<Map<String, Object>>) jsonData.get(itemsType.getDatabaseName()) : null;
            if (seltenList != null) {
                for (Map<String, Object> seltenEntry : seltenList) {
                    List<Map<String, Object>> items = (List<Map<String, Object>>) seltenEntry.get("items");
                    for (Map<String, Object> item : items) {
                        finalItems.add(new LootChestItemParser(item));
                    }
                }
            }

            return finalItems;
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while loading data", e);
            return null;
        }
    }

    public List<ItemStack> getItems(LootChestMenuItems itemsType) {
        List<LootChestItemParser> items = loadData(itemsType);
        if (items != null) {
            List<ItemStack> itemsToAdd = new ArrayList<>();
            for (LootChestItemParser item : items) {
                itemsToAdd.add(buildItem(item));
            }
            return itemsToAdd;
        }
        return null;
    }

    private ItemStack buildItem(LootChestItemParser itemData) {
        ItemBuilder builder = new ItemBuilder(itemData.getType());
        if (itemData.getModelID() != 0) {
            builder.setModelID(itemData.getModelID());
        }
        if (!itemData.getItemDisplay().equalsIgnoreCase("none")) {
            builder.displayName(MiniMessage.miniMessage().deserialize(itemData.getItemDisplay()));
        }
        return builder.build();
    }


}
