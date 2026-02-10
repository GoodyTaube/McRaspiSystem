package eu.goodyfx.system.lootchest.events;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.lootchest.utils.LootItems;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportListeners implements Listener {

    private final LootItems item = LootItems.TELEPORTER;
    private final McRaspiSystem plugin = McRaspiSystem.getPlugin(McRaspiSystem.class);
    private final Map<UUID, Location> locationMap = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeleportPlace(BlockPlaceEvent placeEvent) {
        Raspi.players().withOnlinePlayer(placeEvent.getPlayer(), player -> {

            Location location = placeEvent.getBlockPlaced().getLocation();
            ItemStack stack = placeEvent.getItemInHand();
            Block block = placeEvent.getBlock();
            if (!block.getType().equals(item.getType())) {
                return;
            }
            if (!checkStack(stack)) {
                return;
            }

            if (locationMap.containsKey(player.getUUID())) {
                if (!plugin.getModule().getLootManager().existAndActive(locationMap.get(player.getUUID()), location)) {
                    plugin.getModule().getLootManager().setWarp(player.getPlayer(), locationMap.get(player.getUUID()), location);
                    player.sendActionBar(plugin.getModule().getRaspiMessages().getPrefix() + "Position 2 für Teleport gesetzt!");
                    player.sendMessage(plugin.getModule().getRaspiMessages().getPrefix() + "Der Teleport Nr." + plugin.getModule().getLootManager().size() + " wurde erstellt!");
                    player.getPlayer().playSound(location, Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f);
                    locationMap.remove(player.getUUID());
                    return;
                }
            } else {
                locationMap.put(player.getUUID(), location);
                player.sendActionBar(plugin.getModule().getRaspiMessages().getPrefix() + "Position 1 für Teleport gesetzt!");
            }

        });


    }

    private boolean checkStack(ItemStack stack) {
        boolean v = false;

        if (stack != null && stack.getType().equals(item.getType()) && stack.hasData(DataComponentTypes.CUSTOM_MODEL_DATA)) {
            v = true;
        }
        return v;
    }


}
