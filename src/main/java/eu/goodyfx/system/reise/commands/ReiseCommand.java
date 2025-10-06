package eu.goodyfx.system.reise.commands;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.commandsOLD.subcommands.ReiseListSubCommand;
import eu.goodyfx.system.core.commandsOLD.subcommands.ReiseRemoveSubCommand;
import eu.goodyfx.system.core.commandsOLD.subcommands.ReiseResetSubCommand;
import eu.goodyfx.system.core.commandsOLD.subcommands.ReiseSetupSubCommand;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.SubCommand;
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
                    if (subCommand.commandPerform(Raspi.players().get(player), args)) {
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
