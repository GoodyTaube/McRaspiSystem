package eu.goodyfx.mcraspi.modules.loot.worldlootchest.tasks;

import eu.goodyfx.mcraspi.modules.loot.worldlootchest.RandomLootChest;
import eu.goodyfx.mcraspi.modules.loot.worldlootchest.utils.RLCChest;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RLCTasks {

    private final RandomLootChest system;
    private final Random random;
    private final FileConfiguration config;
    public boolean cancel = false;
    private final List<World> availableWorlds = new ArrayList<>();

    private static final String PATH_MIN_X = "World_Settings.%s.min_X";
    private static final String PATH_MAX_X = "World_Settings.%s.max_X";
    private static final String PATH_MIN_Z = "World_Settings.%s.min_Z";
    private static final String PATH_MAX_Z = "World_Settings.%s.max_Z";
    private static final String PATH_MIN_Y = "World_Settings.%s.min_Y";
    private static final String PATH_MAX_Y = "World_Settings.%s.max_Y";


    public RLCTasks(RandomLootChest system) {
        this.system = system;
        this.random = system.getPlugin().getRandom();
        this.config = system.getConfigManager().getConfig();
        worlds();
        startSpawnTimer();
        startEffectsTimer();
    }

    private void worlds() {

        if (!config.contains("Worlds")) {
            return;
        }
        List<String> name = config.getStringList("Worlds");
        for (String world : name) {
            if (Bukkit.getWorld(world) != null) {
                availableWorlds.add(Bukkit.getWorld(world));
            }
        }
    }


    private void startSpawnTimer() {
        int interval = config.getInt("SpawnChestPerTime", 300);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (cancel) {
                    cancel();
                    return;
                }
                tryToSpawnChest();
            }
        }.runTaskTimer(system.getPlugin(), 20L * interval, 20L * interval);
    }

    private void tryToSpawnChest() {
        if (availableWorlds.isEmpty()) {
            return;
        }
        World world = availableWorlds.get(random.nextInt(availableWorlds.size()));
        if (world == null) {
            return;
        }

        String worldName = world.getName();
        int conMinX = config.getInt(String.format(PATH_MIN_X, worldName));
        int conMaxX = config.getInt(String.format(PATH_MAX_X, worldName));
        int conMinZ = config.getInt(String.format(PATH_MIN_Z, worldName));
        int conMaxZ = config.getInt(String.format(PATH_MAX_Z, worldName));


        int randomX = random.nextInt(Math.max(1, conMaxX - conMinX + 1)) + conMinX;
        int randomZ = random.nextInt(Math.max(1, conMaxZ - conMinZ + 1)) + conMinZ;

        // Paper lädt den Chunk komplett asynchron im Hintergrund
        World finalWorld = world;
        world.getChunkAtAsync(randomX >> 4, randomZ >> 4).thenAccept(chunk -> {
            // Sobald geladen, wechseln wir für die Block-Prüfung kurz auf den Hauptthread
            Bukkit.getScheduler().runTask(system.getPlugin(), () -> {
                Location validLocation = scanVerticalColumn(finalWorld, randomX, randomZ);

                if (validLocation != null) {
                    int killInterval = config.getInt("KillChestAfterTime", 600);
                    RLCChest chest = new RLCChest(system, validLocation, killInterval);
                    chest.generate();
                } else {
                    // Wenn kein Ort im Chunk gefunden wurde (z.B. tiefer Ozean): Direkt neu würfeln
                    Bukkit.getScheduler().runTaskLater(system.getPlugin(), this::tryToSpawnChest, 1L);
                }
            });
        });
    }

    private Location scanVerticalColumn(World world, int x, int z) {
        int minY = config.getInt(String.format(PATH_MIN_Y, world.getName()));
        int maxY = config.getInt(String.format(PATH_MAX_Y, world.getName()));

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
                if (cancel) {
                    cancel();
                    return;
                }

                // Partikel effekte für alle aktiven Kisten im Sekundentakt abspielen
                for (RLCChest rlcChest : system.getChestCache().values()) {
                    Location loc = rlcChest.getLocation();
                    if (loc != null && loc.getWorld() != null) {
                        loc.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 0);
                    }
                }

                // Kisten Lifetime runterzählen und löschen
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
