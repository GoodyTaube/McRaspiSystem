package eu.goodyfx.mcraspisystem.commands.subcommands;

import eu.goodyfx.mcraspisystem.commands.SubCommand;
import eu.goodyfx.mcraspisystem.exceptions.ValueNotFoundException;
import eu.goodyfx.mcraspisystem.managers.ReiseLocationManager;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;

public class ReiseResetSubCommand extends SubCommand {


    @Override
    public String getLabel() {
        return "reset";
    }

    @Override
    public String getDescription() {
        return "Der Command ist dazu da, um eine ID zurück zu setzen und den Spieler zu löschen.";
    }

    @Override
    public String getSyntax() {
        return "/reise reset <id>";
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if (args.length == 2) {
            int id = 0;
            try {
                id = ReiseLocationManager.getIDByName(args[1]);
                ReiseLocationManager.reset(id);
            } catch (ValueNotFoundException e) {
                player.sendMessage("<red>" + args[1] + " konnte nicht gefunden werden.");
            } finally {
                player.sendMessage("<green>" + args[1] + " wurde entfernt. ID:" + id + " ist nun wieder Frei.");
            }
            return true;
        }
        return false;
    }
}
