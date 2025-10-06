package eu.goodyfx.system.trader.events;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.trader.commands.TraderCommand;
import eu.goodyfx.system.trader.managers.TraderDB;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import io.papermc.paper.event.player.PlayerNameEntityEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class TraderListeners implements Listener {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    private final TraderDB traderDB = plugin.getModuleManager().getTraderDB();
    private static final String TRADER_NAME = "trader";

    public TraderListeners() {
        plugin.setListeners(this);
    }

    @EventHandler
    public void onNameChange(PlayerNameEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Villager villager) {
            PersistentDataContainer container = villager.getPersistentDataContainer();
            if (container.has(plugin.getNameSpaced(TRADER_NAME))) {
                event.setCancelled(true);
                event.getPlayer().sendActionBar(MiniMessage.miniMessage().deserialize("<red>\"Ah ah ah, you didn't say the magic word\""));
            }
        }

    }


    @EventHandler
    public void onTraderClick(PlayerInteractAtEntityEvent entityEvent) {
        Entity entity = entityEvent.getRightClicked();
        if (entity.getType().equals(EntityType.VILLAGER)) {
            Villager villager = (Villager) entity;
            RaspiPlayer player = Raspi.players().get(entityEvent.getPlayer());
            PersistentDataContainer container = villager.getPersistentDataContainer();

            if (player.getPlayer().getGameMode().equals(GameMode.CREATIVE) && player.getPlayer().isSneaking()) {
                String name = container.get(plugin.getNameSpaced("trader"), PersistentDataType.STRING);
                player.getPlayer().performCommand(String.format("trader edit %s", traderDB.getTraderByID(name)));
                return;
            }

            if (container.has(plugin.getNameSpaced(TRADER_NAME))) {
                String traderUID = container.get(plugin.getNameSpaced(TRADER_NAME), PersistentDataType.STRING);
                String traderName = String.format("<green>%s", traderDB.getTraderName(traderDB.getTraderByID(traderUID)));
                //Generate Merchant and OPEN by Type
                entityEvent.setCancelled(true);
                player.openInventory(MenuType.MERCHANT.builder().merchant(generateMerchant(traderDB.getTraderByID(traderUID))).title(MiniMessage.miniMessage().deserialize(traderName)).build(player.getPlayer()));
            }

        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent clickEvent) {
        if (clickEvent.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) {
            return;
        }
        RaspiPlayer player = Raspi.players().get((Player) clickEvent.getWhoClicked());
        String title = LegacyComponentSerializer.legacyAmpersand().serialize(clickEvent.getView().title());
        if (title.equalsIgnoreCase("Trader Settings")) {
            ItemStack stack = clickEvent.getCurrentItem();
            clickedSave(clickEvent, player, stack);
            clickedDelete(clickEvent, player, stack);
        }
        if (title.equalsIgnoreCase("Trader Rezepte")) {
            clickEvent.setCancelled(true);
            addItem(player, clickEvent);
            clickedRandom(player, clickEvent);
            clickedRecipe(player, clickEvent);
        }
    }

    private void clickedRecipe(RaspiPlayer player, InventoryClickEvent clickEvent) {
        ItemStack current = clickEvent.getCurrentItem();
        if (current == null || !current.hasItemMeta() || !current.getItemMeta().hasCustomModelData()) {
            return;
        }

        if (!isItem(current, -1) && !isItem(current, -21) && !isItem(current, -20)) {
            int id = clickEvent.getCurrentItem().getItemMeta().getCustomModelData();
            String trader = TraderCommand.traderEditContainer.get(player.getUUID());
            TraderCommand.traderSAVEContainer.put(player.getUUID(), id);
            Inventory inventory = TraderCommand.getEditInventory();
            inventory.setItem(9, traderDB.getItemStack(trader, TraderDB.DB_SHOP_ITEM_1, id));
            inventory.setItem(10, traderDB.getItemStack(trader, TraderDB.DB_SHOP_ITEM_2, id));
            inventory.setItem(12, traderDB.getItemStack(trader, TraderDB.DB_SHOP_RES, id));
            player.getPlayer().closeInventory();
            player.openInventory(inventory);
        }
    }

    public void addItem(RaspiPlayer player, InventoryClickEvent clickEvent) {

        if (isItem(clickEvent.getCurrentItem(), -1)) {
            player.getPlayer().closeInventory();
            player.openInventory(TraderCommand.getEditInventory());
        }
    }

    public void clickedRandom(RaspiPlayer player, InventoryClickEvent clickEvent) {
        if (isItem(clickEvent.getCurrentItem(), -21)) {
            //RANDOM MACHEN
            traderDB.setRandom(TraderCommand.traderEditContainer.get(player.getUUID()), true);
            player.getPlayer().closeInventory();
            player.sendDebugMessage(TraderCommand.traderEditContainer.get(player.getUUID()) + " ist jetzt Random.");
            TraderCommand.traderEditContainer.remove(player.getUUID());
        }

        if (isItem(clickEvent.getCurrentItem(), -20)) {
            //RANDOM REVOKE
            traderDB.setRandom(TraderCommand.traderEditContainer.get(player.getUUID()), false);
            player.getPlayer().closeInventory();
            player.sendDebugMessage(TraderCommand.traderEditContainer.get(player.getUUID()) + " ist jetzt nicht mehr Random.");
            TraderCommand.traderEditContainer.remove(player.getUUID());
        }
    }

    private boolean isItem(ItemStack stack, int modelID) {
        return stack != null && stack.hasItemMeta() && stack.getItemMeta().hasCustomModelData() && stack.getItemMeta().getCustomModelData() == modelID;
    }


    private void clickedSave(InventoryClickEvent clickEvent, RaspiPlayer player, ItemStack stack) {
        if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasCustomModelData() && stack.getItemMeta().getCustomModelData() == 0) {
            String trader = TraderCommand.traderEditContainer.get(player.getUUID());
            ItemStack result = clickEvent.getInventory().getItem(12);
            ItemStack buy1 = clickEvent.getInventory().getItem(9);
            ItemStack buy2 = clickEvent.getInventory().getItem(10);
            if (result != null && buy1 != null) {
                if (TraderCommand.traderSAVEContainer.containsKey(player.getUUID())) {
                    traderDB.setItemToShop(trader, TraderCommand.traderSAVEContainer.get(player.getUUID()), result, buy1, buy2);
                } else {
                    traderDB.addItemToShop(trader, result, buy1, buy2);
                }

            } else {
                player.sendMessage("<red>Bitte gehe sicher das ALLE items für ein Rezept vorhanden sind! (Kauf und Verkauf Items)");
            }
            player.sendDebugMessage("Recipe: SAVED");
            clickEvent.getWhoClicked().closeInventory();
            TraderCommand.traderEditContainer.remove(player.getUUID());
            TraderCommand.traderSAVEContainer.remove(player.getUUID());

        }
    }

    private void clickedDelete(InventoryClickEvent clickEvent, RaspiPlayer player, ItemStack stack) {
        if (stack != null && stack.getType().equals(Material.BARRIER)&&stack.hasItemMeta() && stack.getItemMeta().hasCustomModelData() && stack.getItemMeta().getCustomModelData() == 1) {
            String trader = TraderCommand.traderEditContainer.get(player.getUUID());
            Integer id = TraderCommand.traderSAVEContainer.get(player.getUUID());
            traderDB.removeRecipe(trader, id);
            clickEvent.getWhoClicked().closeInventory();
            player.sendDebugMessage("Recipe: DELETED");
        }
    }


    private Merchant generateMerchant(String traderType) {
        Merchant merchant = Bukkit.createMerchant();
        List<MerchantRecipe> recipes = new ArrayList<>();

        //RANDOM
        if (traderDB.isRandom(traderType)) {
            List<String> shopID = traderDB.getShopIds(traderType);
            int size = shopID.size();

            int id = plugin.getRandom().nextInt(size);

            MerchantRecipe merchantRecipe = new MerchantRecipe(traderDB.getItemStack(traderType, TraderDB.DB_SHOP_RES, Integer.parseInt(shopID.get(id))), 999);
            merchantRecipe.addIngredient(traderDB.getItemStack(traderType, TraderDB.DB_SHOP_ITEM_1, Integer.parseInt(shopID.get(id))));
            if (traderDB.shopItemExist(traderType, shopID.get(id), TraderDB.DB_SHOP_ITEM_2)) {
                merchantRecipe.addIngredient(traderDB.getItemStack(traderType, TraderDB.DB_SHOP_ITEM_2, Integer.parseInt(shopID.get(id))));
            }
            recipes.add(merchantRecipe);
            merchant.setRecipes(recipes);
            return merchant;
        }


        for (String ids : traderDB.getShopIds(traderType)) {
            MerchantRecipe merchantRecipe = new MerchantRecipe(traderDB.getItemStack(traderType, TraderDB.DB_SHOP_RES, Integer.parseInt(ids)), 999);
            merchantRecipe.addIngredient(traderDB.getItemStack(traderType, TraderDB.DB_SHOP_ITEM_1, Integer.parseInt(ids)));
            if (traderDB.shopItemExist(traderType, ids, TraderDB.DB_SHOP_ITEM_2)) {
                merchantRecipe.addIngredient(traderDB.getItemStack(traderType, TraderDB.DB_SHOP_ITEM_2, Integer.parseInt(ids)));
            }
            recipes.add(merchantRecipe);
        }
        merchant.setRecipes(recipes);
        return merchant;
    }


}
