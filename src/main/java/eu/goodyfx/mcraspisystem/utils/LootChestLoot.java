package eu.goodyfx.mcraspisystem.utils;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootChestLoot {

    private final McRaspiSystem plugin;
    private final Random random = new Random();

    public LootChestLoot(McRaspiSystem plugin) {
        this.plugin = plugin;
    }

    public void openLoot(RaspiPlayer player) {
        Inventory inventory = new InventoryBuilder("Loot", InventoryType.CHEST.getDefaultSize()).build();

        List<ItemStack> selten = generateItems(LootChestMenuItems.SELTEN);
        List<ItemStack> nichtSelten = generateItems(LootChestMenuItems.NICHT_SO_SELTEN);
        List<ItemStack> immer = generateItems(LootChestMenuItems.IMMER);
        List<List<ItemStack>> combined = new ArrayList<>();
        combined.add(selten);
        combined.add(nichtSelten);
        combined.add(immer);

        player.getPlayer().openInventory(addItems(combined, inventory));
    }

    private Inventory addItems(List<List<ItemStack>> items, Inventory inventory) {
        for (List<ItemStack> cat : items) {
            for (ItemStack stack : cat) {
                inventory.addItem(stack);
            }
        }
        return inventory;
    }

    private List<ItemStack> generateItems(LootChestMenuItems lootChestType) {
        List<ItemStack> avibalItems = new ArrayList<>(plugin.getModule().getLootChestManager().getItems(lootChestType));
        List<ItemStack> returnList = new ArrayList<>();
        switch (lootChestType) {
            case IMMER:
                returnList.addAll(getItems(5, 2, avibalItems));
                break;
            case NICHT_SO_SELTEN:
                returnList.addAll(getItems(3, 1, avibalItems));
                break;
            case SELTEN:
                returnList.addAll(getItems(1, 0, avibalItems));
                break;
        }
        return returnList;
    }

    private List<ItemStack> getItems(int itemMax, int minItem, List<ItemStack> items) {
        int rounds = random.nextInt(itemMax + 1 - minItem) + minItem;
        List<ItemStack> returnList = new ArrayList<>();
        for (int i = 0; i < rounds; i++) {
            returnList.add(items.get(random.nextInt(items.size())));
        }
        return returnList;
    }

}
