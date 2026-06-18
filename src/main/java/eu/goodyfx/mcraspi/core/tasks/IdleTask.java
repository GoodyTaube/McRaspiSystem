package eu.goodyfx.mcraspi.core.tasks;


import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.commands.AFKCommandContainer;
import eu.goodyfx.mcraspi.core.utils.Settings;
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

        Raspi.playerLifeCycleService().getCachedRaspiPlayers().forEach(all -> {
            if (!plugin.getConfig().getBoolean("Utilities.afk.autoAFK")) {
                //Enable / Disable out of Config
                return;
            }


            if (!all.settings().get(Settings.AUTO_AFK)) {
                //If Player Disabled Auto AFK
                return;
            }
            if (all.isAfk()) {
                //Check if Player is already AFK
                return;
            }
            if (AFKCommandContainer.getPlayerIDLE().containsKey(all.getUUID())) {
                //Player AFK Add
                AFKCommandContainer.getPlayerIDLE().put(all.getUUID(), AFKCommandContainer.getPlayerIDLE().get(all.getUUID()) + 1);
            } else {
                //Player AFK Start
                AFKCommandContainer.getPlayerIDLE().put(all.getUUID(), 1);
            }

            //Check if IDLE time is equal to config
            if (AFKCommandContainer.getPlayerIDLE().get(all.getUUID()) != plugin.getConfig().getInt("Utilities.afk.idleTime")) {
                return;

            }

            //Perform Task sync because Bukkit likes that more than async
            Bukkit.getScheduler().runTask(plugin, () -> {
                all.getPlayer().performCommand("afk");
                AFKCommandContainer.getPlayerIDLE().remove(all.getPlayer().getUniqueId());
            });


        });

    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
    }


}
