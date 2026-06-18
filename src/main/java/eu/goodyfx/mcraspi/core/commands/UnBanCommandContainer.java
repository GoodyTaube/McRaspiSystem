package eu.goodyfx.mcraspi.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.database.RaspiManagement;
import eu.goodyfx.mcraspi.core.database.RaspiSuggestions;
import eu.goodyfx.mcraspi.core.utils.RaspiMessages;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class UnBanCommandContainer extends RaspiCommand {

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return command();
    }

    @Override
    public String getName() {
        return "unban";
    }

    @Override
    public String getDescription() {
        return "";
    }

    public UnBanCommandContainer(McRaspiSystem plugin) {
        super(plugin);
    }

    public LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal(getName())
                .then(Commands.argument("spieler", StringArgumentType.string())
                        .suggests(((context, builder) -> RaspiSuggestions.suggestOfflinePlayer(builder)))
                        .executes(this::performCommand)).build();
    }

    private static final String COMMAND_SUCCESS = "%s <green>wurde erfolgreich entsperrt.";
    private static final String COMMAND_FAIL_NOT_BANNED = "%s <green>wurde erfolgreich entsperrt.";

    private int performCommand(CommandContext<CommandSourceStack> context) {

        CommandSender sender = context.getSource().getSender();
        String targetName = context.getArgument("spieler", String.class);
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
