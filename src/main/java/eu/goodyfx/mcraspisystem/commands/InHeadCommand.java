package eu.goodyfx.mcraspisystem.commands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.utils.PlayerValues;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import eu.goodyfx.mcraspisystem.utils.RaspiUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class InHeadCommand implements CommandExecutor {

    private final McRaspiSystem plugin;

    public InHeadCommand(McRaspiSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            RaspiPlayer raspiPlayer = new RaspiPlayer(plugin, player);
            if(raspiPlayer.userManager().hasTimePlayed(player, 20)){

            }

        }
        return false;
    }
}
