package eu.goodyfx.mcraspisystem.tasks;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.InHeadCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class InHeadTask extends BukkitRunnable {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    public InHeadTask() {
        this.runTaskTimerAsynchronously(plugin, 0L, 2 * 20L);
    }

    private final Map<UUID, UUID> inHeadContainer = InHeadCommand.getInHeadContainer();


    @Override
    public void run() {
        for (UUID uuid : inHeadContainer.keySet()) {
            Player target = Bukkit.getPlayer(inHeadContainer.get(uuid));
            Player player = Bukkit.getPlayer(uuid);
            if (target != null && player != null) {

                if (player.getSpectatorTarget() == null) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.setSpectatorTarget(target);
                        }
                    }.runTask(plugin);
                }

                //new InHeadEvent(player, target).callEvent();
            } else if (player != null && target == null) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        OfflinePlayer target = Bukkit.getOfflinePlayer(inHeadContainer.get(uuid));
                        player.performCommand("inhead");
                        plugin.getRaspiPlayer(player).sendMessage(String.format("%s ist offline gegangen. InHead beendet!", target.getName()), true);
                    }
                }.runTask(plugin);
            }
        }
    }
}
