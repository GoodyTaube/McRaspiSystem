package eu.goodyfx.system.core.utils;

import eu.goodyfx.system.core.database.RaspiManagement;
import eu.goodyfx.system.core.database.RaspiUser;
import eu.goodyfx.system.core.database.RaspiUsernames;
import eu.goodyfx.system.core.database.UserSettings;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class RaspiOfflinePlayer {

    private final OfflinePlayer player;
    private RaspiUser raspiUser;
    private RaspiManagement management;
    private UserSettings userSettings;
    private RaspiUsernames usernames;

    public RaspiOfflinePlayer(OfflinePlayer player) {
        this.player = player;
    }

    public void init(RaspiUser raspiUser, UserSettings userSettings, RaspiManagement management, RaspiUsernames usernames) {
        this.raspiUser = Raspi.players().getRaspiUser(player.getUniqueId());
        this.userSettings = Raspi.players().getUserSettings(player.getUniqueId());
        this.management = Raspi.players().getManagement(player.getUniqueId());
        this.usernames = Raspi.players().getUserNameCache(player.getUniqueId());
    }

    public String getPrefix() {
        if (raspiUser.getPrefix() != null) {
            return raspiUser.getPrefix().replace("@", " ");
        }
        return null;
    }

}
