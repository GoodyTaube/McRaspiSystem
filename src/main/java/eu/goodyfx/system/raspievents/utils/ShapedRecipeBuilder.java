package eu.goodyfx.system.raspievents.utils;

import eu.goodyfx.system.McRaspiSystem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.bukkit.plugin.java.JavaPlugin;

public class ShapedRecipeBuilder {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    private final ShapedRecipe recipe;

    public ShapedRecipeBuilder(ItemStack result, String nameSpaced) {
        this.recipe = new ShapedRecipe(plugin.getNameSpaced(nameSpaced), result);
    }

    public ShapedRecipeBuilder shape(String... shape) {
        recipe.shape(shape);
        return this;
    }

    public ShapedRecipeBuilder setIngredient(Character character, Material material) {
        recipe.setIngredient(character, material);
        return this;
    }

    public ShapedRecipeBuilder setIngredient(Character character, ItemStack material) {
        recipe.setIngredient(character, material);
        return this;
    }

    public ShapedRecipeBuilder setCategory(CraftingBookCategory category) {
        recipe.setCategory(category);
        return this;
    }

    public ShapedRecipe build() {
        return recipe;
    }


}
