package eu.goodyfx.system.core.events;

import eu.goodyfx.system.McRaspiSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoryListeners implements Listener {

    private final McRaspiSystem plugin  = JavaPlugin.getPlugin(McRaspiSystem.class);

    public InventoryListeners(){
        plugin.setListeners(this);
    }

    @EventHandler
    public void open(InventoryOpenEvent event){
        Inventory inventory = event.getInventory();
        plugin.getModule().getItemConverterManager().convert(inventory);
    }

}
