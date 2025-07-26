package eu.goodyfx.system.raspievents.craftings;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

@Getter

public class CanabolaCraftging {

    private static final NamespacedKey key = new NamespacedKey(JavaPlugin.getPlugin(McRaspiSystem.class), "canabolarecipe");
    private static final String name = "Sopora herba - <i>('Vergessens-Kraut')";
    private static final String display = "<green>Raspi-Event <b>2025</b> - 1 // <i>Crafting Recipes";
    public static final int duration = 20 * 60 * 5;

    public CanabolaCraftging(McRaspiSystem plugin) {
        Bukkit.addRecipe(buildRecipe());
    }


    public static ShapedRecipe buildRecipe() {
        ItemStack stack = new ItemBuilder(Material.TALL_DRY_GRASS).displayName(display).addLore("<gold>" + name)
                .addLore("<gray>Ein uraltes Heilkraut,")
                .addLore("<gray>das Körper und Geist beruhigt.")
                .addLore("<gray>In Tinkturen genutzt von Alchemisten.")
                .addLore("<gray>Auch als 'Kraut der Stille' bekannt.")
                .setModelID(1).build();
        ShapedRecipe recipe = new ShapedRecipe(key, stack);

        recipe.shape("DDD", "OGO", "QPQ");
        recipe.setIngredient('D', Material.DEAD_BUSH);
        recipe.setIngredient('O', Material.FERN);
        recipe.setIngredient('G', Material.SHORT_GRASS);
        recipe.setIngredient('Q', Material.QUARTZ);
        recipe.setIngredient('P', Material.PRISMARINE_CRYSTALS);


        return recipe;
    }


}
