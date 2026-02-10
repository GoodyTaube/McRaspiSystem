package eu.goodyfx.system.core.api;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.database.RaspiAccount;
import eu.goodyfx.system.core.database.RaspiPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerLifeCycleService {

    private final McRaspiSystem plugin;
    private final Map<UUID, RaspiPlayer> playerCache = new ConcurrentHashMap<>();
    private final RaspiAccountService raspiAccountService;

    public PlayerLifeCycleService(McRaspiSystem plugin, RaspiAccountService accountService) {
        this.plugin = plugin;
        this.raspiAccountService = accountService;
    }

    public void playerJoinHandler(Player player) {
        raspiAccountService.load(player.getUniqueId()).thenAccept(account -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (!player.isOnline()) {
                    return;
                }
                playerCache.put(player.getUniqueId(), new RaspiPlayer(player, account));
                player.sendMessage("Willkommen auf McRaspi.com " + player.getName()); //Beispiel, wird noch durch Richtige Join Logik ersetzt
            });
        });
    }


    public void playerLeaveHandler(Player player) {
        raspiAccountService.save(player.getUniqueId());
        playerCache.remove(player.getUniqueId());
    }

    public RaspiPlayer getRaspiPlayer(Player player) {
        return playerCache.get(player.getUniqueId());
    }

    public CompletableFuture<RaspiAccount> getRaspiOffPlayer(OfflinePlayer player) {
        return raspiAccountService.loadOffline(player.getUniqueId());
    }

}
