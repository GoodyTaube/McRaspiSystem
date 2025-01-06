package eu.goodyfx.mcraspisystem.commands.subcommands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.SubCommand;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AdminSudoCommand extends SubCommand {

    private final McRaspiSystem plugin;
    private int finalLenth = 3;

    public AdminSudoCommand(McRaspiSystem plugin) {
        this.plugin = plugin;

    }

    @Override
    public String getLabel() {
        return "sudo";
    }

    @Override
    public String getDescription() {
        return "Ein Command über einen Anderen Spieler ausführen.";
    }

    @Override
    public String getSyntax() {
        return "/admin sudo <target:list> <command> [<args>]";
    }

    @Override
    public int length() {
        return finalLenth;
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if (args.length >= 3) {
            finalLenth = args.length;
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                String command = args[2];
                command = command.replace("/", "");
                command = command.replace("%", " ");
                if (target.performCommand(command)) {
                    player.sendMessage("<green>" + target.getName() + " hat " + command + " ausgeführt.");
                } else {

                    player.sendMessage("<red>" + target.getName() + " hat " + command + " nicht ausführen können! Keine Rechte?");
                }
            } else player.sendMessage(plugin.getModule().getRaspiMessages().playerNotOnline(args[1]));
        }
        return true;
    }
}
