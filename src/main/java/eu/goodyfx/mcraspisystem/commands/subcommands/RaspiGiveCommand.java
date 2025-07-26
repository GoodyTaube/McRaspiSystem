package eu.goodyfx.mcraspisystem.commands.subcommands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class RaspiGiveCommand implements CommandExecutor {
    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    public RaspiGiveCommand() {
        plugin.setCommand("raspigive", this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (sender instanceof Player player) {
            if (args.length == 2) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    String item = args[0].toLowerCase();
                    plugin.getModule().getRaspiGiveManager().addItem(target, item);
                    player.sendMessage("oka");
                } else player.sendMessage("HÄ wo denn? ");
            }
        }
        return false;
    }
}
