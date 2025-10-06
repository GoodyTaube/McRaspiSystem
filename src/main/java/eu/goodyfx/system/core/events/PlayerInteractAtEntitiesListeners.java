package eu.goodyfx.system.core.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.lootchest.utils.LootChestLoot;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import eu.goodyfx.system.core.utils.RaspiSounds;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class PlayerInteractAtEntitiesListeners implements Listener {

    private final Random random = new Random();
    private final List<UUID> debugActivation = new ArrayList<>();
    private final McRaspiSystem plugin;

    public PlayerInteractAtEntitiesListeners(McRaspiSystem system) {
        this.plugin = system;
        system.setListeners(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        lootItemSticks(event);
        villagerInteraction(event);
    }


    private void lootItemSticks(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if (event.getHand().equals(EquipmentSlot.HAND) && (player.getInventory().getItemInMainHand().getType().equals(Material.STICK))) {
            ItemStack stack = player.getInventory().getItemInMainHand();
            if (stack.hasItemMeta() && (stack.getItemMeta().hasCustomModelData()) && entity instanceof Animals animal) {
                int data = stack.getItemMeta().getCustomModelData();
                if (data == 2) {
                    //ADULT
                    animal.setAdult();
                    animal.setAgeLock(true);
                    stack.setAmount(stack.getAmount() - 1);
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
                }
                if (data == 3) {
                    //BABY
                    animal.setBaby();
                    animal.setAgeLock(true);
                    stack.setAmount(stack.getAmount() - 1);
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
                }
                event.setCancelled(true);
            }
        }
    }

    /**
     * Check if Player can Sit on Animal
     *
     * @param event The Event
     */
    private void sit(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();
        if ((entity instanceof Animals || entity instanceof WaterMob) && (entity.getPassengers().isEmpty() && player.getInventory().getItem(EquipmentSlot.HAND).getType().equals(Material.AIR))) {
            if (entity instanceof Cat || entity instanceof Wolf) {
                return;
            }
            entity.addPassenger(player);
        }
        interactLootChest(event);
    }

    /**
     * Create and Send Demo Packet to Player
     *
     * @param target The Packet receiver
     */
    private void sendDemoGUIPacket(Player target) {
        PacketContainer container = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);
        container.getGameStateIDs().write(0, 5);
        plugin.getHookManager().getProtocolManager().sendServerPacket(target, container);
    }


    private void villagerInteraction(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked().getType().equals(EntityType.VILLAGER)) {
            Villager villager = (Villager) event.getRightClicked();
            if (villager.getPersistentDataContainer().has(new NamespacedKey(plugin, "special"))) {
                event.setCancelled(true);
                event.getPlayer().sendRichMessage("<white>[<green>Trader<white>] <green><Johan> <gray>: Hello there... Ive got something for you!");
                event.getPlayer().openGrindstone(event.getPlayer().getLocation(), true);
            }
        }
    }

    private void interactLootChest(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Interaction) {
            Interaction interaction = (Interaction) event.getRightClicked();
            if (interaction.getPersistentDataContainer().has(new NamespacedKey(plugin, "special"))) {
                event.setCancelled(true);
                RaspiPlayer player = Raspi.players().get(event.getPlayer());
                if (plugin.getLootChestTimer().isLootChestReady()) {
                    new LootChestLoot(plugin).openLoot(player);
                } else {
                    player.playSound(RaspiSounds.ERROR);
                    player.sendActionBar("<red>LootChest ist noch nicht Offen!");
                }
            }
        }
    }


}
