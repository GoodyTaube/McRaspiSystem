package eu.goodyfx.system.randomlootchest.events;

import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.database.RaspiPlayer;
import eu.goodyfx.system.randomlootchest.RandomLootChest;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class RandomLootChestPlayerEvents implements Listener {

    private final RandomLootChest system;

    public RandomLootChestPlayerEvents(RandomLootChest subSystem) {
        subSystem.getPlugin().setListeners(this);
        this.system = subSystem;
    }


    @EventHandler
    public void onChestInteract(PlayerInteractEvent interactEvent) {
        Block block = interactEvent.getClickedBlock();
        Player player = interactEvent.getPlayer();
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        if (!(block instanceof Chest chest)) {
            return;
        }
        PersistentDataContainer container = chest.getPersistentDataContainer();
        if (container.has(system.getChestKey(), PersistentDataType.BYTE)) {
            interactEvent.setCancelled(true);
            player.openInventory(Bukkit.getServer().createInventory(null, InventoryType.CHEST));
        }

    }


}
