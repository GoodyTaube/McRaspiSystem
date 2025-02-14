package eu.goodyfx.mcraspisystem.utils;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

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

    public InventoryBuilder setItems(Map<Integer, ItemStack> items) {
        for (Map.Entry<Integer, ItemStack> slot : items.entrySet()) {
            inventory.setItem(slot.getKey(), slot.getValue());
        }
        return this;
    }

    public InventoryBuilder filler() {
        ItemStack filler = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName(" ").build();
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, filler);
        }
        return this;
    }

    public InventoryBuilder filler(ItemStack stack) {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, stack);
        }
        return this;
    }

    public InventoryBuilder filler(ItemStack stack, int... freeSlots) {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, stack);
        }
        for (int slot : freeSlots) {
            inventory.setItem(slot, null);
        }
        return this;
    }


    public InventoryBuilder addBack() {
        setItem(inventory.getSize() - 1, new ItemBuilder(Material.SLIME_BALL).displayName("<red>Zurück").setModelID(1).build());

        return this;
    }


}
