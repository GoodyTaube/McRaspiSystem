package eu.goodyfx.system.lootchest.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.core.utils.Raspi;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class LootChestCommand {

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("lootchest").executes(LootChestCommand::helpList).build();
    }

    public static int helpList(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            return Command.SINGLE_SUCCESS;
        }

        Raspi.players().withOnlinePlayer(player, raspiPlayer1 -> {
            raspiPlayer1.sendDebugMessage("Eine Hilfeliste kommt bald.");

        });

        return Command.SINGLE_SUCCESS;
    }


}
