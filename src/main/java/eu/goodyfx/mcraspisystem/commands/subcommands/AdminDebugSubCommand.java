package eu.goodyfx.mcraspisystem.commands.subcommands;


import eu.goodyfx.goodysutilities.GoodysUtilities;
import eu.goodyfx.goodysutilities.commands.SubCommand;
import eu.goodyfx.goodysutilities.utils.RaspiPlayer;
import eu.goodyfx.goodysutilities.utils.Settings;
import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.SubCommand;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import eu.goodyfx.mcraspisystem.utils.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AdminDebugSubCommand extends SubCommand {


    private final McRaspiSystem plugin;

    public AdminDebugSubCommand(McRaspiSystem utilities) {
        this.plugin = utilities;
    }

    @Override
    public String getLabel() {
        return "debug";
    }

    @Override
    public String getDescription() {
        return "Debug Command for Utilities";
    }

    @Override
    public String getSyntax() {
        return "/admin debug <ACTION>";
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if (args.length == 3 && args[1].equalsIgnoreCase("settings")) {
            Player target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                player.sendMessage(plugin.getData().playerNotOnline(args[2]));
                return true;
            }
            player.sendMessage(target.getName() + " Settings:");
            for (Settings setting : Settings.values()) {
                player.sendMessage(setting.getLabel() + ": " + plugin.getPlayerSettingsManager().contains(setting, target));
            }

        }
        return true;
    }
}
