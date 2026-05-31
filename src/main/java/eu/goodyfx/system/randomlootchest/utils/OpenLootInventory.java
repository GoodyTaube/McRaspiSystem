package eu.goodyfx.system.randomlootchest.utils;

import eu.goodyfx.system.core.utils.RaspiFormatting;
import eu.goodyfx.system.randomlootchest.RandomLootChest;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Random;

public class OpenLootInventory {
    private final RandomLootChest system;

    public OpenLootInventory(RandomLootChest randomLootChest) {
        this.system = randomLootChest;
    }

    public ItemStack getRandomItem() {
        int all = system.getItems().size();
        if (all != 0) {
            Random random = system.getPlugin().getRandom();
            int randomInt = random.nextInt(all);
            return system.getItems().get(randomInt);
        } else {
            return new ItemStack(Material.AIR);
        }
    }

    public int findavaliablerandomSlot(Inventory inv) {
        int size = inv.getSize();
        int current = 1000;

        for (int i = 0; i < size; ++i) {
            Random random = system.getPlugin().getRandom();
            int randomInt = random.nextInt(size);
            if (inv.getItem(randomInt) == null) {
                current = randomInt;
                break;
            }
        }
        return current;
    }

    public void openInvenory(Player player) {
        String invName = RaspiFormatting.formattingChatMessage(Objects.requireNonNull(system.getConfigManager().getConfig().getString("Inventory_Name")));
        int itemAmount = system.getConfigManager().getConfig().getInt("ItemAmountToAdd");
        int slots = system.getConfigManager().getConfig().getInt("Inventory_Slots");
        Inventory inv = system.getPlugin().getServer().createInventory(null, slots, MiniMessage.miniMessage().deserialize(invName));
        for (int i = 0; i < itemAmount; ++i) {
            if (this.findavaliablerandomSlot(inv) != 1000) {
                int slot = this.findavaliablerandomSlot(inv);
                inv.setItem(slot, this.getRandomItem());
            }
        }

        player.openInventory(inv);
    }

}
