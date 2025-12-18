package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.core.database.RaspiSuggestions;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class UnMuteCommandContainer {

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("unmute").then(Commands.argument("player", StringArgumentType.string()).suggests(((context, builder) -> RaspiSuggestions.suggestOfflinePlayer(builder))).executes(context -> {

            if (!(context.getSource().getSender() instanceof Player player)) {
                context.getSource().getSender().sendRichMessage("COMMAND NOT FOUND FOR ACTION.");
                return Command.SINGLE_SUCCESS;
            }

            OfflinePlayer taget = Bukkit.getOfflinePlayer(context.getArgument("player", String.class));
            if (taget.isOnline()) {
                RaspiPlayer targetOnline = Raspi.players().get(taget.getPlayer());
                targetOnline.getManagement().performUnMute();
                player.sendRichMessage("<green>Die Anfrage wurde ausgeführt");
                return Command.SINGLE_SUCCESS;

            } else {
                Raspi.players().getRaspiOfflinePlayer(taget).thenAcceptAsync(raspiOfflinePlayer -> {
                    if (raspiOfflinePlayer == null) {
                        player.sendRichMessage("Der Spieler nix gibt.");
                        return;
                    }
                    raspiOfflinePlayer.getManagement().performUnMute();
                    player.sendRichMessage("<green>Du hast den Spieler zum Reden Animiert.");
                });
            }
            return Command.SINGLE_SUCCESS;
        })).build();
    }

}
