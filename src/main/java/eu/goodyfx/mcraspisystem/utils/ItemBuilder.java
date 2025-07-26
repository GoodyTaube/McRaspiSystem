package eu.goodyfx.mcraspisystem.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ItemBuilder {

    private final ItemStack stack;
    private final ItemMeta meta;
    private List<Component> lore;

    public ItemBuilder(Material material) {
        this.stack = new ItemStack(material, 1);
        this.meta = stack.getItemMeta();
        this.lore = new ArrayList<>();
    }

    public ItemBuilder(ItemStack stack) {
        this.stack = stack;
        this.meta = stack.getItemMeta();
        this.lore = new ArrayList<>();
    }


    public ItemBuilder setAmount(int amount) {
        if (amount > 64) {
            amount = 64;
        }
        stack.setAmount(amount);
        return this;
    }

    public ItemBuilder displayName(Component val) {
        meta.displayName(val);
        return this;
    }

    public ItemBuilder displayName(String val) {
        meta.displayName(MiniMessage.miniMessage().deserialize(val));
        return this;
    }

    public ItemBuilder lore(List<Component> lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder addLore(Component val) {
        lore.add(val);
        return this;
    }

    public ItemBuilder addLore(String val) {
        lore.add(MiniMessage.miniMessage().deserialize(val));
        return this;
    }

    public void addEnchantment(Enchantment enchantment, int level, boolean ignoreLimit) {
        if (ignoreLimit) {
            stack.addUnsafeEnchantment(enchantment, level);
        } else {
            stack.addEnchantment(enchantment, level);
        }
    }

    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantmentIntegerMap) {
        stack.addEnchantments(enchantmentIntegerMap);
        return this;
    }

    public ItemBuilder setModelID(int modelID) {
        meta.setCustomModelData(modelID);
        return this;
    }

    public ItemStack build() {
        meta.lore(lore);
        stack.setItemMeta(meta);
        return stack;
    }


    public ItemMeta getMeta() {
        return this.meta;
    }

}
