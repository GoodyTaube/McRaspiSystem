package eu.goodyfx.mcraspi.core.events;

import eu.goodyfx.mcraspi.McRaspiSystem;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class RaspiItemsListener implements Listener {

    private final McRaspiSystem plugin;

    public RaspiItemsListener(McRaspiSystem plugin) {
        this.plugin = plugin;
        plugin.setListeners(this);
    }


    @EventHandler
    public void onHangingDestroy(HangingBreakEvent breakEvent) {
        if (breakEvent.getEntity().getType().equals(EntityType.ITEM_FRAME)) {
            if (breakEvent.getCause().equals(HangingBreakEvent.RemoveCause.ENTITY)) {
                ItemFrame frame = (ItemFrame) breakEvent.getEntity();
                if (!frame.isVisible()) {
                    breakEvent.setCancelled(true);
                    ItemStack stack = new ItemStack(Material.ITEM_FRAME, 1);
                    ItemMeta meta = stack.getItemMeta();
                    PersistentDataContainer container = meta.getPersistentDataContainer();
                    container.set(new NamespacedKey(plugin, "invisible"), PersistentDataType.BYTE, (byte) 1);
                    meta.displayName(MiniMessage.miniMessage().deserialize("<dark_red>Unsichtbares Item-Frame"));
                    stack.setItemMeta(meta);
                    breakEvent.getEntity().getLocation().getWorld().dropItem(breakEvent.getEntity().getLocation(), stack);
                    breakEvent.getEntity().remove();
                }
            }
        }
    }

    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {
        if (event.getEntity().getType().equals(EntityType.ITEM_FRAME)) {
            ItemFrame itemFrame = (ItemFrame) event.getEntity();
            Player player = event.getPlayer();
            if (player.getInventory().getItemInMainHand().getType().equals(Material.ITEM_FRAME)) {
                ItemStack stack = player.getInventory().getItemInMainHand();
                PersistentDataContainer container = stack.getItemMeta().getPersistentDataContainer();
                if (container.has(new NamespacedKey(plugin, "invisible"), PersistentDataType.BYTE)) {
                    itemFrame.setVisible(false);
                }
            }

            if (player.getInventory().getItemInOffHand().getType().equals(Material.ITEM_FRAME)) {
                ItemStack stack = player.getInventory().getItemInOffHand();
                PersistentDataContainer container = stack.getItemMeta().getPersistentDataContainer();
                if (container.has(new NamespacedKey(plugin, "invisible"), PersistentDataType.BYTE)) {
                    itemFrame.setVisible(false);
                }
            }


        }
    }



}
