package eu.goodyfx.system.core.api;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.database.RaspiAccount;
import eu.goodyfx.system.core.database.RaspiPlayer;
import eu.goodyfx.system.core.tasks.PlayTimeTask;
import eu.goodyfx.system.core.utils.PlayerJoinTasks;
import eu.goodyfx.system.core.utils.PlayerTime;
import eu.goodyfx.system.core.utils.RaspiPermission;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerLifeCycleService {

    private final McRaspiSystem plugin;
    private final Map<UUID, RaspiPlayer> playerCache = new ConcurrentHashMap<>();
    private final RaspiAccountService raspiAccountService;
    private final Map<UUID, PlayerTime> timeContainer = new HashMap<>();
    @Getter
    private final Map<UUID, Location> afkContainer = new HashMap<>();
    private PlayerJoinTasks joinTasks = new PlayerJoinTasks();


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
                RaspiPlayer raspiPlayer = new RaspiPlayer(player, account);
                playerCache.put(player.getUniqueId(), raspiPlayer);
                joinTasks.perform(raspiPlayer, timeContainer);
                oldOnlineHours(raspiPlayer);
                raspiPlayer.nameController.setPlayerList();
                PlayTimeTask.getJoinCache().put(player.getUniqueId(), System.currentTimeMillis());
                Bukkit.getScheduler().runTask(plugin, () -> {
                    Raspi.debugger().debug("WELCOME PLAYER:" + plugin.getModule().getJoinMessageManager().get(raspiPlayer));
                });
                player.getPlayer().updateCommands();
            });
        });
    }

    public void oldOnlineHours(RaspiPlayer raspiPlayer) {
        int hoursCurrent = raspiPlayer.userData().getOnlineHours();
        Objective objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("Onlinestunden");
        if (objective == null) {
            raspiPlayer.sendDebugMessage("404 SCOREBOARD!");
            return;
        }
        int score = objective.getScore(raspiPlayer.getPlayer()).getScore();

        if (hoursCurrent < score) {
            raspiPlayer.userData().setOnlineHours(hoursCurrent + score);
            raspiPlayer.sendActionBar(String.format("<green>+%s OnlineStunden", score));
        }
    }


    public void playerLeaveHandler(Player player) {
        raspiAccountService.save(player.getUniqueId()).thenAccept(a -> {
            playerCache.remove(player.getUniqueId());
            PlayTimeTask.getJoinCache().remove(player.getUniqueId());
            plugin.getDebugger().debug(String.format("[Lifecycle] Removed %s from RaspiPLayers!", player.getName()));
            if (timeContainer.containsKey(player.getUniqueId())) {
                PlayerTime playerTime = timeContainer.get(player.getUniqueId());
                playerTime.end(plugin.getModule().getTimeDBManager());
            }
        });
    }

    public RaspiPlayer getRaspiPlayer(Player player) {
        return playerCache.get(player.getUniqueId());
    }

    public CompletableFuture<RaspiAccount> getRaspiOffPlayer(OfflinePlayer player) {
        return raspiAccountService.loadOffline(player.getUniqueId());
    }

    public Collection<RaspiPlayer> getCachedRaspiPlayers() {
        return playerCache.values();
    }

    public boolean isOnline(UUID uuid) {
        return playerCache.containsKey(uuid);
    }

    public List<RaspiPlayer> getRaspiTeamPlayers() {
        List<RaspiPlayer> team = new ArrayList<>();
        for (RaspiPlayer raspiPlayer : playerCache.values()) {
            if (raspiPlayer.hasPermission(RaspiPermission.TEAM)) {
                team.add(raspiPlayer);
            }
        }
        return team;

    }

    public List<RaspiPlayer> getRaspiPlayersWHP(RaspiPermission raspiPermission) {
        List<RaspiPlayer> team = new ArrayList<>();
        for (RaspiPlayer raspiPlayer : playerCache.values()) {
            if (raspiPlayer.hasPermission(raspiPermission)) {
                team.add(raspiPlayer);
            }
        }
        return team;
    }

}
