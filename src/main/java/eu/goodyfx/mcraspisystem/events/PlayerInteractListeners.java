package eu.goodyfx.mcraspisystem.events;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.SitCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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

public class PlayerInteractListeners implements Listener {

    private final McRaspiSystem plugin;

    public PlayerInteractListeners(McRaspiSystem system) {
        this.plugin = system;
        system.setListeners(this);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent interactEvent) {
        //MainEvent to call for all Systems
        lootSystem(interactEvent);
    }


    private void lootSystem(PlayerInteractEvent interactEvent) {
        Player player = interactEvent.getPlayer();
        Action action = interactEvent.getAction();

        sit(interactEvent);

        if (action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR)) {
            lootItemLevelMeet(interactEvent);
        } else if (interactEvent.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block clicked = interactEvent.getClickedBlock();
            if (clicked != null && clicked.getType().equals(Material.POLISHED_BLACKSTONE_BUTTON)) {
                Location location = clicked.getLocation();
                if (plugin.getModule().getLootManager().warpExist(location)) {
                    Location locationTel = plugin.getModule().getLootManager().get(interactEvent.getClickedBlock().getLocation());
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

    private void lootItemLevelMeet(PlayerInteractEvent interactEvent) {
        Player player = interactEvent.getPlayer();
        if (player.getInventory().getItemInMainHand().getType().equals(Material.COOKED_BEEF) && Objects.equals(interactEvent.getHand(), EquipmentSlot.HAND) && player.getExp() != 0f) {
            ItemStack stack = player.getInventory().getItemInMainHand().clone();
            if (stack.hasItemMeta() && stack.getItemMeta().hasCustomModelData() && stack.getItemMeta().getCustomModelData() == 1) {
                ItemMeta meta = stack.getItemMeta();
                PersistentDataContainer container = meta.getPersistentDataContainer();
                float amount = player.getExp();
                container.set(new NamespacedKey(plugin, "level"), PersistentDataType.STRING, String.valueOf(amount));
                player.setExp(0f);
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

    private void sit(PlayerInteractEvent interactEvent) {
        Player player = interactEvent.getPlayer();
        Action action = interactEvent.getAction();

        if (action.equals(Action.RIGHT_CLICK_BLOCK) && interactEvent.getHand() == EquipmentSlot.HAND && interactEvent.getPlayer().getInventory().getItem(EquipmentSlot.HAND).getType().equals(Material.AIR) && !SitCommand.getSits().containsKey(player.getUniqueId())) {
            if (interactEvent.getClickedBlock().getLocation().getY() > player.getLocation().getY()) {
                return;
            }
            Block block = interactEvent.getClickedBlock();
            if (block != null && (block.getBlockData() instanceof Stairs || block.getBlockData() instanceof Slab)) {
                Location location = block.getLocation();
                SitCommand.add(player, location, -0.53);
            }
            if (block != null && Tag.WOOL_CARPETS.isTagged(block.getType())) {
                Location location = block.getLocation();
                SitCommand.add(player, location, 0.046);
            }
        }
    }


}
