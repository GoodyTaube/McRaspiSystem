package eu.goodyfx.system.core.commandsOLD;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiMessages;
import eu.goodyfx.system.core.utils.RaspiOfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class UnBanCommand implements CommandExecutor {

    private final RaspiMessages data;

    public UnBanCommand(McRaspiSystem plugin) {
        this.data = plugin.getModule().getRaspiMessages();
        plugin.setCommand("unban", this);
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            RaspiOfflinePlayer target = Raspi.players().getRaspiOfflinePlayer(Bukkit.getOfflinePlayer(args[0]));
            if (!target.getPlayer().hasPlayedBefore()) {
                sender.sendRichMessage("<red>Der Spieler hat noch nie Gespielt.");
                return true;
            }

            if (target.getManagement().isBanned()) {
                target.getManagement().performUnban();
                sender.sendRichMessage(data.getPrefix() + "<green>" + target.getPlayer().getName() + " wurde von dir entsperrt.");
            } else {
                sender.sendRichMessage(data.getPrefix() + "<red>" + target.getPlayer().getName() + " ist nicht gesperrt.");
            }
            return true;

        }
        return false;
    }
}
