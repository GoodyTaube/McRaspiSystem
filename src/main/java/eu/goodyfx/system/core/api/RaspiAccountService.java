package eu.goodyfx.system.core.api;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.database.*;
import eu.goodyfx.system.core.exceptions.PlayerNotExistendException;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class RaspiAccountService {

    private final Map<UUID, RaspiAccount> cache = new ConcurrentHashMap<>();
    private final Map<UUID, CompletableFuture<RaspiAccount>> loading = new ConcurrentHashMap<>();
    private final Executor asyncExecutor;
    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    public RaspiAccountService(Executor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }

    public CompletableFuture<RaspiAccount> load(UUID uuid) {
        if (cache.containsKey(uuid)) {
            return CompletableFuture.completedFuture(cache.get(uuid));
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
            cache.put(id, account);
            return account;
        }, asyncExecutor).whenComplete((account, ex) -> loading.remove(id)));
    }

    public CompletableFuture<RaspiAccount> loadOffline(UUID uuid) {
        if (cache.containsKey(uuid)) {
            return CompletableFuture.completedFuture(cache.get(uuid));
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
                throw new PlayerNotExistendException("The Requested Player Cant be found!");
            }
            return new RaspiAccount(id, raspiUser, settings, raspiUsernames, raspiManagement);
        }, asyncExecutor).whenComplete((account, ex) -> loading.remove(id)));
    }


    public CompletableFuture<Void> save(UUID uuid) {
        RaspiAccount account = cache.get(uuid);
        if (account != null) {
            return CompletableFuture.runAsync(() -> {
                account.save();
                cache.remove(uuid);
            }, asyncExecutor);
        }
        return CompletableFuture.completedFuture(null);
    }

    public RaspiAccount getCached(UUID uuid) {
        return cache.get(uuid);
    }

}
