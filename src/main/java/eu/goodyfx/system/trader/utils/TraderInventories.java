package eu.goodyfx.system.trader.utils;

import eu.goodyfx.system.core.utils.InventoryBuilder;
import eu.goodyfx.system.core.utils.ItemBuilder;
import eu.goodyfx.system.trader.managers.TraderDB;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraderInventories {

    public static Inventory createRecipeInventory() {
        //OPEN INV TO PLACE ORDER
        ItemStack delete = new ItemBuilder(Material.BARRIER).displayName("<red>Rezept Löschen").setModelID(1).addLore("Lösche dieses Rezept").addLore("<red><b>PERMANENT").build();
        ItemStack buy = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("<green>Item zum Bezahlen.").addLore("<green>Bezahl Item").addLore("max 64x Item").build();
        ItemStack sell = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).displayName("<green>Item zum Kaufen.").addLore("<green>Kauf Item").addLore("max 64x Item").build();
        ItemStack arrow = new ItemBuilder(Material.MAGENTA_GLAZED_TERRACOTTA).displayName("<gray>Wird zu-->").build();
        ItemStack save = new ItemBuilder(Material.SLIME_BLOCK).displayName("<green>Speichern.").setModelID(0).addLore("<green>Speichere das Rezept.").build();

        ItemStack filler = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName(" ").build();
        Map<Integer, ItemStack> itemContainer = new HashMap<>();
        itemContainer.put(0, buy);
        itemContainer.put(1, buy);
        itemContainer.put(2, arrow);
        itemContainer.put(3, sell);
        itemContainer.put(6, delete);
        itemContainer.put(17, save);
        return new InventoryBuilder("Trader Settings", 18).filler(filler, 9, 10, 12).setItems(itemContainer).build();
    }

    public static Inventory traderMenu(TraderDB traderDB, String trader) {
        List<String> shopRecipes = traderDB.getShopIds(trader);
        Inventory inventory;
        if (shopRecipes.size() < 9) {
            inventory = new InventoryBuilder("Trader Rezepte", 9).build();
        } else {
            inventory = new InventoryBuilder("Trader Rezepte", InventoryType.CHEST.getDefaultSize()).build();
        }

        //Rezept Hinzufügen Item
        ItemStack stack = new ItemBuilder(Material.GOLD_INGOT).setModelID(-1).displayName("<green><b>Rezept Hinzufügen").build();
        ItemStack setRandom = null;

        ItemStack setBanker = null;
        if (!traderDB.isBanker(trader)) {
            setBanker = new ItemBuilder(Material.CLOCK).setModelID(-26).displayName("<green>Zum Banker machen.").build();
        } else if (traderDB.isBanker(trader)) {
            setBanker = new ItemBuilder(Material.BARRIER).setModelID(-25).displayName("<green>Normal machen.").build();
        }


        if (traderDB.isRandom(trader)) {
            setRandom = new ItemBuilder(Material.GOLD_INGOT).setModelID(-20).displayName("<green><b>Random Entfernen").build();
        } else if (!traderDB.isRandom(trader)) {
            setRandom = new ItemBuilder(Material.GOLD_NUGGET).setModelID(-21).displayName("<green><b>Random Machen").build();
        }

        for (String id : traderDB.getShopIds(trader)) {
            //Rezepte Anzeigen im Inventar
            ItemStack result = traderDB.getItemStack(trader, TraderDB.DB_SHOP_RES, Integer.parseInt(id));
            ItemBuilder itemBuilder = new ItemBuilder(Material.PAPER).setModelID(Integer.parseInt(id));
            itemBuilder.addLore("Verkauf: " + result.getType().name());
            if (result.hasItemMeta() && result.getItemMeta().hasDisplayName()) {
                itemBuilder.addLore(result.displayName());
            }
            itemBuilder.displayName("<gray>Rezept: <aqua>" + id);
            inventory.addItem(itemBuilder.build());
        }
        inventory.setItem(inventory.getSize() - 1, stack);
        inventory.setItem(inventory.getSize() - 2, setRandom);
        inventory.setItem(inventory.getSize() - 3, setBanker);


        return inventory;
    }


}
