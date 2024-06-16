package eu.goodyfx.mcraspisystem.utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LootItemManager {

    private ItemMeta meta;
    private ItemStack stack;

    public LootItemManager(ItemStack stack) {
        this.meta = stack.getItemMeta();
        this.stack = stack;
    }

    public boolean isItem(Powers powers) {
        if (meta.hasCustomModelData()) {
            return meta.getCustomModelData() == powers.getId();
        } else
            return false;
    }

    public boolean isSpecial() {
        return this.meta.hasCustomModelData();
    }

    public LootItemManager removeEnchant() {
        stack.removeEnchantment(Enchantment.FIRE_ASPECT);
        return this;
    }

    public LootItemManager addEnchant() {
        stack.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
        return this;
    }


}
