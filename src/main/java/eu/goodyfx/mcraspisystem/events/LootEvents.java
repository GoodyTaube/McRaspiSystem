package eu.goodyfx.mcraspisystem.events;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public record LootEvents(McRaspiSystem plugin) implements Listener {

    public LootEvents {
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


    @EventHandler
    public void enDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            ItemStack stack = player.getInventory().getItemInMainHand();
            if (stack.getType().equals(Material.STICK) && stack.hasItemMeta()) {
                if (stack.getItemMeta().hasCustomModelData()) {
                    if (stack.getItemMeta().getCustomModelData() == 1) {
                        ItemMeta meta = stack.getItemMeta();
                        PersistentDataContainer container = meta.getPersistentDataContainer();
                        if (plugin.getConfig().contains("items.knock.uses")) {
                            if (!container.has(plugin.getRaspiItemKey(), PersistentDataType.INTEGER)) {
                                container.set(plugin.getRaspiItemKey(), PersistentDataType.INTEGER, plugin.getConfig().getInt("items.knock.uses"));
                            }
                            Integer current = container.get(plugin.getRaspiItemKey(), PersistentDataType.INTEGER);
                            assert current != null;
                            container.set(plugin.getRaspiItemKey(), PersistentDataType.INTEGER, (current - 1));

                            current = current - 1;
                            List<Component> lore = new ArrayList<>();
                            lore.add(MiniMessage.miniMessage().deserialize("Benutzungen: " + current));
                            meta.lore(lore);
                            stack.setItemMeta(meta);

                            if (current == 0) {
                                stack.setAmount(stack.getAmount() - 1);
                                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);

                            }

                            if (plugin.getConfig().contains("items.knock.power")) {
                                event.getEntity().teleport(event.getEntity().getLocation().add(0, 1, 0));
                                event.getEntity().setVelocity(player.getEyeLocation().getDirection().multiply(plugin.getConfig().getInt("items.knock.power")));
                            }
                        }

                    }
                }
            }
        }
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent joinEvent) {
        if (LootConsumeEvents.getTimeStampMap().containsKey(joinEvent.getPlayer().getUniqueId())) {
            Player player = joinEvent.getPlayer();
            player.setAllowFlight(true);
            player.setFlying(true);
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() == null) {
            return;
        }
        if (event.getInventory() instanceof MerchantInventory) {
            MerchantInventory inventory = (MerchantInventory) event.getInventory();
            Merchant merchant = inventory.getMerchant();
            List<MerchantRecipe> recipes = new ArrayList<>();

            ItemStack stack1_result = new ItemBuilder(Material.GOLDEN_APPLE).displayName("Raspi Apple").addLore("COOLER_RASPI_COIN").setAmount(1).build();

            ItemStack voting = new ItemBuilder(Material.CLOCK).displayName("VOTING COIN").build();

            MerchantRecipe merchantRecipe = new MerchantRecipe(stack1_result, 99999);
            MerchantRecipe merchantRecipe2 = new MerchantRecipe(voting, 99999);


            merchantRecipe.addIngredient(new ItemBuilder(Material.CLOCK).setAmount(25).displayName("VOTING COIN").build());
            merchantRecipe2.addIngredient(new ItemStack(Material.COMPASS, 22));
            recipes.add(merchantRecipe);
            recipes.add(merchantRecipe2);
            merchant.setRecipes(recipes);
        }
    }

}