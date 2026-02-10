package eu.goodyfx.system.core.database;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiPermission;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Getter
public class RaspiPlayers {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    private final ConcurrentHashMap<UUID, RaspiAccount> accountsCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, RaspiPlayer> activePlayers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, CompletableFuture<RaspiAccount>> loading = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Location> afkContainer = new ConcurrentHashMap<>();


    public CompletableFuture<RaspiAccount> getOrLoadPlayer(UUID uuid) {

        RaspiAccount cached = accountsCache.get(uuid);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }

        return loading.computeIfAbsent(uuid, id -> CompletableFuture.supplyAsync(() -> {
            RaspiUser raspiUser = new RaspiUser(id);
            RaspiManagement raspiManagement = new RaspiManagement(id);
            RaspiUsernames raspiUsernames = new RaspiUsernames(id);
            UserSettings settings = new UserSettings(id);
            if (plugin.getDatabaseManager().userExistInTable(id, DatabaseTables.USER_DATA)) {
                raspiUser.fetch();
                raspiManagement.fetchData();
                raspiUsernames.update();
                settings.fetch();
            } else {
                raspiUser.write();
                raspiManagement.writeUser();
                raspiUsernames.update();
                settings.write();
            }
            RaspiAccount account = new RaspiAccount(id, raspiUser, settings, raspiUsernames, raspiManagement);
            accountsCache.put(id, account);
            return account;
        }, plugin.getAsyncExecutor()).whenComplete((account, ex) -> loading.remove(id)));
    }

    public CompletableFuture<RaspiUserContext> getContextPlayer(UUID uuid) {
        RaspiPlayer onlinePlayer = activePlayers.get(uuid);
        if (onlinePlayer != null) {
            return CompletableFuture.completedFuture(onlinePlayer);
        }
        return getOrLoadPlayer(uuid).thenCompose(profile -> CompletableFuture.supplyAsync(() -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                RaspiPlayer newPlayer = new RaspiPlayer(player, profile);
                activePlayers.put(uuid, newPlayer);
                return newPlayer;
            }
            return new RaspiOffPlayer(uuid, profile);
        }, runnable -> Bukkit.getScheduler().runTask(plugin, runnable)));
    }

    public List<RaspiPlayer> getRaspiTeamPlayers() {
        List<RaspiPlayer> team = new ArrayList<>();
        for (RaspiPlayer player : activePlayers.values()) {
            if (player.hasPermission(RaspiPermission.TEAM)) {
                team.add(player);
            }
        }
        return team;
    }

    public List<RaspiPlayer> getRaspiPlayers(RaspiPermission permission) {
        List<RaspiPlayer> team = new ArrayList<>();
        for (RaspiPlayer player : activePlayers.values()) {
            if (player.hasPermission(permission)) {
                team.add(player);
            }
        }
        return team;
    }


    public void withOnlinePlayer(Player player, Consumer<RaspiPlayer> consumer) {
        Raspi.players().getContextPlayer(player.getUniqueId())
                .thenAccept(context -> {
                    if (context instanceof RaspiPlayer online) {
                        Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(McRaspiSystem.class),
                                () -> consumer.accept(online));
                    }
                });
    }

    public RaspiPlayer getActive(Player player) {
        return activePlayers.get(player.getUniqueId());
    }


}
