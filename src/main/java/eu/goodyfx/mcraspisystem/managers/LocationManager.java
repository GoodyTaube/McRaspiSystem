package eu.goodyfx.mcraspisystem.managers;

import eu.goodyfx.goodysutilities.GoodysUtilities;
import eu.goodyfx.mcraspisystem.McRaspiSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LocationManager {


    private final UtilityFileManager manager;


    public LocationManager(McRaspiSystem system) {
        this.manager = new UtilityFileManager(system, "location");
    }

    public void set(Player player, String name) {
        Location location = player.getLocation();
        Object[] inserts = new Object[6];
        inserts[0] = location.getWorld().getName();
        inserts[1] = location.getX();
        inserts[2] = location.getY();
        inserts[3] = location.getZ();
        inserts[4] = location.getYaw();
        inserts[5] = location.getPitch();
        setLocationValue(name, inserts);
        // Easy Copy out of Config
        manager.set("Locations." + name + ".toCopy", "/tp " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());
    }


    public boolean exist(String name) {
        if (Boolean.TRUE.equals(manager.exist())) {
            return manager.contains(pathBuilder(name, null));
        }
        return false;
    }


    public Location get(String name) {
        String worldName = getWorldName(name);

        if (worldName == null) {
            throw new NullPointerException("World for " + name + " did not exist");
        }
        World world = Bukkit.getWorld(worldName);
        double x = manager.get(pathBuilder(name, "worldX"), Double.class);
        double y = manager.get(pathBuilder(name, "worldY"), Double.class);
        double z = manager.get(pathBuilder(name, "worldZ"), Double.class);
        double pitch = manager.get(pathBuilder(name, "worldPitch"), Double.class);
        double yaw = manager.get(pathBuilder(name, "worldYaw"), Double.class);
        return new Location(world, x, y, z, (float) yaw, (float) pitch);
    }

    private String pathBuilder(@NotNull String name, @Nullable String value) {
        name = name.toLowerCase();
        if (value == null) {
            return String.format("Locations.%1s", name);
        }

        return String.format("Locations.%1s.%2s", name, value);
    }

    private void setLocationValue(String name, @Nullable Object[] locationValue) {
        manager.set(pathBuilder(name, "worldLabel"), locationValue[0]);
        manager.set(pathBuilder(name, "worldX"), locationValue[1]);
        manager.set(pathBuilder(name, "worldY"), locationValue[2]);
        manager.set(pathBuilder(name, "worldZ"), locationValue[3]);
        manager.set(pathBuilder(name, "worldYaw"), locationValue[4]);
        manager.set(pathBuilder(name, "worldPitch"), locationValue[5]);
    }

    public String getWorldName(String name) {
        return manager.get(pathBuilder(name, "worldLabel"), String.class);
    }
}
