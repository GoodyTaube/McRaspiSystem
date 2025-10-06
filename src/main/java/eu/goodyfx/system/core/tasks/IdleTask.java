package eu.goodyfx.system.core.tasks;


import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.commandsOLD.AFKCommand;
import eu.goodyfx.system.core.utils.Raspi;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class IdleTask extends BukkitRunnable {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    public IdleTask() {
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

        Raspi.players().getRaspiPlayers().forEach(all -> {
            if (!plugin.getConfig().getBoolean("Utilities.afk.autoAFK")) {
                //Enable / Disable out of Config
                return;
            }

            if (!all.getUserSettings().isAuto_afk()) {
                //If Player Disabled Auto AFK
                return;
            }
            if (!all.getUserSettings().isAfk()) {
                //Check if Player is already AFK
                return;
            }
            if (AFKCommand.getPlayerIDLE().containsKey(all.getUUID())) {
                //Player AFK Add
                AFKCommand.getPlayerIDLE().put(all.getUUID(), AFKCommand.getPlayerIDLE().get(all.getUUID()) + 1);
            } else {
                //Player AFK Start
                AFKCommand.getPlayerIDLE().put(all.getUUID(), 1);
            }

            //Check if IDLE time is equal to config
            if (AFKCommand.getPlayerIDLE().get(all.getUUID()) != plugin.getConfig().getInt("Utilities.afk.idleTime")) {
                return;
            }

            //Perform Task sync because Bukkit likes that more than async
            Bukkit.getScheduler().runTask(plugin, () -> {
                all.getPlayer().performCommand("afk");
                AFKCommand.getPlayerIDLE().remove(all.getPlayer().getUniqueId());
            });


        });

    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
    }


}
