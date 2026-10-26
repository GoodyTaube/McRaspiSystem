package eu.goodyfx.mcraspi.modules.trader.inventories;

import eu.goodyfx.mcraspi.core.utils.ItemBuilder;
import eu.goodyfx.mcraspi.modules.trader.utils.Trader;
import eu.goodyfx.mcraspi.modules.trader.utils.TraderMenuItem;
import eu.goodyfx.mcraspi.modules.trader.utils.TraderRecipe;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class TraderSettings implements InventoryHolder {

    private Inventory inventory;
    private final Trader trader;
    private final Plugin plugin;
    private final NamespacedKey recipeIDKey;
    private final Map<Integer, TraderMenuItem> slotContainer = new ConcurrentHashMap<>();
    private final List<ItemStack> recipeContainer = new ArrayList<>();

    public TraderSettings(Plugin plugin, Trader trader) {
        this.recipeIDKey = new NamespacedKey(plugin, "recipeID");
        this.plugin = plugin;
        this.trader = trader;
        fillContents();
    }

    public void reloadContents() {
        inventory.clear();
        fillContents();
    }

    private void fillContents() {
        int shopSize = trader.getRecipes().size();
        int rows = Math.clamp((shopSize / 9) + 1, 1, 6);
        int invSize = rows * 9;
        this.inventory = plugin.getServer().createInventory(this, invSize, MiniMessage.miniMessage().deserialize(String.format("<green>%s <white>| <dark_gray>Settings", trader.getName())));

        TraderMenuItem recipeAdd = TraderMenuItem.SETTING_ADD_RECIPE;
        TraderMenuItem randomMaker = trader.isRandom() ? TraderMenuItem.SETTING_RANDOM_REMOVE : TraderMenuItem.SETTING_RANDOM_SET;
        slotContainer.put(inventory.getSize() - 1, recipeAdd);
        slotContainer.put(inventory.getSize() - 2, randomMaker);

        for (TraderRecipe recipe : trader.getRecipes()) {
            Component displayNameResult = recipe.getResult().displayName();
            ItemStack recipeDummy = new ItemBuilder(Material.PAPER).displayName(displayNameResult).addLore("ID: '" + recipe.getId() + "'").addLore("<gray>Klicke zum Editieren").build();
            ItemMeta meta = recipeDummy.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(recipeIDKey, PersistentDataType.INTEGER, recipe.getId());
            recipeDummy.setItemMeta(meta);
            recipeContainer.add(recipeDummy);
            inventory.addItem(recipeDummy);
        }

        for (Map.Entry<Integer, TraderMenuItem> slot : slotContainer.entrySet()) {
            inventory.setItem(slot.getKey(), slot.getValue().getItem());
        }

    }


    public TraderMenuItem getItemBySlot(int slotID) {
        return slotContainer.get(slotID);
    }

}
