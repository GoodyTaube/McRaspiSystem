package eu.goodyfx.mcraspisystem.commands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.managers.ReiseLocationManager;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ReiseSucheCommand implements CommandExecutor, TabCompleter {

    private McRaspiSystem system;

    public ReiseSucheCommand(McRaspiSystem plugin) {
        this.system = plugin;
        plugin.setCommand("rbsuche", this, this);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (s.equalsIgnoreCase("rbsuche") && args.length == 1) {
            List<String> result = ReiseLocationManager.getAllUsers();
            List<String> finalresults = new ArrayList<>();

            for (String val : result) {
                if (val.toLowerCase().startsWith(args[0].toLowerCase())) {
                    finalresults.add(val);
                }
            }


            return finalresults;
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player player && args.length == 1) {
            ReiseLocationManager.checkUser(new RaspiPlayer(player), args[0], system);
            return true;
        }
        return false;
    }
}
