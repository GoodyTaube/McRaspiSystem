package eu.goodyfx.system.core.commandsOLD.subcommands;

import eu.goodyfx.system.core.utils.SubCommand;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

public class AdminResetPlayerSubCommand extends SubCommand {
    @Override
    public String getLabel() {
        return "resetPlayer";
    }

    @Override
    public String getDescription() {
        return "Reset a Player by this Server";
    }

    @Override
    public String getSyntax() {
        return "/admin reset <player>";
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if (args.length == 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            target.setStatistic(Statistic.PLAY_ONE_MINUTE, 0);

        }
        return false;
    }
}
