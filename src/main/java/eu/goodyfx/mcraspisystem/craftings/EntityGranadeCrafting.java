package eu.goodyfx.mcraspisystem.craftings;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class EntityGranadeCrafting {

    private static final NamespacedKey key = new NamespacedKey(JavaPlugin.getPlugin(McRaspiSystem.class), "craftingGranade");

    public EntityGranadeCrafting() {
        Bukkit.addRecipe(generate());
    }

    public ShapelessRecipe generate() {

        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(key, new ItemBuilder(Material.SNOWBALL).build());
        shapelessRecipe.addIngredient(new ItemBuilder(Material.CRAFTING_TABLE).displayName("TEST").build());
        return shapelessRecipe;

    }

}
