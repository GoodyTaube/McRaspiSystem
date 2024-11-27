package eu.goodyfx.mcraspisystem.managers;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.utils.ItemBuilder;
import eu.goodyfx.mcraspisystem.utils.LootChestItemParser;
import eu.goodyfx.mcraspisystem.utils.LootChestMenuItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;

public class LootChestManager {

    private File file;

    private final McRaspiSystem plugin;


    public LootChestManager(McRaspiSystem plugin) {
        this.file = new File(plugin.getDataFolder(), "lootChest.json");
        this.plugin = plugin;

    }

    private void reload() {
        this.file = new File(plugin.getDataFolder(), "lootChest.json");
    }

    private List<LootChestItemParser> loadData(LootChestMenuItems itemsType) {
        if (!file.exists()) {
            return Collections.emptyList();
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
            return Collections.emptyList();
        }
    }

    private List<Map<String, Object>> loadData2(LootChestMenuItems itemsType) {
        if (!file.exists()) {
            return Collections.emptyList();
        }
        reload();
        List<LootChestItemParser> finalItems = new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> jsonData = gson.fromJson(reader, type);
            List<Map<String, Object>> seltenList = jsonData.get(itemsType.getDatabaseName()) != null ? (List<Map<String, Object>>) jsonData.get(itemsType.getDatabaseName()) : null;
            return Objects.requireNonNullElse(seltenList, Collections.emptyList());
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while loading data", e);
            return Collections.emptyList();
        }
    }

    public List<ItemStack> getItems(LootChestMenuItems itemsType) {
        List<LootChestItemParser> items = loadData(itemsType);
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemStack> itemsToAdd = new ArrayList<>();
        for (LootChestItemParser item : items) {
            itemsToAdd.add(buildItem(item));
        }
        return itemsToAdd;
    }

    private ItemStack buildItem(LootChestItemParser itemData) {
        ItemBuilder builder = new ItemBuilder(itemData.getType());
        if (itemData.getModelID() != 0) {
            builder.setModelID(itemData.getModelID());
        }
        if (itemData.getItemDisplay() != null) {
            builder.displayName(itemData.getItemDisplay());
        }
        if (itemData.getLore() != null) {
            builder.lore(itemData.getLore());
        }
        return builder.build();
    }

    public void prepareWriting(List<ItemStack> content, LootChestMenuItems saveCat) {
        List<Map<String, Object>> selten = loadData2(LootChestMenuItems.SELTEN);
        List<Map<String, Object>> nichtSoSelten = loadData2(LootChestMenuItems.NICHT_SO_SELTEN);
        List<Map<String, Object>> immer = loadData2(LootChestMenuItems.IMMER);

        List<Map<String, Object>> itemsMap = new ArrayList<>();
        for (ItemStack perStack : content) {
            itemsMap.add(writeItemStack(perStack));
        }

        switch (saveCat) {
            case IMMER:
                immer = itemsMap;
                break;
            case NICHT_SO_SELTEN:
                nichtSoSelten = itemsMap;
                break;
            case SELTEN:
                selten = itemsMap;
                break;
        }

        Map<String, Object> jsonData = new HashMap<>();
        jsonData.put("selten", selten);
        jsonData.put("nichtSoSelten", nichtSoSelten);
        jsonData.put("immer", immer);
        writeToJson(file, jsonData);
    }

    private Map<String, Object> writeItemStack(ItemStack item) {
        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("type", item.getType().name());

        ItemMeta meta = item.getItemMeta();
        dataMap.put("modelID", meta != null && meta.hasCustomModelData() ? meta.getCustomModelData() : null);

        dataMap.put("itemDisplay", meta != null && meta.hasDisplayName() ? GsonComponentSerializer.gson().serialize(meta.displayName()) : null);

        dataMap.put("lore", meta != null && meta.hasLore() ? compactLore(meta.lore()) : null);
        Map<String, Object> itemsMap = new HashMap<>();
        itemsMap.put("items", List.of(dataMap));

        return itemsMap;
    }

    private String compactLore(List<Component> lore) {
        StringBuilder builder = new StringBuilder();
        for (Component loreLine : lore) {
            builder.append(GsonComponentSerializer.gson().serialize(loreLine)).append("@");
        }
        if (builder.length() > 1) {
            builder.setLength(builder.length() - 1);
        }
        return builder.toString();
    }

    private void writeToJson(File file, Map<String, Object> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(data, writer);
            plugin.getLogger().info("Data wurde gespeichert.");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load", e);
        }
    }

}
