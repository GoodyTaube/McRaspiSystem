package eu.goodyfx.system.core.commandsOLD;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiMessages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
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

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);

            if(offlinePlayer.hasPlayedBefore()){
                Raspi.players().getOrLoadPlayer(offlinePlayer.getUniqueId()).thenAccept(account -> {

                    if (account.getRaspiManagement().isBanned()) {
                        account.getRaspiManagement().performUnban();
                        sender.sendRichMessage(data.getPrefix() + "<green>" + account.getRaspiUser().getUsername() + " wurde von dir entsperrt.");
                        return;
                    }
                    sender.sendRichMessage(data.getPrefix() + "<green>" + account.getRaspiUser().getUsername() + " ist nicht gesperrt.");
                });
            }

            return true;

        }
        return false;
    }
}
