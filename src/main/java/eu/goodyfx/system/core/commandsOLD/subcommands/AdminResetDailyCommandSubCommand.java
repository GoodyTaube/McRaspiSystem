package eu.goodyfx.system.core.commandsOLD.subcommands;

import com.destroystokyo.paper.profile.PlayerProfile;
import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.commandsOLD.RandomTeleportCommand;
import eu.goodyfx.system.core.utils.SubCommand;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AdminResetDailyCommandSubCommand extends SubCommand {

    private final McRaspiSystem plugin;

    public AdminResetDailyCommandSubCommand(McRaspiSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getLabel() {
        return "resetRandomTP";
    }

    @Override
    public String getDescription() {
        return "Reset des RandomTPS für einen Spieler.";
    }

    @Override
    public String getSyntax() {
        return "/admin resetCommand <player>";
    }
    @Override
    public int length() {
        return 1;
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if (args.length == 2) {
            PlayerProfile target = Bukkit.createProfile(args[1]);

            if (target.isComplete() && plugin.getModule().getUserManager().userExist(Bukkit.getOfflinePlayer(target.getId()))) {
                RandomTeleportCommand.getPlayerContainer().remove(target.getUniqueId());
                player.sendMessage(plugin.getModule().getRaspiMessages().getPrefix() + "<green>" + target.getName() + " Erfolgreich zurückgesetzt!");
                Player targetPlayer = Bukkit.getPlayer(target.getName());
                if (targetPlayer.isOnline()) {
                    plugin.getRaspiPlayer(targetPlayer).sendActionBar("<green>Du kannst deinen RandomTP Neu setzten.");
                }
            } else
                player.sendMessage(plugin.getModule().getRaspiMessages().playerNotOnline(args[1]));
        }
        return true;
    }
}
