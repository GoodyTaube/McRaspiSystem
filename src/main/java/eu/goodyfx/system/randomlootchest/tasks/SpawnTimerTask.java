package eu.goodyfx.system.randomlootchest.tasks;

import eu.goodyfx.system.core.utils.RaspiFormatting;
import eu.goodyfx.system.randomlootchest.RandomLootChest;
import eu.goodyfx.system.randomlootchest.utils.GeneratedChest;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpawnTimerTask extends BukkitRunnable {

    @Getter
    private static final List<GeneratedChest> chests = new ArrayList<>();
    @Getter
    private static final Map<Location, Long> loaded_Chest = new ConcurrentHashMap<>();

    private final RandomLootChest system;
    private final FileConfiguration config;

    public SpawnTimerTask(RandomLootChest randomLootChest, int interval) {
        this.runTaskTimerAsynchronously(randomLootChest.getPlugin(), 0, interval * 20L);
        this.system = randomLootChest;
        this.config = randomLootChest.getConfigManager().getConfig();
    }

    @Override
    public void run() {
        handleKill();
        spawnChest();
    }

    private void handleKill() {
        for (GeneratedChest chest : chests) {
            Location location = chest.getLocation();
            if (chest.getKillTime() != System.currentTimeMillis()) {
                return;
            }
            chest.getChest().setType(Material.AIR);
            if (!config.getBoolean("BroadcastKillMessage")) {
                return;
            }
            String broadCast = config.getString("BroadcastMessage");
            if (broadCast == null) {
                return;
            }
            broadCast = RaspiFormatting.formattingChatMessage(broadCast);
            String x1 = String.valueOf(location.getBlockX());
            String y1 = String.valueOf(location.getBlockY());
            String z1 = String.valueOf(location.getBlockZ());
            broadCast = broadCast.replace("{X}", x1);
            broadCast = broadCast.replace("{Y}", y1);
            broadCast = broadCast.replace("{Z}", z1);
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize(broadCast));
        }
    }

    private void spawnChest() {
        chests.add(new GeneratedChest(system));
    }

}
