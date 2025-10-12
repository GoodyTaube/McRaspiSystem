package eu.goodyfx.system.core.commandsOLD;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiMessages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class UnBanCommand implements CommandExecutor {

    private final RaspiMessages data;

    public UnBanCommand(McRaspiSystem plugin) {
        this.data = plugin.getModule().getRaspiMessages();
        plugin.setCommand("unban", this);
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
            Raspi.players().getRaspiOfflinePlayer(offlinePlayer).thenAcceptAsync(raspiOfflinePlayer -> {
                if (raspiOfflinePlayer == null) {
                    sender.sendRichMessage("<red>Der Spieler spielte noch nicht.");
                    return;
                }
                if (raspiOfflinePlayer.getManagement().isBanned()) {
                    raspiOfflinePlayer.getManagement().performUnban();
                    sender.sendRichMessage(data.getPrefix() + "<green>" + raspiOfflinePlayer.getPlayer().getName() + " wurde von dir entsperrt.");
                    return;
                }
                sender.sendRichMessage(data.getPrefix() + "<green>" + raspiOfflinePlayer.getPlayer().getName() + " ist nicht gesperrt.");

            }, runnable -> Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(McRaspiSystem.class), runnable));
            return true;

        }
        return false;
    }
}
