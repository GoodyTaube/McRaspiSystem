package eu.goodyfx.mcraspisystem.managers;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class RaspiGiveManager {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    private final File file = new File(plugin.getDataFolder(), "RaspiGive.yml");
    private FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    public RaspiGiveManager() {
        if (!file.exists()) {
            defaults();
        }
    }

    public void defaults() {
        String name = "beispiel";
        config.addDefault(String.format("items.%s.type", name), "CLOCK");
        config.addDefault(String.format("items.%s.display", name), "<red>Deine MOM!");
        config.addDefault(String.format("items.%s.lore", name), Collections.EMPTY_LIST);
        config.options().copyDefaults(true);
        save();
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "RaspiGive.yml"));
        save();
    }

    private boolean contains() {
        return config.contains("items");
    }

    public void addItem(Player player, String item) {
        Inventory inventory = player.getInventory();
        if (!contains()) {
            return;
        }

        if (inventory.firstEmpty() == -1) {
            player.getLocation().getWorld().dropItem(player.getLocation(), build(item));
            return;
        }
        inventory.addItem(build(item));
    }

    public ItemStack build(String item) {
        try {
            String display = config.getString(String.format("items.%s.display", item));
            Material material = Material.valueOf(config.getString(String.format("items.%s.type", item)));
            List<String> strings = config.getStringList(String.format("items.%s.lore", item));
            List<Component> lore = new ArrayList<>();
            strings.forEach(string -> {
                lore.add(MiniMessage.miniMessage().deserialize(string));
            });
            return new ItemBuilder(material).displayName(display).lore(lore).build();
        } catch (NullPointerException e) {
            plugin.getLogger().log(Level.SEVERE, "ERROR WHILE PARSING ITEM!", e);
            return new ItemStack(Material.AIR);
        }
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save RaspiGive.yml");
        }
    }


}
