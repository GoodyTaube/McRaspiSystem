package eu.goodyfx.mcraspisystem.commands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VoteCommand implements CommandExecutor {

    private final McRaspiSystem plugin;

    public VoteCommand(McRaspiSystem plugin) {
        this.plugin = plugin;
        plugin.setCommand("vote", this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player && args.length == 0 && plugin.getConfig().contains("Utilities.vote")) {
            String link = plugin.getConfig().getString("Utilities.vote");
            player.sendRichMessage(plugin.getModule().getRaspiMessages().getPrefix() + "<green>Vote hier: <click:open_url:'" + link + "'><aqua><underlined>" + link);
        }
        return true;
    }
}
