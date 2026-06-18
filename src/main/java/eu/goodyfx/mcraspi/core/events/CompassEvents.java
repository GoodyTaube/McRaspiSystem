package eu.goodyfx.mcraspi.core.events;

import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CompassEvents implements Listener {

    public CompassEvents() {
        plugin.setListeners(this);
    }

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    private final ItemStack compass = new ItemBuilder(Material.COMPASS).displayName("<red>Command Menü").addLore("<gray>Official McRaspi Merch").build();

    private boolean containsCompass(Player player) {
        Inventory inventory = player.getInventory();
        return inventory.contains(compass);
    }

    public boolean inventoryFull(Player player) {
        Inventory inventory = player.getInventory();
        return inventory.firstEmpty() == -1;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCompassJoin(PlayerJoinEvent joinEvent) {
        Player player = joinEvent.getPlayer();
        compassCheck(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCompassDeath(PlayerDeathEvent deathEvent) {
        deathEvent.getDrops().remove(compass);
    }

    @EventHandler
    public void onRespawnCompass(PlayerRespawnEvent respawnEvent) {
        compassCheck(respawnEvent.getPlayer());
    }

    private void compassCheck(Player player) {
        if (!containsCompass(player) && !inventoryFull(player)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.getInventory().addItem(compass);
                }
            }.runTaskAsynchronously(plugin);
        }
    }


}
