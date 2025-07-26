package eu.goodyfx.system.core.commandsOLD;


import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.managers.ExtraInfos;
import eu.goodyfx.system.core.managers.UserManager;
import eu.goodyfx.system.core.utils.PlayerInfo;
import eu.goodyfx.system.core.utils.RaspiMessages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PlayerStatistic Based
 */

@SuppressWarnings("ConstantConditions")
public class InfoCommand implements CommandExecutor, TabCompleter {

    private final RaspiMessages data;
    private final UserManager userManager;
    private final McRaspiSystem plugin;

    public InfoCommand(McRaspiSystem plugin) {
        this.data = plugin.getModule().getRaspiMessages();
        this.plugin = plugin;
        this.userManager = plugin.getModule().getUserManager();
        plugin.setCommand("playerinfo", this, this);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("playerinfo") && args.length == 1) {
            List<String> result = new ArrayList<>();
            List<String> databaseUsers = userManager.getAllUsers();

            for (String name : databaseUsers) {
                if (name.startsWith(args[0])) {
                    result.add(name);
                }
            }
            Collections.sort(result);
            return result;

        }
        return null;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                player.performCommand("playerinfo " + player.getName());
                return true;
            }
            if (args.length == 1) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                if (!userManager.userExist(target)) {
                    player.sendRichMessage(data.getPrefix() + "<red>Der Spieler hat noch nicht gespielt.");
                    return true;
                }
                //reload
                player.sendRichMessage(new PlayerInfo(plugin, target).buildPlayerInfos());

                ExtraInfos extraInfos = new ExtraInfos(player);
                extraInfos.getExtraInfos(target);

                return true;
            }
            if (args.length >= 3) {
                addInfo(player, args);
                removeInfo(player, args);
                return true;
            }
        }
        return false;
    }

    private void addInfo(Player player, String[] args) {
        if (args[1].equalsIgnoreCase("add")) {
            add(player, args, false);
        } else if (args[1].equalsIgnoreCase("addMod")) {
            add(player, args, true);
        }
    }

    private void add(Player player, String[] args, boolean mod) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!target.hasPlayedBefore()) {
            player.sendRichMessage(data.getUsage("/playerinfo <player> [<add:remove>] [<id>]"));
        }

        StringBuilder message = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            message.append(args[i]).append("@");
        }
        message.setLength(message.length() - 1);
        ExtraInfos extraInfos = new ExtraInfos(player);
        extraInfos.add(target, message.toString(), mod);
        player.sendRichMessage(data.getPrefix() + "Die Info wurde hinzugefügt!");
    }

    private void removeInfo(Player player, String[] args) {
        if (args[1].startsWith("re")) {
            if (args[1].equalsIgnoreCase("remove")) {
                remove(player, args, false);
            } else if (args[1].equalsIgnoreCase("removeMod")) {
                remove(player, args, true);
            }
            player.sendRichMessage(data.getPrefix() + "Die Info wurde gelöscht!");
        }

    }

    private void remove(Player player, String[] args, boolean mod) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!target.hasPlayedBefore()) {
            player.sendRichMessage(data.getUsage("/playerinfo <player> [<add:remove>] [<id>]"));
        }
        ExtraInfos extraInfos = new ExtraInfos(player);
        extraInfos.remove(target, args[2], mod);
    }

}
