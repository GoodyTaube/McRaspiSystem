package eu.goodyfx.system.randomlootchest.utils;

import eu.goodyfx.system.randomlootchest.RandomLootChest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Random;

public class FindAvLocations {

    private final Random random;
    private RandomLootChest system;
    private final FileConfiguration config;

    private int biggestX = 0;
    private int smallestX = 0;
    private int biggestZ = 0;
    private int smallestZ = 0;
    private int biggestY = 0;
    private int smallestY = 0;


    public FindAvLocations(RandomLootChest subSystem) {
        this.random = subSystem.getPlugin().getRandom();
        this.config = subSystem.getConfigManager().getConfig();
        init();
    }

    public void init() {
        biggestX = config.getInt("LargestDinctance_X");
        smallestX = config.getInt("SmallestDinctance_X");
        biggestZ = config.getInt("LargestDinctance_Z");
        smallestZ = config.getInt("SmallestDinctance_Z");
        biggestY = config.getInt("LargestDinctance_Y");
        smallestY = config.getInt("SmallestDinctance_Y");
    }

    public int getRandom(int no1, int no2) {
        int max;
        int min;
        if (no1 > no2) {
            max = no1;
            min = no2;
        } else {
            max = no2;
            min = no1;
        }


        return this.random.nextInt(max - min + 1) + min;

    }

    public Location findLocation() {
        Location loc = null;
        int counter = 0;
        boolean found = false;

        while (!found) {
            ++counter;
            if (counter > 10) {
                break;
            }

            String worldName = config.getString("World");
            if (worldName == null) {
                return null;
            }
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                return null;
            }

            int randomX = getRandom(smallestX, biggestX);
            int randomZ = getRandom(smallestZ, biggestZ);
            int randomY = world.getHighestBlockYAt(randomX, randomZ);
            if (randomY >= smallestY && randomY <= biggestY) {
                found = true;
            }

            loc = new Location(world, (double) randomX, (double) randomY, (double) randomZ);
            if (!loc.getChunk().isLoaded()) {
                loc.getChunk().load();
            }
        }

        return loc;
    }

}
