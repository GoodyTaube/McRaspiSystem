package eu.goodyfx.mcraspisystem.commands;

import eu.goodyfx.goodysutilities.GoodysUtilities;
import eu.goodyfx.goodysutilities.managers.PlayerBanManager;
import eu.goodyfx.goodysutilities.managers.UserManager;
import eu.goodyfx.goodysutilities.utils.Data;
import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.managers.PlayerBanManager;
import eu.goodyfx.mcraspisystem.managers.UserManager;
import eu.goodyfx.mcraspisystem.utils.RaspiMessages;
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
        this.userManager = plugin.getUserManager();
        this.playerBanManager = plugin.getPlayerBanManager();
        this.data = plugin.getData();
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
