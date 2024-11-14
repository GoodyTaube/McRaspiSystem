package eu.goodyfx.mcraspisystem.managers;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * UtilityFileManager manages configuration files in a McRaspiSystem environment.
 * It loads, saves, and modifies YAML configuration files.
 */
public class UtilityFileManager {

    private final String fileName;
    private final File file;
    private FileConfiguration config;
    private final McRaspiSystem system;

    public UtilityFileManager(McRaspiSystem system, @NotNull String fileName) {
        if (!fileName.contains(".yml")) {
            fileName = String.format("%s.yml", fileName);
        }
        this.fileName = fileName;
        this.file = new File(system.getDataFolder().getAbsolutePath(), fileName);
        this.config = YamlConfiguration.loadConfiguration(file);
        this.system = system;
        if (Boolean.FALSE.equals(exist()) && system.getResource(fileName) != null) {
            system.saveResource(fileName, false);
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Sets a value in the configuration at the specified path.
     *
     * @param path The path within the configuration file where the value should be set.
     * @param val  The value to be set at the specified path.
     */
    public void set(String path, Object val) {
        config.set(path, val);
        save();
    }


    /**
     * Sets the location values in the configuration file.
     *
     * @param path The base path in the configuration file where the location details should be stored.
     * @param locationName The name associated with the location, used as part of the path in the configuration file.
     * @param location The Location object containing the world and coordinate details to be stored.
     */
    public void setLocation(String path, String locationName, Location location){
        set(path + "." + locationName + ".world", location.getWorld().getName());
        set(path + "." + locationName + ".x", location.getX());
        set(path + "." + locationName + ".y", location.getY());
        set(path + "." + locationName + ".z", location.getZ());
        set(path + "." + locationName + ".yaw", location.getYaw());
        set(path + "." + locationName + ".pitch", location.getPitch());
    }


    /**
     * Remove Value by path
     *
     * @param path The value Path
     */
    public void remove(String path) {
        config.set(path, null);
        save();
    }

    /**
     * Remove Value by path
     *
     * @param path The value Path
     * @param deep set this true to remove Value Tree
     */
    public void remove(String path, Boolean deep) {
        if (Boolean.TRUE.equals(deep)) {
            Set<String> content = Objects.requireNonNull(config.getConfigurationSection(path)).getKeys(true);
            content.forEach(cont -> config.set(cont, null));
        }
        config.set(path, null);
        save();
    }

    public <T> T get(String path, Class<T> clazz) {
        return clazz.cast(config.get(path));
    }

    public File file() {
        return this.file;
    }

    public FileConfiguration config() {
        return this.config;
    }

    public Boolean exist() {
        return this.file.exists();
    }

    public Boolean contains(String path) {
        return config.contains(path);
    }

    public void reload() {
        Validate.notNull(this.fileName, "File not Exist!");
        final InputStream stream = system.getResource(this.fileName);
        if (stream == null) {
            return;
        }
        this.config = YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    public List<String> getStringList(String path) {
        return this.config.getStringList(path);
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            system.getHookManager().getDiscordIntegration().sendError(this.getClass(), e);
        }
    }
}
