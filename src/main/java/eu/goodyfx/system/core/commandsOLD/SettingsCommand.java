package eu.goodyfx.system.core.commandsOLD;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.database.UserSettings;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import eu.goodyfx.system.core.utils.Settings;
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


    public SettingsCommand(McRaspiSystem plugin) {
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
        if (sender instanceof Player dummy && (args.length == 1)) {
            RaspiPlayer raspiPlayer = Raspi.players().get(dummy);
            // settings chat
            switch (args[0]) {
                case "chat":
                    perform(Settings.MESSAGES, raspiPlayer);
                    break;
                case "afk":
                    perform(Settings.AUTO_AFK, raspiPlayer);
                    break;
                case "opt-chat":
                    perform(Settings.ADVANCED_CHAT, raspiPlayer);
                    break;
            }
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


    public Boolean hasSetting(Settings settings, RaspiPlayer player) {
        return switch (settings) {
            case ADVANCED_CHAT -> player.getUserSettings().isOpt_chat();
            case AUTO_AFK -> player.getUserSettings().isAuto_afk();
            case MESSAGES -> player.getUserSettings().isServer_messages();
        };

    }


    private void perform(Settings settings, RaspiPlayer player) {
        UserSettings userSettings = player.getUserSettings();
        switch (settings) {
            case ADVANCED_CHAT:
                if (userSettings.isOpt_chat()) {
                    userSettings.setOpt_chat(false);
                    player.sendMessage(createMessage(settings, false), true);
                } else {
                    userSettings.setOpt_chat(true);
                    player.sendMessage(createMessage(settings, true), true);
                }
                break;
            case AUTO_AFK:
                if (userSettings.isAuto_afk()) {
                    userSettings.setAuto_afk(false);
                    player.sendMessage(createMessage(settings, false), true);
                } else {
                    userSettings.setAuto_afk(true);
                    player.sendMessage(createMessage(settings, true), true);
                }
                break;
            case MESSAGES:
                if (userSettings.isServer_messages()) {
                    userSettings.setServer_messages(false);
                    player.sendMessage(createMessage(settings, false), true);
                } else {
                    userSettings.setServer_messages(true);
                    player.sendMessage(createMessage(settings, true), true);
                }
                break;
        }

    }

}

