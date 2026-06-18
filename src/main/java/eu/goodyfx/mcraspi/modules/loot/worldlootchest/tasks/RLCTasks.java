package eu.goodyfx.mcraspi.modules.loot.worldlootchest.tasks;

import eu.goodyfx.mcraspi.modules.loot.worldlootchest.RandomLootChest;
import eu.goodyfx.mcraspi.modules.loot.worldlootchest.utils.RLCChest;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

public class RLCTasks {

    private final RandomLootChest system;
    private final Random random;
    private final FileConfiguration config;
    public boolean cancel = false;

    public RLCTasks(RandomLootChest system) {
        this.system = system;
        this.random = system.getPlugin().getRandom();
        this.config = system.getConfigManager().getConfig();

        startSpawnTimer();
        startEffectsTimer();
    }

    private void startSpawnTimer() {
        int interval = config.getInt("SpawnChestPerTime", 300);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (cancel) { cancel(); return; }
                tryToSpawnChest();
            }
        }.runTaskTimer(system.getPlugin(), 20L * interval, 20L * interval);
    }

    private void tryToSpawnChest() {
        String worldName = config.getString("World");
        if (worldName == null) return;
        World world = Bukkit.getWorld(worldName);
        if (world == null) return;

        int minX = config.getInt("SmallestDinctance_X");
        int maxX = config.getInt("LargestDinctance_X");
        int minZ = config.getInt("SmallestDinctance_Z");
        int maxZ = config.getInt("LargestDinctance_Z");

        int randomX = random.nextInt(Math.max(1, maxX - minX + 1)) + minX;
        int randomZ = random.nextInt(Math.max(1, maxZ - minZ + 1)) + minZ;

        // Paper lädt den Chunk komplett asynchron im Hintergrund
        world.getChunkAtAsync(randomX >> 4, randomZ >> 4).thenAccept(chunk -> {
            // Sobald geladen, wechseln wir für die Block-Prüfung kurz auf den Hauptthread
            Bukkit.getScheduler().runTask(system.getPlugin(), () -> {
                Location validLocation = scanVerticalColumn(world, randomX, randomZ);

                if (validLocation != null) {
                    int killInterval = config.getInt("KillChestAfterTime", 600);
                    RLCChest chest = new RLCChest(system, validLocation, killInterval);
                    chest.generate();
                } else {
                    // Wenn kein Ort im Chunk gefunden wurde (z.B. tiefer Ozean): Direkt neu würfeln
                    tryToSpawnChest();
                }
            });
        });
    }

    private Location scanVerticalColumn(World world, int x, int z) {
        int minY = config.getInt("SmallestDinctance_Y");
        int maxY = config.getInt("LargestDinctance_Y");

        // Wir scannen von oben nach unten, um Höhlen oder die Oberfläche zu treffen
        for (int y = maxY; y >= minY; y--) {
            Block block = world.getBlockAt(x, y, z);
            Material type = block.getType();

            if (type == Material.AIR || type == Material.CAVE_AIR) {
                Block ground = block.getRelative(0, -1, 0);

                if (isValidGround(ground.getType())) {
                    Block above = block.getRelative(0, 1, 0);
                    if (above.getType() == Material.AIR || above.getType() == Material.CAVE_AIR) {
                        return block.getLocation();
                    }
                }
            }
        }
        return null;
    }

    private boolean isValidGround(Material material) {
        return material.isSolid()
                && material != Material.WATER
                && material != Material.LAVA
                && material != Material.BARRIER
                && material != Material.BEDROCK;
    }

    private void startEffectsTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (cancel) { cancel(); return; }

                // Partikeleffekte für alle aktiven Kisten im Sekundentakt abspielen
                for (RLCChest rlcChest : system.getChestCache().values()) {
                    Location loc = rlcChest.getLocation();
                    if (loc != null && loc.getWorld() != null) {
                        loc.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 0);
                    }
                }

                // Kisten-Lifetimer runterzählen und löschen
                for (RLCChest chest : new ArrayList<>(system.getChestCache().values())) {
                    int time = chest.getKillTime();
                    if (time <= 0) {
                        chest.kill();
                        system.getChestCache().remove(chest.getLocation());
                    } else {
                        chest.setKillTime(time - 1);
                    }
                }
            }
        }.runTaskTimer(system.getPlugin(), 20L, 20L);
    }
}
