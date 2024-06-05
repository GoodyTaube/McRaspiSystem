package eu.goodyfx.mcraspisystem.commands;

import eu.goodyfx.goodysutilities.GoodysUtilities;
import eu.goodyfx.goodysutilities.commands.subcommands.WarteschlangeCommandReload;
import eu.goodyfx.goodysutilities.commands.subcommands.WarteschlangeCommandSet;
import eu.goodyfx.goodysutilities.managers.LocationManager;
import eu.goodyfx.goodysutilities.utils.RaspiPlayer;
import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.subcommands.WarteschlangeCommandReload;
import eu.goodyfx.mcraspisystem.commands.subcommands.WarteschlangeCommandSet;
import eu.goodyfx.mcraspisystem.managers.LocationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WarteschlangeCommand implements CommandExecutor, TabCompleter {

    private static final String SPAWN_VAL = "spawn";
    private static final String WARTERAUM_VAL = "warteraum";
    private final McRaspiSystem plugin;
    private final LocationManager manager;
    private final List<SubCommand> subCommands = new ArrayList<>();


    public WarteschlangeCommand(McRaspiSystem plugin) {
        this.plugin = plugin;
        this.manager = plugin.getLocationManager();
        plugin.setCommand("warteschlange", this, this);
        addSubCommands();
    }

    private void addSubCommands() {
        subCommands.add(new WarteschlangeCommandSet(plugin.getData(), manager));
        subCommands.add(new WarteschlangeCommandReload(plugin));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("warteschlange") && args.length >= 1) {
            if (args.length == 1) {
                subCommands.forEach(val -> results.add(val.getLabel()));
                Collections.sort(results);
                return results;
            } else if (args.length == 2) {
                results.add(SPAWN_VAL);
                results.add(WARTERAUM_VAL);
                return results;
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length > 0) {
                for (SubCommand subCommand : subCommands) {
                    if (args[0].startsWith(subCommand.getLabel())) {
                        return subCommand.commandPerform(new RaspiPlayer(plugin, player), args);
                    }
                }
            } else {
                player.sendRichMessage(plugin.getData().getUsage("/warteschlange <set : reload> [<location>]"));
            }
        }
        return false;
    }

}
