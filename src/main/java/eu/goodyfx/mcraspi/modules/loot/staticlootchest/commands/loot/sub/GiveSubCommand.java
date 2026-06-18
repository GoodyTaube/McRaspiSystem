package eu.goodyfx.mcraspi.modules.loot.staticlootchest.commands.loot.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.commands.RaspiSubCommand;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.core.utils.ItemBuilder;
import eu.goodyfx.mcraspi.modules.loot.staticlootchest.utils.LootItems;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.concurrent.atomic.AtomicInteger;

public class GiveSubCommand implements RaspiSubCommand {
    @Override
    public ArgumentBuilder<CommandSourceStack, ?> build() {
        return Commands.literal("give").then(Commands.argument("item", StringArgumentType.word()).suggests(suggestStrings("all")).executes(this::execute));
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer((Player) context.getSource().getSender());
        Inventory inventory = Bukkit.createInventory(null, InventoryType.CHEST, MiniMessage.miniMessage().deserialize("Loot - Items"));

        AtomicInteger sizer = new AtomicInteger();
        for (LootItems per : LootItems.values()) {
            ItemBuilder builder = new ItemBuilder(per.getType());
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

        raspiPlayer.openInventory(inventory);
        return Command.SINGLE_SUCCESS;
    }

}
