package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.database.RaspiPlayer;
import eu.goodyfx.system.core.database.RaspiSuggestions;
import eu.goodyfx.system.core.utils.RaspiMessages;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnMuteCommandContainer {

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("unmute").then(Commands.argument("player", StringArgumentType.string()).suggests(((context, builder) -> RaspiSuggestions.suggestOfflinePlayer(builder))).executes(context -> {

            if (!(context.getSource().getSender() instanceof Player player)) {
                context.getSource().getSender().sendRichMessage("COMMAND NOT FOUND FOR ACTION.");
                return Command.SINGLE_SUCCESS;
            }
            RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
            String targetName = context.getArgument("player", String.class);
            UUID uuid = Bukkit.getPlayerUniqueId(targetName);
            if (uuid == null) {
                raspiPlayer.sendMessage(String.format(RaspiMessages.PLAYER_NOT_FOUND_NAME, targetName), true);
                return 1;
            }

            if (!Raspi.playerLifeCycleService().isOnline(uuid)) {
                Raspi.playerLifeCycleService().getRaspiAccount(uuid, false).thenAccept(account -> {
                    if (account == null) {
                        raspiPlayer.sendMessage(RaspiMessages.PLAYER_NOT_FOUND);
                        return;
                    }
                    account.getRaspiManagement().performUnMute();
                    raspiPlayer.sendMessage("<green>Du hast den Spieler zum Reden Animiert.", true);
                    Raspi.accountService().saveIfOffline(account);
                });
            }
            return Command.SINGLE_SUCCESS;
        })).build();
    }

}
