package eu.goodyfx.mcraspisystem.commands.subcommands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.SubCommand;
import eu.goodyfx.mcraspisystem.managers.ReiseLocationManager;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import org.bukkit.entity.Player;

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
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if ( args.length == 2) {
            ReiseLocationManager.checkUser(player, args[1], plugin);
            return true;
        }
        return false;

    }
}
