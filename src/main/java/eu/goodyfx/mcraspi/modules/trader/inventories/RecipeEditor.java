package eu.goodyfx.mcraspi.modules.trader.inventories;

import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.core.utils.ItemBuilder;
import eu.goodyfx.mcraspi.modules.trader.TraderSubSystem;
import eu.goodyfx.mcraspi.modules.trader.utils.Trader;
import eu.goodyfx.mcraspi.modules.trader.utils.TraderMenuItem;
import eu.goodyfx.mcraspi.modules.trader.utils.TraderRecipe;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class RecipeEditor implements InventoryHolder {

    private final Inventory inventory;
    private final Trader trader;
    private final Integer id;
    private Map<Integer, TraderMenuItem> slotItems = new ConcurrentHashMap<>();

    public RecipeEditor(TraderSubSystem plugin, Trader trader, Integer id) {
        this.inventory = plugin.getPlugin().getServer().createInventory(this, 18, MiniMessage.miniMessage().deserialize(String.format("<green>%s <white>| <gray>Recipe Editor ID:<aqua>%s", trader.getName(), id)));
        this.trader = trader;
        this.id = id;
        fillView();
        fillContents();
    }

    /**
     * Check if current Recipe is available to edit and add Items
     */
    private void fillContents() {
        if (trader.getRecipeByID(id) != null) {
            TraderRecipe recipe = trader.getRecipeByID(id);
            inventory.setItem(9, recipe.getCurrency_slot1());
            inventory.setItem(10, recipe.getCurrency_slot2());
            inventory.setItem(12, recipe.getResult());
        }
    }


    /**
     * Filling up the UI with UI buttons
     */
    private void fillView() {
        ItemStack filler = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName(" ").build();
        slotItems.put(0, TraderMenuItem.INFO_RECIPE_BUY_WITH);
        slotItems.put(1, TraderMenuItem.INFO_RECIPE_BUY_WITH);
        slotItems.put(2, TraderMenuItem.INFO_RECIPE);
        slotItems.put(3, TraderMenuItem.INFO_RECIPE_SELLING);
        slotItems.put(6, TraderMenuItem.RECIPE_REMOVE);
        slotItems.put(17, TraderMenuItem.RECIPE_SAVE);
        setItems(slotItems);
        fillOut(filler);
    }

    /**
     * FillOut Inventory with filler item and make place for recipe
     * {@link RecipeEditor#fillContents()} may override Free Places.
     *
     * @param filler The filler Items
     */
    private void fillOut(ItemStack filler) {
        for (int i = 0; i < inventory.getSize(); i++) {
            this.inventory.setItem(i, filler);
        }

        for (Map.Entry<Integer, TraderMenuItem> slots : slotItems.entrySet()) {
            this.inventory.setItem(slots.getKey(), slots.getValue().getItem());
        }
        for (int slot : new int[]{9, 10, 12}) {
            this.inventory.setItem(slot, null);
        }
    }


    private void setItems(Map<Integer, TraderMenuItem> items) {
        for (Map.Entry<Integer, TraderMenuItem> slot : items.entrySet()) {
            this.inventory.setItem(slot.getKey(), slot.getValue().getItem());
        }
    }

    public void saveRecipe(RaspiPlayer raspiPlayer) {

        ItemStack result = inventory.getItem(12);
        ItemStack shop1 = inventory.getItem(9);
        ItemStack shop2 = inventory.getItem(10);

        if (result == null || shop1 == null) {
            raspiPlayer.getPlayer().closeInventory();
            raspiPlayer.sendMessage("<red>Du solltest alle Items Hinzufügen.", true);
            return;
        }
        trader.setRecipe(new TraderRecipe(id, result, shop1, shop2));
    }

    public TraderMenuItem getItemBySlot(int slotID) {
        return slotItems.get(slotID);
    }

}
