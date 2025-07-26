package eu.goodyfx.system.core.commandsOLD;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.managers.PlayerBanManager;
import eu.goodyfx.system.core.managers.UserManager;
import eu.goodyfx.system.core.utils.RaspiMessages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnBanCommand implements CommandExecutor, TabCompleter {

    private final UserManager userManager;
    private final PlayerBanManager playerBanManager;
    private final RaspiMessages data;

    public UnBanCommand(McRaspiSystem plugin) {
        this.userManager = plugin.getModule().getUserManager();
        this.playerBanManager = plugin.getModule().getPlayerBanManager();
        this.data = plugin.getModule().getRaspiMessages();
        plugin.setCommand("unban", this);
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("unban")) {
            List<String> result = new ArrayList<>();
            List<String> bannedUsers = userManager.getAllUsersWhoSets("ban");

            if (args.length == 1) {
                for (String user : bannedUsers) {
                    if (user.startsWith(args[0])) {
                        result.add(user);
                    }
                }
            }
            Collections.sort(result);
            return result;

        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target.getName() == null) {
                sender.sendRichMessage(data.getPrefix() + "<red>Der Spieler Existiert nicht!");
                return true;
            }

            userManager.reloadFile();
            if (playerBanManager.contains(target)) {
                playerBanManager.removeBan(target);
                sender.sendRichMessage(data.getPrefix() + "<green>" + target.getName() + " wurde von dir entsperrt.");
            } else {
                sender.sendRichMessage(data.getPrefix() + "<red>" + target.getName() + " ist nicht gesperrt.");
            }
            return true;

        }
        return false;
    }
}
