package eu.goodyfx.system.core.utils;

import eu.goodyfx.system.core.managers.TimeDBManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerTime {

    private final Player player;
    private final Long timeStamp;

    public PlayerTime(Player player) {
        this.player = player;
        this.timeStamp = System.currentTimeMillis();

    }

    public void end(TimeDBManager manager) {
        long time = (System.currentTimeMillis() - timeStamp);
        OfflinePlayer target = Bukkit.getOfflinePlayer(player.getUniqueId());

        if (manager.contains(target)) {
            long timePast = manager.get(target);
            time = (time + timePast);
        }

        manager.add(Bukkit.getOfflinePlayer(player.getUniqueId()), time);

    }

}
