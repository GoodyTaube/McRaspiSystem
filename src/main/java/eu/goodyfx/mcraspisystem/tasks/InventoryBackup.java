package eu.goodyfx.mcraspisystem.tasks;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryBackup extends BukkitRunnable {

    private final McRaspiSystem plugin;

    public InventoryBackup(McRaspiSystem mcRaspiSystem) {
        this.plugin = mcRaspiSystem;
        this.runTaskTimerAsynchronously(plugin,  60 * 20L,  60 * 20L);
    }

    private static Map<UUID, ItemStack[]> inventoryContainer = new HashMap<>();

    @Override
    public void run() {
        for (RaspiPlayer player : plugin.getRaspiPlayers()) {
            inventoryContainer.put(player.getUUID(), player.getPlayer().getInventory().getContents());
            plugin.getLogger().info("Inventar von " + player.getName() + " Erfolgreich gespeichert.");
        }
    }

    public static Map<UUID, ItemStack[]> getInventoryContainer() {
        return inventoryContainer;
    }

}
