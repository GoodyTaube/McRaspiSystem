package eu.goodyfx.system.randomlootchest.utils;

import eu.goodyfx.system.randomlootchest.RandomLootChest;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class FindAvLocations {

    private final Random random;
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
        int max = Math.max(no1, no2);
        int min = Math.min(no1, no2);
        return this.random.nextInt(max - min + 1) + min;
    }

    /**
     * Startet die asynchrone Suche. Gibt ein CompletableFuture mit der fertigen Location zurück.
     */
    public CompletableFuture<Location> findLocationAsync() {
        CompletableFuture<Location> resultFuture = new CompletableFuture<>();

        String worldName = config.getString("World");
        if (worldName == null) {
            resultFuture.complete(null);
            return resultFuture;
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            resultFuture.complete(null);
            return resultFuture;
        }

        // 1. Koordinaten auswürfeln
        int randomX = getRandom(smallestX, biggestX);
        int randomZ = getRandom(smallestZ, biggestZ);

        // 2. Paper den Chunk asynchron laden lassen
        CompletableFuture<Chunk> chunkFuture = world.getChunkAtAsync(randomX >> 4, randomZ >> 4);

        // 3. Sobald der Chunk geladen ist, die beste Y-Höhe auf Gültigkeit prüfen
        chunkFuture.thenAccept(chunk -> {
            // WICHTIG: Die Validierung muss zurück auf den Hauptthread, da wir Blöcke abfragen!
            Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugins()[0], () -> {

                // Wir testen bis zu 20 Y-Höhen in diesem geladenen Chunk
                for (int i = 0; i < 20; i++) {
                    int randomY = getRandom(smallestY, biggestY);
                    Location checkLoc = new Location(world, randomX, randomY, randomZ);

                    if (isValidSpawnLocation(checkLoc)) {
                        resultFuture.complete(checkLoc); // Erfolg! Location gefunden
                        return;
                    }
                }
                resultFuture.complete(null); // Keine gültige Höhe in diesem Chunk gefunden
            });
        }).exceptionally(ex -> {
            resultFuture.complete(null);
            return null;
        });

        return resultFuture;
    }

    private boolean isValidSpawnLocation(Location loc) {
        Block target = loc.getBlock();
        if (target.getType() == Material.AIR || target.getType() == Material.CAVE_AIR) {
            Block ground = loc.clone().add(0, -1, 0).getBlock();
            if (isValidGround(ground.getType())) {
                Block above = loc.clone().add(0, 1, 0).getBlock();
                return above.getType() == Material.AIR || above.getType() == Material.CAVE_AIR;
            }
        }
        return false;
    }

    private boolean isValidGround(Material material) {
        return material.isSolid()
                && material != Material.WATER
                && material != Material.LAVA
                && material != Material.BARRIER
                && material != Material.BEDROCK;
    }
}
