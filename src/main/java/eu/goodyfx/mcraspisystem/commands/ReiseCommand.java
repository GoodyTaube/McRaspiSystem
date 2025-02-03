package eu.goodyfx.mcraspisystem.commands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.subcommands.ReiseListSubCommand;
import eu.goodyfx.mcraspisystem.commands.subcommands.ReiseRemoveSubCommand;
import eu.goodyfx.mcraspisystem.commands.subcommands.ReiseResetSubCommand;
import eu.goodyfx.mcraspisystem.commands.subcommands.ReiseSetupSubCommand;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
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

public class ReiseCommand implements CommandExecutor, TabCompleter {


    private final List<SubCommand> subCommands = new ArrayList<>();
    private final McRaspiSystem system;

    public ReiseCommand(McRaspiSystem plugin) {
        this.system = plugin;
        plugin.setCommand("reise", this, this);
        addSubs();

    }

    private void addSubs() {
        subCommands.add(new ReiseSetupSubCommand());
        subCommands.add(new ReiseRemoveSubCommand());
        subCommands.add(new ReiseListSubCommand());
        subCommands.add(new ReiseResetSubCommand());
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (s.equalsIgnoreCase("reise") && strings.length == 1) {
            List<String> results = new ArrayList<>();
            subCommands.forEach(subCommands1 -> results.add(subCommands1.getLabel()));
            Collections.sort(results);
            return results;
        }
        return null;

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player player) {
            for (SubCommand subCommand : subCommands) {
                if (args[0].equalsIgnoreCase(subCommand.getLabel())) {
                    if (subCommand.commandPerform(new RaspiPlayer(player), args)) {
                        return true;
                    } else {
                        if (subCommand.getDescription() != null && subCommand.getSyntax() != null) {
                            player.sendRichMessage("<italic><gray>" + subCommand.getDescription() + "<hover:show_text:'" + subCommand.getSyntax() + "'> <green>SHOW");
                        }
                        player.sendRichMessage("Error in reise " + subCommand.getLabel() + "!");
                    }
                    break;
                }
            }
        }
        return false;
    }
}
