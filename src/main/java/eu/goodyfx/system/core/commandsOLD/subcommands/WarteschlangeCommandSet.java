package eu.goodyfx.system.core.commandsOLD.subcommands;

import eu.goodyfx.system.core.utils.SubCommand;
import eu.goodyfx.system.core.managers.LocationManager;
import eu.goodyfx.system.core.utils.RaspiMessages;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import org.bukkit.entity.Player;

public class WarteschlangeCommandSet extends SubCommand {

    private static final String SPAWN = "spawn";
    private static final String WARTERAUM = "warteraum";

    private static final String FIRST_SPAWN = "firstSpawn";

    private final RaspiMessages data;

    private final LocationManager locationManager;

    public WarteschlangeCommandSet(RaspiMessages data, LocationManager locationManager) {
        this.locationManager = locationManager;
        this.data = data;
    }

    @Override
    public String getLabel() {
        return "set";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        Player player1 = player.getPlayer();

        if (args.length == 2) {
            switch (args[1]) {
                case SPAWN:
                    if (locationManager.exist(SPAWN)) {
                        player.sendMessage(data.refreshed());
                    } else {
                        player.sendMessage(data.created());
                    }
                    locationManager.set(player1, SPAWN);
                    break;
                case WARTERAUM:
                    if (locationManager.exist(WARTERAUM)) {
                        player.sendMessage(data.refreshedWaiting());
                    } else {
                        player.sendMessage(data.createdWaiting());
                    }
                    locationManager.set(player1, WARTERAUM);
                    break;
                case FIRST_SPAWN:
                    if (locationManager.exist(FIRST_SPAWN)) {
                        player.sendMessage(data.refreshed());
                    } else {
                        player.sendMessage(data.created());
                    }
                    locationManager.set(player1, FIRST_SPAWN);
                    break;
                default:
                    player.sendMessage(data.getUsage("/warteschlange <set> <spawn : warteraum>"));
                    return false;
            }


        }
        return true;
    }
}
