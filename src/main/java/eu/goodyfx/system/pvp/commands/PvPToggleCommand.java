package eu.goodyfx.system.pvp.commands;

import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.database.RaspiPlayer;
import eu.goodyfx.system.pvp.PvPSubSystem;
import eu.goodyfx.system.pvp.utils.PvPManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PvPToggleCommand implements CommandExecutor {

    private final PvPManager pvPManager;

    public PvPToggleCommand(PvPSubSystem system) {
        this.pvPManager = system.getPvPManager();
        system.getPlugin().setCommand("pvptoggle", this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (sender instanceof Player player) {
            RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
            if (strings.length == 0) {
                pvPManager.handleToggle(raspiPlayer);
                return true;
            }
        }
        return false;
    }
}
