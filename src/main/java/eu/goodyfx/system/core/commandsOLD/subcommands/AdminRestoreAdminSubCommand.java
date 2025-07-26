package eu.goodyfx.system.core.commandsOLD.subcommands;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.SubCommand;
import eu.goodyfx.system.core.tasks.InventoryBackup;
import eu.goodyfx.system.core.utils.RaspiPlayer;
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
    public int length() {
        return 1;
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
