package eu.goodyfx.system.core.tasks;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.database.RaspiPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayTimeTask extends BukkitRunnable {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    public PlayTimeTask() {
        this.runTaskTimer(plugin, 0, 20 * 60);
    }

    @Getter
    private static final Map<UUID, Long> joinCache = new ConcurrentHashMap<>();

    private final long millis = 3600000;

    @Override
    public void run() {
        long current = System.currentTimeMillis();
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            long lastChek = getJoinCache().get(uuid);


            long diff = current - lastChek;

            if (diff >= millis) {
                long hours = diff / millis;
                addPlaytime(player, hours);
                joinCache.put(player.getUniqueId(), lastChek + (hours * millis));
            }

        }
    }

    private void addPlaytime(Player player, long hoursToAdd) {
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);

        int old = raspiPlayer.userData().getOnlineHours();

        // deine eigene Speicherung passiert woanders
        int updated = old + (int) hoursToAdd;

        if (updated > old) {
            raspiPlayer.userData().setOnlineHours(updated);
            raspiPlayer.sendActionBar("<green>+1 OnlineStunde");
        }
    }


}
