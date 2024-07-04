package eu.goodyfx.mcraspisystem.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Item {

    private final ItemStack stack;
    private final int id;

    private final Material material;

    public Item(Material material, int id) {
        this.stack = new ItemBuilder(material).setModelID(id).build();
        this.material = material;
        this.id = id;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public int getId() {
        return id;
    }

    public Material getMaterial() {
        return this.material;
    }
}
