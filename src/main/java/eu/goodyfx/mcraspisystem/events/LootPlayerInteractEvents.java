package eu.goodyfx.mcraspisystem.events;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LootPlayerInteractEvents implements Listener {

    private final McRaspiSystem plugin;

    public LootPlayerInteractEvents(McRaspiSystem plugin) {
        this.plugin = plugin;
        plugin.setListeners(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onStick(PlayerInteractAtEntityEvent event) {
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR)) {
            performLevelFleisch(event);
        } else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block clicked = event.getClickedBlock();
            if (clicked != null && clicked.getType().equals(Material.POLISHED_BLACKSTONE_BUTTON)) {
                Location location = clicked.getLocation();
                if (plugin.getModule().getLootManager().warpExist(location)) {
                    Location locationTel = plugin.getModule().getLootManager().get(event.getClickedBlock().getLocation());
                    locationTel.setYaw(player.getBodyYaw());
                    locationTel.setPitch(player.getLocation().getPitch());

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.teleport(locationTel, PlayerTeleportEvent.TeleportCause.PLUGIN);
                        }
                    }.runTaskLater(plugin, 4L);
                }
            }
        }
    }

    private void performLevelFleisch(PlayerInteractEvent interactEvent) {
        Player player = interactEvent.getPlayer();
        if (player.getInventory().getItemInMainHand().getType().equals(Material.COOKED_BEEF) && Objects.equals(interactEvent.getHand(), EquipmentSlot.HAND) && player.getLevel() != 0) {
            ItemStack stack = player.getInventory().getItemInMainHand().clone();
            if (stack.hasItemMeta() && stack.getItemMeta().hasCustomModelData() && stack.getItemMeta().getCustomModelData() == 1) {
                ItemMeta meta = stack.getItemMeta();
                PersistentDataContainer container = meta.getPersistentDataContainer();
                int amount = player.getLevel();
                container.set(new NamespacedKey(plugin, "level"), PersistentDataType.STRING, String.valueOf(amount));
                player.setLevel(0);
                player.setTotalExperience(0);
                List<Component> lore = new ArrayList<>();
                lore.add(MiniMessage.miniMessage().deserialize("<aqua>Iss mich um die EXP zu Bekommen."));
                lore.add(MiniMessage.miniMessage().deserialize("<gray><i>Links klick um Level zu Speichern."));
                lore.add(Component.empty());
                lore.add(MiniMessage.miniMessage().deserialize("<gray>EXP Gespeichert: <aqua>" + amount));
                meta.lore(lore);


                stack.setItemMeta(meta);
                player.getInventory().setItemInMainHand(stack);
                stack.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);
                stack.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }
    }

}
