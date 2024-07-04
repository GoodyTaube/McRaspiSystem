package eu.goodyfx.mcraspisystem.commands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.managers.ReiseLocationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReisePortCommand implements CommandExecutor {

    public ReisePortCommand(McRaspiSystem reise) {
        reise.setCommand("reisePort", this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player player && (args.length == 1)) {
            try {
                int id = Integer.parseInt(args[0]);
                if (!ReiseLocationManager.exist(id)) {
                    player.sendRichMessage("Die ID:" + args[0] + " konnte nicht gefunden werden.");
                    return true;
                }
                if (!ReiseLocationManager.hasEntry(id, "FREE")) {
                    player.teleport(ReiseLocationManager.get(id));
                } else {
                    player.sendRichMessage("Dieser Platz ist noch Frei! Lade freunde ein <3");
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("<id> has to be a number <3");
            }

        }
        return false;
    }

}
