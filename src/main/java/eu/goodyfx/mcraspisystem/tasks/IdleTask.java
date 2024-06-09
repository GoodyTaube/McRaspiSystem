package eu.goodyfx.mcraspisystem.tasks;


import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.AFKCommand;
import eu.goodyfx.mcraspisystem.managers.PlayerSettingsManager;
import eu.goodyfx.mcraspisystem.utils.Settings;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class IdleTask extends BukkitRunnable {

    private final McRaspiSystem utilities;
    private final PlayerSettingsManager playerSettingsManager;

    private final Plugin plugin;

    public IdleTask(McRaspiSystem utilities, Plugin plugin) {
        this.plugin = plugin;
        this.utilities = utilities;
        this.playerSettingsManager = utilities.getModule().getPlayerSettingsManager();
        start();
    }

    private void start() {
        this.runTaskTimerAsynchronously(plugin, 40, 40L);
    }

    public void stop() {
        this.cancel();
    }


    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(all -> {
            if (!utilities.getConfig().getBoolean("Utilities.afk.autoAFK")) {
                //Enable / Disable out of Config
                return;
            }

            if (!playerSettingsManager.contains(Settings.AUTO_AFK, all)) {
                //If Player Disabled Auto AFK
                return;
            }

            if (utilities.getModule().getUserManager().getAfkContainer().containsKey(all.getUniqueId())) {
                //Check if Player is already AFK
                return;
            }

            if (AFKCommand.getPlayerIDLE().containsKey(all.getUniqueId())) {
                //Player AFK Add
                AFKCommand.getPlayerIDLE().put(all.getUniqueId(), AFKCommand.getPlayerIDLE().get(all.getUniqueId()) + 1);
            } else {
                //Player AFK Start
                AFKCommand.getPlayerIDLE().put(all.getUniqueId(), 1);
            }

            //Check if IDLE time is equal to config
            if (AFKCommand.getPlayerIDLE().get(all.getUniqueId()) != utilities.getConfig().getInt("Utilities.afk.idleTime")) {
                return;
            }

            //Perform Task sync because Bukkit likes that more than async
            Bukkit.getScheduler().runTask(utilities, () -> {
                all.performCommand("afk");
                AFKCommand.getPlayerIDLE().remove(all.getUniqueId());
            });

        });


    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
    }


}
