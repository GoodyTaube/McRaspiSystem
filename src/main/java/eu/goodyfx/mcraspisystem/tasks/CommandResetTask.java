package eu.goodyfx.mcraspisystem.tasks;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.RandomTeleportCommand;
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
