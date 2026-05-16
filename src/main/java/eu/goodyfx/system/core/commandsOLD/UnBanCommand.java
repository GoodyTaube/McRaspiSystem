package eu.goodyfx.system.core.commandsOLD;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.utils.RaspiMessages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

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
            String name = args[0];
            UUID uuid = Bukkit.getPlayerUniqueId(name);
            if (uuid == null) {
                sender.sendRichMessage(RaspiMessages.PLAYER_NOT_FOUND);
                return true;
            }

            if (offlinePlayer.hasPlayedBefore()) {
                Raspi.playerLifeCycleService().getRaspiAccount(uuid, false).thenAccept(account -> {

                    if (account.getRaspiManagement().isBanned()) {
                        account.getRaspiManagement().performUnban();
                        sender.sendRichMessage(data.getPrefix() + "<green>" + account.getRaspiUser().getUsername() + " wurde von dir entsperrt.");
                        return;
                    }
                    sender.sendRichMessage(data.getPrefix() + "<green>" + account.getRaspiUser().getUsername() + " ist nicht gesperrt.");
                    Raspi.accountService().saveIfOffline(account);
                });
            }

            return true;

        }
        return false;
    }
}
