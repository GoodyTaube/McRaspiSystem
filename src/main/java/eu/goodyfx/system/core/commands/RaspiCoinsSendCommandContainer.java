package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.core.database.RaspiSuggestions;
import eu.goodyfx.system.core.events.RaspiCoinsEvents;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import eu.goodyfx.system.core.utils.RaspiSounds;
import eu.goodyfx.system.core.utils.Transaction;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class RaspiCoinsSendCommandContainer {

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("sendcoins")
                .then(Commands.argument("player", StringArgumentType.string()).suggests((context, builder) -> RaspiSuggestions.suggestOfflinePlayer(builder)).then(Commands.argument("value", IntegerArgumentType.integer(1, 999)).executes(RaspiCoinsSendCommandContainer::exc))).build();
    }

    private static int exc(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player dummy)) {
            return Command.SINGLE_SUCCESS;
        }
        RaspiPlayer player = Raspi.players().get(dummy);
        String target = context.getArgument("player", String.class);
        int value = context.getArgument("value", Integer.class);
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(target);
        if (!offlinePlayer.hasPlayedBefore()) {
            player.sendMessage("Der Spieler hat noch nicht gespielt.");
            player.playSound(RaspiSounds.ERROR);
            return Command.SINGLE_SUCCESS;
        }

        Transaction transaction = new Transaction(player.getUUID(), offlinePlayer.getUniqueId(), context.getArgument("value", Integer.class), 5);
        RaspiCoinsEvents.getTransactions().add(transaction);
        player.sendMessage(String.format("<green>Du hast eine Transaction in höhe von: <aqua>%s RC <green>an: <aqua>%s <green>gesendet.<br><gray><italic>Diese Transaction kostet dich %s RC!", value, target, transaction.getCost()), true);
        player.playSound(RaspiSounds.SUCCESS);
        return Command.SINGLE_SUCCESS;
    }

}
