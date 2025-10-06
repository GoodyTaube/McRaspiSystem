package eu.goodyfx.system.core.commandsOLD;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.database.DatabaseTables;
import eu.goodyfx.system.core.database.RaspiUser;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiOfflinePlayer;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import eu.goodyfx.system.core.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class AdminPlayerInfoCommand extends SubCommand {
    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    @Override
    public String getLabel() {
        return "playerinfo";
    }

    @Override
    public String getDescription() {
        return "Get Player Infos out of DB";
    }

    @Override
    public String getSyntax() {
        return "/admin playerinfo <player>";
    }

    @Override
    public int length() {
        return 3;
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if (args.length == 2 && args[0].equalsIgnoreCase("playerinfo")) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            if (plugin.getDatabaseManager().userExistInTable(target.getUniqueId(), DatabaseTables.USER_DATA)) {
                Raspi.players().clearCacheFor(target.getUniqueId());
                if (target.isConnected()) {
                    Raspi.players().get(target.getUniqueId()); // Um den im Spiel Spieler nicht zu broken :o
                }
                RaspiOfflinePlayer offlinePlayer = Raspi.players().getRaspiOfflinePlayer(target);
                String builder = getString(offlinePlayer);
                player.sendDebugMessage(builder);
            } else {
                player.sendMessage("<dark_red>Admin:: <red>Der Spieler hat noch nicht bei uns gespielt.");
            }
        }
        return false;
    }

    private static @NotNull String getString(RaspiOfflinePlayer offlinePlayer) {
        RaspiUser raspiUser = offlinePlayer.getRaspiUser();
        return "DatabasePlayerInfos" + "UUID::" + raspiUser.getUuid() + "<br>" +
                "name::" + raspiUser.getUsername() + "<br>" +
                "color::" + raspiUser.getColor() + "<br>" +
                "prefix::" + raspiUser.getPrefix() + "<br>" +
                "state::" + raspiUser.getState() + "<br>" +
                "denied_by::" + raspiUser.getDenied_by() + "<br>" +
                "deny_reason::" + raspiUser.getDeny_reason() + "<br>" +
                "accepted_by::" + raspiUser.getAllowed_by() + "<br>" +
                "accepted_since::" + raspiUser.getAllowed_since() + "<br>" +
                "first_join::" + raspiUser.getFirst_join() + "<br>" +
                "lastSeen::" + raspiUser.getLastSeen() + "<br>" +
                "voting::" + raspiUser.getVoting() + "<br>" +
                "online_hours::" + raspiUser.getOnlineHours() + "<br>";
    }
}
