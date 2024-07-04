package eu.goodyfx.mcraspisystem.managers;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.exceptions.ValueNotFoundException;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import eu.goodyfx.mcraspisystem.utils.ReiseDisplayBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class ReiseLocationManager {

    private static final File file = new File("plugins/Reise/", "locations.yml");
    private static FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    private static final String PATH = "reise.";

    private static final String USER = ".user";

    private static final String WORLD = ".World";
    private static final String X = ".X";
    private static final String Y = ".Y";
    private static final String Z = ".Z";
    private static final String YAW = ".Yaw";
    private static final String PITCH = ".Pitch";


    public static void set(int id, Location location) {
        config.set(PATH + id + USER, "FREE");
        config.set(PATH + id + WORLD, location.getWorld().getName());
        config.set(PATH + id + X, location.getX());
        config.set(PATH + id + Y, location.getY());
        config.set(PATH + id + Z, location.getZ());
        config.set(PATH + id + YAW, location.getYaw());
        config.set(PATH + id + PITCH, location.getPitch());
        save();
    }

    public static void bind(int rawID, String name) {
        String id = String.valueOf(rawID);
        config.set(PATH + id + USER, name);
        save();
    }

    public static void reset(int rawID) {
        String id = String.valueOf(rawID);
        config.set(PATH + id + USER, "FREE");
        config.set(PATH + id + WORLD, null);
        config.set(PATH + id + X, null);
        config.set(PATH + id + Y, null);
        config.set(PATH + id + Z, null);
        config.set(PATH + id + YAW, null);
        config.set(PATH + id + PITCH, null);
        save();
    }

    public static void remove(int id) {
        config.set(PATH + id + USER, null);
        config.set(PATH + id + WORLD, null);
        config.set(PATH + id + X, null);
        config.set(PATH + id + Y, null);
        config.set(PATH + id + Z, null);
        config.set(PATH + id + YAW, null);
        config.set(PATH + id + PITCH, null);
        config.set(PATH + id, null);
        save();
    }


    public static Location get(int rawID) {
        String id = String.valueOf(rawID);
        World world = Bukkit.getWorld(Objects.requireNonNull(config.getString(PATH + id + WORLD)));
        double x = config.getDouble(PATH + id + X);
        double y = config.getDouble(PATH + id + Y);
        double z = config.getDouble(PATH + id + Z);
        Location location = new Location(world, x, y, z);
        location.setYaw(config.getInt(PATH + id + YAW));
        location.setPitch(config.getInt(PATH + id + PITCH));
        return location;
    }

    /**
     * This Method get used whenever you only have the User and want the ID
     *
     * @param name The Target UserName
     * @return The ID of That User iF EXIST ELse Return
     * @throws ValueNotFoundException if name was not Found
     */
    public static int getIDByName(String name) throws ValueNotFoundException {
        for (String key : getKeys()) {
            if (config.contains(PATH + key) && Objects.requireNonNull(config.getString(PATH + key + USER)).equalsIgnoreCase(name)) {
                return Integer.parseInt(key);
            }
        }
        throw new ValueNotFoundException("Es wurde kein Wert mit dem Namen: " + name + " gefunden.");
    }

    public static boolean exist(int rawID) {

        if (!file.exists()) {
            return false;
        }
        reload();
        return config.contains(PATH + rawID);
    }

    public static boolean hasEntry(int rawID, String name) {
        if (!exist(rawID)) {
            return exist(rawID);
        }

        return config.getString(PATH + rawID + USER).equalsIgnoreCase(name);
    }

    public static boolean checkUser(RaspiPlayer player, String name, McRaspiSystem reise) {
        for (String key : getKeys()) {
            if (config.getString(PATH + key + USER).equalsIgnoreCase(name)) {
                player.sendMessage("<green>McRaspi <gray>| Der Teleport wurde <green><underlined>erfolgreich</underlined><gray> markiert.");
                new ReiseDisplayBuilder(reise, get(Integer.parseInt(key))).buildBlockDisplay();
                return true;
            }
        }
        player.sendMessage("Der Spieler ist noch nicht Hinterlegt!");
        return false;
    }

    public static boolean searchUser(RaspiPlayer player, String name) {
        for (String key : getKeys()) {
            if (config.getString("reise." + key + USER).equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error while Save", e);
        }
    }

    private static void reload() {
        config = YamlConfiguration.loadConfiguration(file);

    }

    public static Map<Integer, String> getPOS() {
        reload();
        Map<Integer, String> sets = new HashMap<>();
        for (String key : getKeys()) {
            sets.put(Integer.parseInt(key), String.format("<gray>ID:<aqua>'%s' <gray>:: <aqua>%s ", key, config.get(PATH + key + USER)));
        }
        return sets;
    }

    private static Set<String> getKeys() {
        return Objects.requireNonNull(config.getConfigurationSection("reise")).getKeys(false);
    }

    public static String getIDSArray() {
        StringBuilder builder = new StringBuilder("ID's:");
        for (String key : getKeys()) {
            builder.append(key).append(",");
        }
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }

    public static List<String> getAllUsers() {
        reload();
        List<String> names = new ArrayList<>();
        for (String key : getKeys()) {
            String name = config.getString(PATH + key + USER);
            if (!names.contains(name) && !name.equalsIgnoreCase("FREE")) {
                names.add(name);
            }
        }
        return names;
    }

}
