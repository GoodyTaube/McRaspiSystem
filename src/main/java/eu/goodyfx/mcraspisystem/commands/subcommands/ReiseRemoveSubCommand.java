package eu.goodyfx.mcraspisystem.commands.subcommands;

import eu.goodyfx.mcraspisystem.commands.SubCommand;
import eu.goodyfx.mcraspisystem.managers.ReiseLocationManager;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;

public class ReiseRemoveSubCommand extends SubCommand {
    @Override
    public String getLabel() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "Dieser Command entfernt eine ID.";
    }

    @Override
    public String getSyntax() {
        return "/reise remove <id>";
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if (args.length == 2) {
            int id = 0;
            try {
                id = Integer.parseInt(args[1]);
                if (!ReiseLocationManager.exist(id)) {
                    player.sendMessage("<red>Die ID:" + args[1] + " existiert nicht.");
                    return true;
                }
                ReiseLocationManager.remove(id);
                player.sendMessage("<green>" + args[1] + " wurde entfernt. ID:" + id + " ist nun wieder Frei.");
            } catch (NumberFormatException e) {
                player.sendMessage(String.format("<red>Wert <%s> muss einer zahl entsprechen.", args[1]));
            }
            return true;
        }
        return false;
    }
}
