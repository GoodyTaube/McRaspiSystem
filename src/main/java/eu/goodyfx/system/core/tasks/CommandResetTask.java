package eu.goodyfx.system.core.tasks;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.commandsOLD.RandomTeleportCommand;
import org.bukkit.scheduler.BukkitRunnable;

public class CommandResetTask extends BukkitRunnable {

    public CommandResetTask(McRaspiSystem plugin) {
        this.runTaskTimerAsynchronously(plugin, 0L, 20L * 60 * 60 * 24);
    }

    @Override
    public void run() {
        RandomTeleportCommand.getPlayerContainer().clear();
    }
}
