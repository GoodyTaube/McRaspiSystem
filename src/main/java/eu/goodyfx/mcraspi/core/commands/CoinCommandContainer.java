package eu.goodyfx.mcraspi.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.core.database.RaspiSuggestions;
import eu.goodyfx.mcraspi.core.utils.Transaction;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CoinCommandContainer {

    private final static String PLAYER_NOT_PLAYED = "<red>%s hat noch nicht bei uns gespielt!";
    private final static String FAIL_COINS_AMOUNT = "<red>Du hast nicht genug coins! <yellow>%s/%s";
    private final static String SUCCESS_COINS_SEND = "Du hast erfolgreich eine Transaction gestartet. <gray><italic>Transaktions Kosten: %s";
    private final static String SUCCESS_COINS_SEND_HOVER = "<hover:show_text:'%s an %s --> %s'>";

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("coins").executes(CoinCommandContainer::coinsCommand).then(Commands.literal("send").then(Commands.argument("player", StringArgumentType.string())
                .suggests(((context, builder) -> RaspiSuggestions.suggestOfflinePlayer(builder))).then(Commands.argument("amount", IntegerArgumentType.integer(1, 9999)).executes(CoinCommandContainer::coinsSendCommand)))
        ).build();
    }

    private static int coinsCommand(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            context.getSource().getSender().sendRichMessage("Dein Kontostand: ∞ VC (bitte nicht ausgeben)");
            return 1;
        }
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        raspiPlayer.sendMessage(String.format("<gray>Dein Kontostand: <aqua>%s <gray>VC", raspiPlayer.userData().getCoins()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int coinsSendCommand(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            context.getSource().getSender().sendRichMessage("NOPE");
            return 1;
        }
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        long playerCoins = raspiPlayer.userData().getCoins();
        int amount = context.getArgument("amount", Integer.class);
        String name = context.getArgument("player", String.class);
        OfflinePlayer target = Bukkit.getOfflinePlayer(name);

        if (!target.hasPlayedBefore()) {
            raspiPlayer.sendMessage(String.format(PLAYER_NOT_PLAYED, name), true);
            return 1;
        }

        if (!(amount < playerCoins)) {
            raspiPlayer.sendMessage(String.format(FAIL_COINS_AMOUNT, playerCoins, amount), true);
            return 1;
        }

        Transaction transaction = new Transaction(player.getUniqueId(), target.getUniqueId(), amount, 10);
        int cost = (int) transaction.getCost();

        String hover = String.format(SUCCESS_COINS_SEND_HOVER, player.getName(), target.getName(), amount);
        raspiPlayer.sendMessage(String.format(hover + SUCCESS_COINS_SEND, cost), true);
        Raspi.debugger().debug(String.format("Started Transaction! %s -> %s : %s", player.getName(), target.getName(), amount));
        return Command.SINGLE_SUCCESS;
    }


}
