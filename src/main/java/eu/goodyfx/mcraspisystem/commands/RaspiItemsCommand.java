package eu.goodyfx.mcraspisystem.commands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.utils.ItemBuilder;
import eu.goodyfx.mcraspisystem.utils.LootItems;
import eu.goodyfx.mcraspisystem.utils.RaspiMessages;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class RaspiItemsCommand implements CommandExecutor {

    //TODO RASPI COIN STACK

    private final RaspiMessages messages;

    public RaspiItemsCommand(McRaspiSystem plugin) {
        plugin.setCommand("loot", this);
        this.messages = plugin.getModule().getRaspiMessages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 2 && args[0].equalsIgnoreCase("get") && args[1].equalsIgnoreCase("all")) {
                Inventory inventory = Bukkit.createInventory(null, InventoryType.CHEST, MiniMessage.miniMessage().deserialize("Raspi-Items"));

                AtomicInteger sizer = new AtomicInteger();
                for (LootItems per : LootItems.values()) {
                    ItemBuilder builder = new ItemBuilder(per.getMaterial());
                    builder.displayName(per.getLabel());
                    builder.setModelID(per.getModelID());
                    if (per.getLore() != null) {
                        per.getLore().forEach(builder::addLore);
                    }
                    if (per.getEnchantment() != null) {
                        builder.addEnchantment(per.getEnchantment(), 1, true);
                    }
                    inventory.setItem(sizer.getAndAdd(1), builder.build());

                }

                player.openInventory(inventory);

            } else {
                player.sendRichMessage(messages.getUsage("/loot get all"));
            }
        }
        return false;
    }
}
