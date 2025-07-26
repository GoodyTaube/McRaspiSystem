package eu.goodyfx.mcraspisystem.managers;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class ItemConverterManager {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    private boolean disabled = false;

    public File file = new File(plugin.getDataFolder(), "item-converter.yml");
    public FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    public ItemConverterManager() {
        if (!file.exists()) {
            defaults();
        }
        this.disabled = config.getBoolean("disabled");
        if (disabled) {
            plugin.getLogger().info("Item Converter Disabled.");
        }
    }

    public void set(ItemStack stack) {
        int item = 0;
        if (config.contains("items") && config.getConfigurationSection("items") != null) {
            item = Objects.requireNonNull(config.getConfigurationSection("items")).getKeys(false).size() + 1;
        }
        config.set(String.format("items.%s", item), stack);
        save();
    }

    private List<ItemStack> getItems() {
        List<ItemStack> itemStacks = new ArrayList<>();
        Objects.requireNonNull(config.getConfigurationSection("items")).getKeys(false).forEach(entry -> {
            itemStacks.add(config.getItemStack(String.format("items.%s", entry)));
        });
        return itemStacks;
    }

    private void defaults() {
        config.addDefault("disabled", true);
        config.addDefault("lore", Collections.EMPTY_LIST);
        config.addDefault("display", "");
        config.addDefault("items", Collections.EMPTY_LIST);
        config.options().copyDefaults(true);
        save();
    }


    public void convert(Inventory inventory) {
        if (disabled) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    getItems().forEach(item -> {
                        if (inventory.contains(item.getType())) {
                            for (int i = 0; i < inventory.getSize(); i++) {
                                if (inventory.getItem(i) != null) {
                                    ItemStack stack = inventory.getItem(i);
                                    assert stack != null;
                                    ItemStack copy = stack.clone();
                                    copy.setAmount(1);
                                    if (copy.equals(item)) {
                                        ItemStack to = buildTo(item);
                                        to.setAmount(stack.getAmount());
                                        inventory.setItem(i, to);
                                    }
                                }
                            }
                        }
                    });
                } catch (NullPointerException e) {
                    plugin.getLogger().severe("Item Converter ERROR! Das Item Konnte nicht gefunden werden.");
                    plugin.getLogger().info("Item Converter Konvertierung wird bis NeuStart übersprungen.");
                    disabled = true;
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public ItemStack buildTo(ItemStack from) {
        ItemBuilder builder = new ItemBuilder(from.getType());
        if (!getConvertLoreList().isEmpty()) {
            builder.lore(getConvertedLore());
        }
        builder.displayName(config.getString("display"));
        builder.addEnchantments(from.getEnchantments());
        return builder.build();
    }

    private List<String> getConvertLoreList() {
        if (config.contains("lore")) {
            return config.getStringList("lore");
        }
        return Collections.emptyList();
    }

    private List<Component> getConvertedLore() {
        List<Component> lore = new ArrayList<>();
        getConvertLoreList().forEach(string -> {
            lore.add(MiniMessage.miniMessage().deserialize(string));
        });
        return lore;
    }


    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while Saving item-converter.yml", e);
            plugin.getHookManager().getDiscordIntegration().sendError(this.getClass(), e);
        }
    }

}
