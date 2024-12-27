package eu.goodyfx.mcraspisystem.tasks;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryBackup extends BukkitRunnable {

    private final McRaspiSystem plugin;

    public InventoryBackup(McRaspiSystem mcRaspiSystem) {
        this.plugin = mcRaspiSystem;
        this.runTaskTimerAsynchronously(plugin, 5 * 60 * 20L, 10 * 60 * 20L);
    }

    private final static Map<UUID, ItemStack[]> inventoryContainer = new HashMap<>();

    @Override
    public void run() {
        for (RaspiPlayer player : plugin.getRaspiPlayers()) {
            inventoryContainer.put(player.getUUID(), player.getPlayer().getInventory().getContents());
        }
    }

    public static Map<UUID, ItemStack[]> getInventoryContainer() {
        return inventoryContainer;
    }

}
