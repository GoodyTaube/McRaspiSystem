package eu.goodyfx.system.core.events;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.ItemBuilder;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import org.bukkit.Material;
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

    private boolean containsCompass(RaspiPlayer player) {
        Inventory inventory = player.getPlayer().getInventory();
        return inventory.contains(compass);
    }

    public boolean inventoryFull(RaspiPlayer player) {
        Inventory inventory = player.getPlayer().getInventory();
        return inventory.firstEmpty() == -1;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCompassJoin(PlayerJoinEvent joinEvent) {
        RaspiPlayer player = Raspi.players().get(joinEvent.getPlayer());
        compassCheck(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCompassDeath(PlayerDeathEvent deathEvent) {
        RaspiPlayer player = Raspi.players().get(deathEvent.getPlayer());
        deathEvent.getDrops().remove(compass);
    }

    @EventHandler
    public void onRespawnCompass(PlayerRespawnEvent respawnEvent) {
        RaspiPlayer player = Raspi.players().get(respawnEvent.getPlayer());
        compassCheck(player);
    }

    private void compassCheck(RaspiPlayer player) {
        if (!containsCompass(player) && !inventoryFull(player)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.getPlayer().getInventory().addItem(compass);
                }
            }.runTaskAsynchronously(plugin);
        }
    }


}
