package eu.goodyfx.mcraspisystem.managers;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

public class LootManager {

    private final McRaspiSystem plugin;

    public LootManager(McRaspiSystem plugin) {
        this.plugin = plugin;
    }

    private final File file = new File("plugins/" + McRaspiSystem.class.getSimpleName() + "/", "locations.yml");
    private FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    private final Map<Location, Location> currentWarps = new HashMap<>();

    private final List<Location> nonWarp = new ArrayList<>();


    private String pathBuilder(Location location) {
        String raw = location.getWorld().getName() + location.getX() + location.getY() + location.getZ();
        raw = raw.replaceAll("\\.", "");
        return "warps." + raw;
    }

    public void setWarp(Player creator, Location locationStart, Location locationWarp) {
        //Save both states to get both direction Support
        config.set(pathBuilder(locationStart) + ".creator", creator.getUniqueId().toString());
        config.set(pathBuilder(locationStart) + ".timeStamp", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        config.set(pathBuilder(locationStart) + ".active", true);
        config.set(pathBuilder(locationStart) + ".world", locationWarp.getWorld().getName());
        config.set(pathBuilder(locationStart) + ".X", locationWarp.getX());
        config.set(pathBuilder(locationStart) + ".Y", locationWarp.getY());
        config.set(pathBuilder(locationStart) + ".Z", locationWarp.getZ());
        //Second Position Save
        config.set(pathBuilder(locationWarp) + ".creator", creator.getUniqueId().toString());
        config.set(pathBuilder(locationWarp) + ".timeStamp", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        config.set(pathBuilder(locationWarp) + ".active", true);
        config.set(pathBuilder(locationWarp) + ".world", locationStart.getWorld().getName());
        config.set(pathBuilder(locationWarp) + ".X", locationStart.getX());
        config.set(pathBuilder(locationWarp) + ".Y", locationStart.getY());
        config.set(pathBuilder(locationWarp) + ".Z", locationStart.getZ());
        save();
        currentWarps.put(locationStart, locationWarp);
        currentWarps.put(locationWarp, locationStart);
        plugin.getLogger().info("Neuer Warp punkt erstellt! :: " + creator.getName() + " :: Loc1." + pathBuilder(locationStart) + " :: Loc2." + pathBuilder(locationWarp));
        nonWarp.remove(locationWarp);
        nonWarp.remove(locationStart);
    }

    public void disable(Location location) {
        Location swapWarp = get(location);
        config.set(pathBuilder(location) + ".active", false);
        //config.set(pathBuilder(swapWarp) + ".active", false);
        save();
        nonWarp.add(location);
        //nonWarp.add(swapWarp);
    }


    public Integer size() {
        this.config = YamlConfiguration.loadConfiguration(new File("plugins/" + McRaspiSystem.class.getSimpleName() + "/", "locations.yml"));
        return Objects.requireNonNull(config.getConfigurationSection("warps")).getKeys(false).size() / 2;
    }

    public boolean existAndActive(Location location1, Location location2) {
        if (warpExist(location1) && warpExist(location2)) {
            return config.getBoolean(pathBuilder(location1) + ".active") && config.getBoolean(pathBuilder(location2) + ".active");
        }
        return false;
    }

    public Location get(Location location) throws NullPointerException {
        if (currentWarps.containsKey(location)) {  //Check if current warps saved the asked Warp *Runtime Save*
            return currentWarps.get(location);
        }
        String worldString = config.getString(pathBuilder(location) + ".world");
        World world;
        double locationX = config.getDouble(pathBuilder(location) + ".X");
        double locationY = config.getDouble(pathBuilder(location) + ".Y");
        double locationZ = config.getDouble(pathBuilder(location) + ".Z");
        if (worldString != null) { //Build new Location by Database input; Save Locations into current
            world = Bukkit.getWorld(worldString);
            Location warpLocation = new Location(world, locationX, locationY, locationZ);
            currentWarps.put(location, warpLocation);
            return warpLocation;
        }
        return null;
    }

    public boolean warpExist(Location location) {
        if (nonWarp.contains(location)) {
            return false;
        }
        if (!file.exists() || !config.contains("warps")) {
            return false;
        }
        if (currentWarps.containsKey(location)) {
            return true;
        }
        if (config.contains(pathBuilder(location))) {
            return true;
        } else {
            nonWarp.add(location);
            return false;
        }
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while Saving File", e);
        }
    }
}
