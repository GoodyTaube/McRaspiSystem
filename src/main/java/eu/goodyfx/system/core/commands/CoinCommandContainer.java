package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.database.RaspiPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class CoinCommandContainer {

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("coins").executes(context -> {
            if (!(context.getSource().getSender() instanceof Player player)) {
                context.getSource().getSender().sendRichMessage("Dein Kontostand: ∞ RC (bitte nicht ausgeben)");
                return Command.SINGLE_SUCCESS;
            }
            RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
            raspiPlayer.sendMessage(String.format("<gray>Dein Kontostand: <aqua>%s <gray>RC", raspiPlayer.userData().getCoins()), true);
            return Command.SINGLE_SUCCESS;
        }).build();
    }

}
