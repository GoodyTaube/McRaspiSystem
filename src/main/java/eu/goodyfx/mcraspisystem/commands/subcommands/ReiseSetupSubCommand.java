package eu.goodyfx.mcraspisystem.commands.subcommands;

import eu.goodyfx.mcraspisystem.commands.SubCommand;
import eu.goodyfx.mcraspisystem.exceptions.ValueNotFoundException;
import eu.goodyfx.mcraspisystem.managers.ReiseLocationManager;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import org.bukkit.entity.Player;

public class ReiseSetupSubCommand extends SubCommand {
    @Override
    public String getLabel() {
        return "setup";
    }

    @Override
    public String getDescription() {
        return "Dieser Command wird genutzt um einen Nutzer ins Reisebüro zu bringen! Oder um eine ID im Reisebüro zu vergeben! ";
    }

    @Override
    public String getSyntax() {
        return "/reise setup <underlined><nutzername:id> [<id>]</underlined>";
    }

    @Override
    public int length() {
        return 2;
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if ( args.length >= 2) {
            try {
                if (args.length == 3) {
                    int id = Integer.parseInt(args[2]);
                    return user(player, args[1], id);
                }
                int id = Integer.parseInt(args[1]);
                return id(player, id);
            } catch (NumberFormatException e) {
                player.sendMessage("<red>Wert <id> muss eine Zahl sein!");
            }

        }
        return false;
    }

    private boolean id(RaspiPlayer player, int id) {

        if (ReiseLocationManager.exist(id)) {
            player.sendMessage("Die ID:" + id + " existiert bereits!");
        } else {
            ReiseLocationManager.set(id, player.getPlayer().getLocation());
            player.sendMessage(id + " erfolgreich registriert!");
        }
        return true;
    }

    private boolean user(RaspiPlayer player, String targetName, int id) {
        if (!ReiseLocationManager.exist(id)) {
            player.sendMessage("Die ID existiert nicht!");
            return true;
        }

        if (ReiseLocationManager.searchUser(player, targetName)) {
            try {
                int targetID = ReiseLocationManager.getIDByName(targetName);
                player.sendMessage("Der User ist bereits hinterlegt! Seine ID ist: " + targetID);
            } catch (ValueNotFoundException e) {
                player.sendMessage("Ein Datenbank Fehler ist aufgetreten.");
                return false;
            }
            return true;
        }

        if (ReiseLocationManager.hasEntry(id, "FREE")) {
            ReiseLocationManager.bind(id, targetName);
            player.sendMessage("Du hast " + targetName + " erfolgreich auf ID:" + id + " platziert!");
            return true;
        } else {
            player.sendMessage("Die ID ist bereits belegt");
            return false;
        }
    }
}
