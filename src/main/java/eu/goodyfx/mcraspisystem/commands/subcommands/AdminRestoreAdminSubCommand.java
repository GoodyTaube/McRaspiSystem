package eu.goodyfx.mcraspisystem.commands.subcommands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.SubCommand;
import eu.goodyfx.mcraspisystem.tasks.InventoryBackup;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AdminRestoreAdminSubCommand extends SubCommand {

    private final McRaspiSystem plugin;

    public AdminRestoreAdminSubCommand(McRaspiSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getLabel() {
        return "restoreinv";
    }

    @Override
    public String getDescription() {
        return "Stellt das Inventar aus einem Backup (15min) wieder her.";
    }

    @Override
    public String getSyntax() {
        return "/admin " + getLabel() + " <player>";
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if (args.length == 2) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                if (!InventoryBackup.getInventoryContainer().containsKey(player.getUUID())) {
                    player.sendMessage(plugin.getModule().getRaspiMessages().getPrefix() + "<red>Entschuldigung, der Spieler hat noch kein Backup.");
                    return true;
                }
                player.getPlayer().getInventory().setContents(InventoryBackup.getInventoryContainer().get(player.getUUID()));
            } else {
                player.sendMessage(plugin.getModule().getRaspiMessages().playerNotOnline(args[1]));
            }

        }
        return true;
    }
}
