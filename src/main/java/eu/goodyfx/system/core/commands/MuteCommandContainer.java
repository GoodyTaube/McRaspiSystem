package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.database.RaspiSuggestions;
import eu.goodyfx.system.core.utils.RaspiMessages;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MuteCommandContainer {

    public static LiteralCommandNode<CommandSourceStack> muteCommand() {
        return Commands.literal("mute").executes(context -> {
            context.getSource().getSender().sendRichMessage("<gray>Bitte nutze: <yellow>/mute <player> <grund>");
            return Command.SINGLE_SUCCESS;
        }).then(Commands.argument("player", StringArgumentType.string()).suggests(((context, builder) -> RaspiSuggestions.suggestOfflinePlayer(builder))).then(Commands.argument("reason", StringArgumentType.string()).executes(MuteCommandContainer::execute))).build();
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        //TODO NEWBIE CHECK
        if (!(context.getSource().getSender() instanceof Player player)) {
            return Command.SINGLE_SUCCESS;
        }

        String targetName = StringArgumentType.getString(context, "player");
        String reason = StringArgumentType.getString(context, "reason");
        String formatted = reason.replace(" ", "@");


        UUID targetUUID = Bukkit.getPlayerUniqueId(targetName);
        if (targetUUID == null) {
            player.sendRichMessage(String.format(RaspiMessages.PLAYER_NOT_FOUND_NAME, targetName));
            return 0;
        }

        Raspi.playerLifeCycleService().getRaspiAccount(targetUUID, false).thenAccept(account -> {
            if (account == null) {
                player.sendMessage("<red>Der Spieler existiert nicht.");
                return;
            }
            if (account.getRaspiManagement().isMuted()) {
                player.sendRichMessage("Der Spieler ist bereits stumm.");
                return;
            }
            account.getRaspiManagement().performMute(player, formatted);
            player.sendRichMessage("Du hast den Spieler erfolgreich muted.");
            Raspi.accountService().saveIfOffline(account);
        });
        return Command.SINGLE_SUCCESS;
    }

}
