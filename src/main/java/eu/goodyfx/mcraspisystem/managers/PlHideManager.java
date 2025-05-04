package eu.goodyfx.mcraspisystem.managers;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class PlHideManager {

    private final File file = new File(JavaPlugin.getPlugin(McRaspiSystem.class).getDataFolder(), "hide_config.yml");
    private final FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    private static final String groupString = "groups.%s";
    private static final String groupCommands = "groups.%s.commands";
    private static final String groupTabs = "groups.%s.tabs";
    private static final String groupErben = "groups.%s.erben-aus";

    private boolean valueExist(String val) {
        return configuration.contains(val);
    }

    private boolean filesUpdated() {
        try {
            return FileUtils.contentEquals(file, new File(plugin.getDataFolder(), "hide_config.yml"));
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "error while compare files", e);
        }
        return false;
    }

    private List<String> getCommands(String groups) {
        return configuration.getStringList(String.format(groupCommands, groups));
    }

    private List<String> getTabs(String groups) {
        return configuration.getStringList(String.format(groupTabs, groups));
    }

    private List<String> getErben(String groups) {
        return configuration.getStringList(String.format(groupErben, groups));
    }

    private void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to Save hide_config", e);
        }
    }


}
