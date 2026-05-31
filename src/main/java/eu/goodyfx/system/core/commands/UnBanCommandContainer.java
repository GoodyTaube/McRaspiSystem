package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.database.RaspiManagement;
import eu.goodyfx.system.core.database.RaspiSuggestions;
import eu.goodyfx.system.core.utils.RaspiMessages;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class UnBanCommandContainer {

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("unban")
                .then(Commands.argument("player", StringArgumentType.string())
                        .suggests(((context, builder) -> RaspiSuggestions.suggestOfflinePlayer(builder)))
                        .executes(UnBanCommandContainer::performCommand)).build();
    }

    private static final String COMMAND_SUCCESS = "%s <green>wurde erfolgreich entsperrt.";
    private static final String COMMAND_FAIL_NOT_BANNED = "%s <green>wurde erfolgreich entsperrt.";

    private static int performCommand(CommandContext<CommandSourceStack> context) {

        CommandSender sender = context.getSource().getSender();
        String targetName = context.getArgument("player", String.class);
        UUID targetUUID = Bukkit.getPlayerUniqueId(targetName);
        if (targetUUID == null) {
            sender.sendRichMessage(String.format(RaspiMessages.PLAYER_NOT_FOUND_NAME, targetName));
            return 1;
        }
        Raspi.playerLifeCycleService().getRaspiAccount(targetUUID, false).thenAccept(raspiAccount -> {
            RaspiManagement management = raspiAccount.getRaspiManagement();
            String colorName = raspiAccount.getRaspiUser().getColor() + raspiAccount.getRaspiUser().getUsername() + "<reset>";
            if (!management.isBanned()) {
                sender.sendRichMessage(String.format(COMMAND_FAIL_NOT_BANNED, colorName));
                return;
            }
            management.performUnban();
            sender.sendRichMessage(String.format(COMMAND_SUCCESS, colorName));
            Raspi.accountService().saveIfOffline(raspiAccount);
        });
        return Command.SINGLE_SUCCESS;
    }


}
