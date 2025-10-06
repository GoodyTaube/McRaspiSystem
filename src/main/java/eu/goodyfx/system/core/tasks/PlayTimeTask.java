package eu.goodyfx.system.core.tasks;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.Raspi;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;

public class PlayTimeTask extends BukkitRunnable {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    public PlayTimeTask() {
        this.runTaskTimerAsynchronously(plugin, 0, 1);
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(this::check);
    }

    public void check(Player player) {
        int onlineHours = 0;
        Objective objective = player.getScoreboard().getObjective("Onlinestunden");
        Objective time = player.getScoreboard().getObjective("time");

        if (objective != null && time != null) {
            int score = time.getScore(player).getScore();
            int HOUR_IN_TICK = 20 * 60 * 60;
            while (score / HOUR_IN_TICK >= 1) {
                score = score - HOUR_IN_TICK;
                time.getScore(player).setScore(score);
                onlineHours = onlineHours + 1;
                Raspi.players().get(player).getUser().setOnlineHours(onlineHours);
                player.sendActionBar(MiniMessage.miniMessage().deserialize("<green> +1 Onlinestunde"));
                objective.getScore(player).setScore(objective.getScore(player).getScore() + 1);
            }

        }
    }

}
