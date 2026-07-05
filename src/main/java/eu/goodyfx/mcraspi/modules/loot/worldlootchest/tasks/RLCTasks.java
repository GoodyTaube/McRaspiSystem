package eu.goodyfx.mcraspi.modules.loot.worldlootchest.tasks;

import eu.goodyfx.mcraspi.modules.loot.worldlootchest.RandomLootChest;
import eu.goodyfx.mcraspi.modules.loot.worldlootchest.utils.RLCChest;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RLCTasks {

    private final RandomLootChest system;
    private final Random random;
    private final FileConfiguration config;
    public boolean cancel = false;
    private BukkitTask spawn;
    private BukkitTask particles;
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
        this.spawn = new BukkitRunnable() {
            @Override
            public void run() {
                if (cancel) {
                    cancel();
                    return;
                }
                if (Bukkit.getOnlinePlayers().isEmpty()) {
                    //Try to prevent Spawning Chest when nobody is online.
                    return;
                }
                tryToSpawnChest(0);
            }
        }.runTaskTimer(system.getPlugin(), 20L * interval, 20L * interval);
    }

    private void tryToSpawnChest(int times) {
        if (availableWorlds.isEmpty() || times > 20) {
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
        int minY = config.getInt(String.format(PATH_MIN_Y, world.getName()));
        int maxY = config.getInt(String.format(PATH_MAX_Y, world.getName()));

        int randomX = random.nextInt(Math.max(1, conMaxX - conMinX + 1)) + conMinX;
        int randomZ = random.nextInt(Math.max(1, conMaxZ - conMinZ + 1)) + conMinZ;
        world.getChunkAtAsync(randomX >> 4, randomZ >> 4).thenAccept(chunk -> {
            Bukkit.getScheduler().runTask(system.getPlugin(), () -> {
                Location validLocation = scanVerticalColumn(chunk, randomX, randomZ, minY, maxY);
                if (validLocation != null) {
                    int killInterval = config.getInt("KillChestAfterTime", 600);
                    RLCChest chest = new RLCChest(system, validLocation, killInterval);
                    chest.generate();
                } else {
                    // Wenn kein Ort im Chunk gefunden wurde (z.B. tiefer Ozean): Direkt neu würfeln
                    Bukkit.getScheduler().runTaskLater(system.getPlugin(), () -> tryToSpawnChest(times + 1), 2L);
                }
            });
        });
    }

    private Location scanVerticalColumn(Chunk chunk, int x, int z, int minY, int maxY) {
        int relativeX = Math.floorMod(x, 16);
        int relativeZ = Math.floorMod(z, 16);

        int worldMin = chunk.getWorld().getMinHeight();
        int worldMax = chunk.getWorld().getMaxHeight();

        int startY = Math.min(maxY, worldMax - 2);
        int endY = Math.max(minY, worldMin + 1);

        for (int y = startY; y >= endY; y--) {
            Block block = chunk.getBlock(relativeX, y, relativeZ);
            Material type = block.getType();

            if (type == Material.AIR || type == Material.CAVE_AIR) {
                Block ground = chunk.getBlock(relativeX, y - 1, relativeZ);

                if (isValidGround(ground.getType())) {
                    Block above = chunk.getBlock(relativeX, y + 1, relativeZ);
                    if (above.getType() == Material.AIR || above.getType().equals(Material.CAVE_AIR)) {
                        return new Location(chunk.getWorld(), x, y, z);
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

    /**
     * Particle Effects like MobSpawner for the LootChest
     */
    private void startEffectsTimer() {
        this.particles = new BukkitRunnable() {
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

                List<RLCChest> chests = new ArrayList<>(system.getChestCache().values());
                // Kisten Lifetime runterzählen und löschen
                for (RLCChest chest : chests) {
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

    public void cancelTasks() {
        this.cancel = true;
        if (spawn != null) spawn.cancel();
        if (particles != null) particles.cancel();
    }

}
