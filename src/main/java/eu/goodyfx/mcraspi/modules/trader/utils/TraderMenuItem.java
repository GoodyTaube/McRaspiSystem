package eu.goodyfx.mcraspi.modules.trader.utils;

import eu.goodyfx.mcraspi.core.utils.ItemBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum TraderMenuItem {

    RECIPE_SAVE(Material.SLIME_BLOCK, 1, "<green>Speichern", "<green>Speichere das Rezept"),
    RECIPE_REMOVE(Material.BARRIER, 2, "<green>Löschen", "Lösche dieses Rezept", "<red><b>PERMANENT"),
    INFO_RECIPE(Material.MAGENTA_GLAZED_TERRACOTTA, 0, "<gray>Wird zu --> "),
    INFO_RECIPE_SELLING(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 0, "Item zum Kaufen", "Hier bitte das Item,", "was gekauft werden soll."),
    INFO_RECIPE_BUY_WITH(Material.GREEN_STAINED_GLASS_PANE, 0, "Item zum Bezahlen", "Hier bitte das Item,", "was zum Bezahlen verwendet werden soll."),

    SETTING_RANDOM_SET(Material.REDSTONE, 1, "Random Machen", "Mache den Trader Random", "Trader würfelt beim öffnen ein Rezept"),
    SETTING_RANDOM_REMOVE(Material.GLOWSTONE, 2, "Normal Machen", "Mache den Trader Normal", "Trader würfelt beim öffnen <underlined>kein</underlined> Rezept"),
    SETTING_ADD_RECIPE(Material.GOLD_INGOT, 3, "Rezept Hinzufügen", "Erstelle ein neues Verkaufs Item");


    private final int id;
    private final String name;
    private final String[] lore;
    private final Material type;

    TraderMenuItem(Material material, Integer id, String name, String... info) {
        this.id = id;
        this.name = name;
        this.lore = info;
        this.type = material;
    }

    public ItemStack getItem() {
        List<Component> loreList = new ArrayList<>();
        for (String lore : lore) {
            loreList.add(MiniMessage.miniMessage().deserialize(lore));
        }
        return new ItemBuilder(this.type).displayName(this.name).lore(loreList).build();
    }

    public static boolean isMenuItem(int id) {
        return Arrays.stream(values()).anyMatch(menuItem -> menuItem.id == id);
    }
}
