package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class CoinStatusCommandContainer {

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("coins").executes(context -> {
            if (!(context.getSource().getSender() instanceof Player player)) {
                context.getSource().getSender().sendRichMessage("Dein Kontostand: 1.000.000.000.000,00² RP");
                return Command.SINGLE_SUCCESS;
            }
            RaspiPlayer raspiPlayer = Raspi.players().get(player);
            raspiPlayer.sendMessage(String.format("<gray>Dein Kontostand: <aqua>%s <gray>RP", raspiPlayer.getUser().getCoins()), true);
            return Command.SINGLE_SUCCESS;
        }).build();
    }

}
