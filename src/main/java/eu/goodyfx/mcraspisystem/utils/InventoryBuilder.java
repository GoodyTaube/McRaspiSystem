package eu.goodyfx.mcraspisystem.utils;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryBuilder {

    private final Inventory inventory;

    public InventoryBuilder(String title, int size) {
        this.inventory = Bukkit.createInventory(null, size, MiniMessage.miniMessage().deserialize(title));
    }

    public InventoryBuilder addItem(ItemStack stack) {
        inventory.addItem(stack);
        return this;
    }

    public InventoryBuilder setItem(int slot, ItemStack stack) {
        inventory.setItem(slot, stack);
        return this;
    }

    public Inventory build() {
        return this.inventory;
    }

    public InventoryBuilder addBack() {
        setItem(inventory.getSize()-1, new ItemBuilder(Material.SLIME_BALL).displayName("<red>Zurück").setModelID(1).build());

        return this;
    }


}
