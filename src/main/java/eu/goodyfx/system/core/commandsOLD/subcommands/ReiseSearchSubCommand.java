package eu.goodyfx.system.core.commandsOLD.subcommands;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.SubCommand;
import eu.goodyfx.system.reise.managers.ReiseLocationManager;
import eu.goodyfx.system.core.utils.RaspiPlayer;

public class ReiseSearchSubCommand extends SubCommand {

    private McRaspiSystem plugin;

    public ReiseSearchSubCommand(McRaspiSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getLabel() {
        return "suche";
    }

    @Override
    public String getDescription() {
        return "Dieser Command sucht einen Spieler und gibt dessen Reisebüro info Werte wider zurück";
    }

    @Override
    public String getSyntax() {
        return "/reise suche <username>";
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if ( args.length == 2) {
            ReiseLocationManager.checkUser(player, args[1], plugin);
            return true;
        }
        return false;

    }
}
