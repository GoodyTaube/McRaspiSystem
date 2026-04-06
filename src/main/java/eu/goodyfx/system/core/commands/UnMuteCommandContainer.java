package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.database.RaspiPlayer;
import eu.goodyfx.system.core.database.RaspiSuggestions;
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

            OfflinePlayer target = Bukkit.getOfflinePlayer(context.getArgument("player", String.class));

            if (!Raspi.playerLifeCycleService().isOnline(target.getUniqueId())) {
                Raspi.playerLifeCycleService().getRaspiOffPlayer(target).thenAccept(account -> {
                    if (account == null) {
                        player.sendMessage("PLAYER NOT FOUND");
                        return;
                    }
                    account.getRaspiManagement().performUnMute();
                    player.sendRichMessage("<green>Du hast den Spieler zum Reden Animiert.");
                });
            }

            assert target.getPlayer() != null;
            RaspiPlayer targetOnline = Raspi.playerLifeCycleService().getRaspiPlayer(target.getPlayer());
            targetOnline.userManagement().performUnMute();
            player.sendRichMessage("<green>Die Anfrage wurde ausgeführt");
            return Command.SINGLE_SUCCESS;
        })).build();
    }

}
