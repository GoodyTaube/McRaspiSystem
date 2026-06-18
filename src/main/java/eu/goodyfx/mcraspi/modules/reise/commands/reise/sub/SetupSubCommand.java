package eu.goodyfx.mcraspi.modules.reise.commands.reise.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.commands.RaspiSubCommand;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.core.exceptions.ValueNotFoundException;
import eu.goodyfx.mcraspi.core.utils.RaspiMessages;
import eu.goodyfx.mcraspi.modules.reise.managers.ReiseLocationManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SetupSubCommand implements RaspiSubCommand {

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> build() {
        return Commands.literal("setup").then(Commands.argument("id", IntegerArgumentType.integer(1, 99999)).executes(this::idExecute).then(Commands.argument("spieler", StringArgumentType.string()).suggests(suggestOffline()).executes(this::bindIDExecute)));
    }

    private int idExecute(CommandContext<CommandSourceStack> context) {
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer((Player) context.getSource().getSender());
        int id = context.getArgument("id", Integer.class);
        if (ReiseLocationManager.exist(id)) {
            raspiPlayer.sendMessage("<red>Die id existiert bereits!", true);
            return 1;
        }

        ReiseLocationManager.set(id, raspiPlayer.getLocation());
        raspiPlayer.sendMessage(String.format("<gray>Du hast <aqua>%s erfolgreich <green>erstellt<gray>!", true));
        return Command.SINGLE_SUCCESS;
    }

    private int bindIDExecute(CommandContext<CommandSourceStack> context) {
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer((Player) context.getSource().getSender());
        int id = context.getArgument("id", Integer.class);
        String targetName = context.getArgument("spieler", String.class);
        UUID uuid = Bukkit.getPlayerUniqueId(targetName);
        if (uuid == null) {
            raspiPlayer.sendMessage(String.format(RaspiMessages.PLAYER_NOT_FOUND_NAME, targetName));
            return 1;
        }

        if (!ReiseLocationManager.exist(id)) {
            raspiPlayer.sendMessage("<red>Die ID existiert noch nicht.", true);
            return 1;
        }

        if (ReiseLocationManager.searchUser(raspiPlayer, targetName)) {
            try {
                int targetID = ReiseLocationManager.getIDByName(targetName);
                raspiPlayer.sendMessage("<gray>Der User ist bereits hinterlegt! Seine ID ist: " + targetID, true);
            } catch (ValueNotFoundException e) {
                raspiPlayer.sendMessage("<red>Ein Datenbank Fehler ist aufgetreten.", true);
                return Command.SINGLE_SUCCESS;
            }
            return 1;
        }


        if (ReiseLocationManager.hasEntry(id, "FREE")) {
            ReiseLocationManager.bind(id, targetName);
            raspiPlayer.sendMessage(String.format("<green>Du hast %s auf %s gesetzt! <underlined><aqua><click:run_command:'/rbsuche %s'>Test", targetName, id, targetName), true);
        } else {
            raspiPlayer.sendMessage("<red>Die ID ist bereits belegt", true);
        }

        return Command.SINGLE_SUCCESS;
    }


}
