package eu.goodyfx.mcraspisystem.commands.subcommands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.SubCommand;
import eu.goodyfx.mcraspisystem.tasks.AnimationBlockDisplay;
import eu.goodyfx.mcraspisystem.utils.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AdminLootChestSubCommand extends SubCommand {

    private final McRaspiSystem plugin;

    public AdminLootChestSubCommand(McRaspiSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getLabel() {
        return "lootChest";
    }

    @Override
    public String getDescription() {
        return "Ein Command um die LootChest auf dem Server zu steuern.";
    }

    @Override
    public String getSyntax() {
        return "/admin lootChest <open|generate|menu>";
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if (args.length == 2 && args[1].equalsIgnoreCase("menu")) {
            Inventory inv = Bukkit.createInventory(null, 9, MiniMessage.miniMessage().deserialize("<green>LootChest - Menu"));
            ItemStack selten = new ItemBuilder(LootChestMenuItems.SELTEN.getType()).displayName(LootChestMenuItems.SELTEN.getTitle()).build();
            ItemStack nichtSoSelten = new ItemBuilder(LootChestMenuItems.NICHT_SO_SELTEN.getType()).displayName(LootChestMenuItems.NICHT_SO_SELTEN.getTitle()).build();
            ItemStack immer = new ItemBuilder(LootChestMenuItems.IMMER.getType()).displayName(LootChestMenuItems.IMMER.getTitle()).build();
            inv.setItem(2, selten);
            inv.setItem(4, nichtSoSelten);
            inv.setItem(6, immer);
            player.getPlayer().openInventory(inv);
        } else if (args.length == 2 && args[1].equalsIgnoreCase("generate")) {
            new LootChest(plugin, player.getLocation());
        } else if (args.length == 2 && args[1].equalsIgnoreCase("open")) {
            new LootChestLoot(plugin).openLoot(player);
        } else if (args.length == 2 && args[1].equalsIgnoreCase("kill")) {
            for (LootChest lootChest : plugin.getLootChestTimer().getLootChestDisplay()) {
                lootChest.killAll();
            }
            plugin.getLootChestTimer().getLootChestDisplay().clear();
            AnimationBlockDisplay.getBlockDisplayList().clear();
        }

        return true;
    }


}
