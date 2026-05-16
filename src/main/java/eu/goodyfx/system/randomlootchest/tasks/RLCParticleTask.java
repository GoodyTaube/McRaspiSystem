package eu.goodyfx.system.randomlootchest.tasks;

import eu.goodyfx.system.randomlootchest.RandomLootChest;
import eu.goodyfx.system.randomlootchest.utils.GeneratedChest;
import org.bukkit.Effect;
import org.bukkit.scheduler.BukkitRunnable;

public class RLCParticleTask extends BukkitRunnable {

    public RLCParticleTask(RandomLootChest system) {
        this.runTaskTimerAsynchronously(system.getPlugin(), 0, 20);
    }

    @Override
    public void run() {
        for (GeneratedChest chest : SpawnTimerTask.getChests()) {
            chest.getLocation().getWorld().playEffect(chest.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
        }

    }
}
