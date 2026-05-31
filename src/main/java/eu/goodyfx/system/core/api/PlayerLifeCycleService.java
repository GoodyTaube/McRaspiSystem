package eu.goodyfx.system.core.api;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.database.RaspiAccount;
import eu.goodyfx.system.core.database.RaspiPlayer;
import eu.goodyfx.system.core.tasks.PlayTimeTask;
import eu.goodyfx.system.core.utils.PlayerJoinTasks;
import eu.goodyfx.system.core.utils.PlayerTime;
import eu.goodyfx.system.core.utils.RaspiPermission;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Objective;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerLifeCycleService {

    private final McRaspiSystem plugin;
    private final Map<UUID, RaspiPlayer> playerCache = new ConcurrentHashMap<>();
    private final RaspiAccountService raspiAccountService;
    private final Map<UUID, PlayerTime> timeContainer = new HashMap<>();
    @Getter
    private static NamespacedKey HOURS_MIGRATION_KEY;
    @Getter
    private final Map<UUID, Location> afkContainer = new HashMap<>();
    private PlayerJoinTasks joinTasks = new PlayerJoinTasks();


    public PlayerLifeCycleService(McRaspiSystem plugin, RaspiAccountService accountService) {
        this.plugin = plugin;
        HOURS_MIGRATION_KEY = new NamespacedKey(plugin, "hoursMigration");
        this.raspiAccountService = accountService;
    }

    public void playerJoinHandler(@NonNull Player player) {

        //First Join Message.
        if (!player.hasPlayedBefore()) {
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize(String.format("<dark_purple>%s ist zum ersten mal auf mcrapsi.com!", player.getName())));
        }

        raspiAccountService.getRaspiAccount(player.getUniqueId(), true).thenAccept(account -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (!player.isOnline()) {
                    return;
                }
                RaspiPlayer raspiPlayer = new RaspiPlayer(player, account);
                playerCache.put(player.getUniqueId(), raspiPlayer);
                if (!joinTasks.perform(raspiPlayer, timeContainer)) {
                    return;
                }
                oldOnlineHours(raspiPlayer);
                raspiPlayer.nameController.setPlayerList();
                PlayTimeTask.getJoinCache().put(player.getUniqueId(), System.currentTimeMillis());
                Raspi.debugger().debug("WELCOME PLAYER:" + plugin.getModule().getJoinMessageManager().get(raspiPlayer));
                player.getPlayer().updateCommands();
                plugin.getHookManager().getDiscordIntegration().send(String.format("`[System] <%s> ist zurückgekehrt.`", player.getName()));
            });
        });
    }

    public void oldOnlineHours(RaspiPlayer raspiPlayer) {
        Player player = raspiPlayer.getPlayer();
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        player.setScoreboard(PlayTimeTask.getOnlineHours());
        PlayTimeTask.updatePlayerValue(player);
        if (!dataContainer.has(HOURS_MIGRATION_KEY, PersistentDataType.BYTE)) {
            dataContainer.set(HOURS_MIGRATION_KEY, PersistentDataType.BYTE, (byte) 1); //Setzt den Wert in den Spieler (vermeidet) doppel Buchung
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

    /**
     * Dont forget to Save!
     * {@link RaspiAccountService#saveIfOffline(RaspiAccount)}
     *
     * @param targetUUID The Requested Player
     * @return A RaspiAccount
     */
    public CompletableFuture<RaspiAccount> getRaspiAccount(UUID targetUUID, boolean putInCache) {
        return raspiAccountService.getRaspiAccount(targetUUID, putInCache);
    }

    public Collection<RaspiPlayer> getCachedRaspiPlayers() {
        return playerCache.values();
    }

    public boolean isOnline(UUID uuid) {
        return plugin.getRaspiAccountService().isCached(uuid);
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

    public OfflinePlayer playedBefore(String userName) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(userName);
        return offlinePlayer.hasPlayedBefore() ? offlinePlayer : null;
    }

}
