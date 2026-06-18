package eu.goodyfx.mcraspi.core.tasks;

import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayTimeTask extends BukkitRunnable {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    @Getter
    private static final Scoreboard onlineHours = Bukkit.getScoreboardManager().getMainScoreboard();
    private static Objective objective = onlineHours.getObjective("hours");

    public PlayTimeTask() {
        if (objective == null) {
            objective = onlineHours.registerNewObjective("hours", Criteria.DUMMY, MiniMessage.miniMessage().deserialize("Onlinestunden"));
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
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
            long lastChek = getJoinCache().getOrDefault(uuid, System.currentTimeMillis());
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
            objective.getScore(player).setScore(raspiPlayer.userData().getOnlineHours());
        }

    }

    public static void updatePlayerValue(Player player){
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        objective.getScore(player).setScore(raspiPlayer.userData().getOnlineHours());
    }


}
