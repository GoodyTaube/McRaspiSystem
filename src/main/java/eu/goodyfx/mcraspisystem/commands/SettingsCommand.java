package eu.goodyfx.mcraspisystem.commands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.managers.PlayerSettingsManager;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import eu.goodyfx.mcraspisystem.utils.Settings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SettingsCommand implements CommandExecutor, TabCompleter {

    private final PlayerSettingsManager playerSettingsManager;

    private final McRaspiSystem system;

    public SettingsCommand(McRaspiSystem plugin) {
        this.system = plugin;
        this.playerSettingsManager = plugin.getModule().getPlayerSettingsManager();
        plugin.setCommand("settings", this, this);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equals("settings") && (args.length == 1)) {
            List<String> results = new ArrayList<>();
            results.add("afk");
            results.add("chat");
            results.add("opt-chat");
            return results;

        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player && (args.length == 1)) {
            // settings chat
            RaspiPlayer raspiPlayer = new RaspiPlayer(system, player);
            switch (args[0]) {
                case "chat":
                    perform(Settings.MESSAGES, player);
                    break;
                case "afk":
                    perform(Settings.AUTO_AFK, player);
                    break;
                case "opt-chat":
                    perform(Settings.ADVANCED_CHAT, player);
                    break;
                default:
                    return false;
            }
            playerSettingsManager.refresh(player);
            return true;
        }
        return false;
    }

    private String createMessage(Settings settings, boolean onOff) {
        if (onOff) {
            return "<gray>Einstellung: <blue>" + settings.getDisplayName() + " <gray>// <green>AN";
        } else {
            return "<gray>Einstellung: <blue>" + settings.getDisplayName() + " <gray>// <red>AUS";
        }
    }

    private boolean hasSetting(Player player, Settings setting) {
        return playerSettingsManager.contains(setting, player);
    }

    private void perform(Settings settings, Player player) {
        if (Boolean.TRUE.equals(hasSetting(player, settings))) {
            playerSettingsManager.remove(settings, player);
            player.sendRichMessage(createMessage(settings, false));
        } else if (!playerSettingsManager.contains(settings, player)) {
            playerSettingsManager.set(settings, player);
            player.sendRichMessage(createMessage(settings, true));
        }
    }

}

