package eu.goodyfx.system.core.api;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.database.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

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

    /**
     * Raspi Account Service provide all necessary Database Caches for the RaspiPlayer
     * Handles Load and Saving Players
     *
     * @param asyncExecutor The plugin asyncExecutor
     */
    public RaspiAccountService(Executor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }


    /**
     * Get a RaspiAccount by Player UUID
     *
     * @param uuid       The Player
     * @param putInCache Should we Cache this Player?
     * @return A RaspiAccount or Null if Player is Offline and non Exist
     */
    public CompletableFuture<RaspiAccount> getRaspiAccount(UUID uuid, boolean putInCache) {
        RaspiAccount cached = cache.get(uuid);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }
        return loading.computeIfAbsent(uuid, id -> CompletableFuture.supplyAsync(() -> {
            boolean exist = plugin.getDatabaseManager().userExistInTable(uuid, DatabaseTables.USER_DATA);
            plugin.getLogger().info(exist + "");
            if (!putInCache && !exist) {
                //OFFLINE PLAYER NO EXIST
                return null;
            }
            RaspiAccount raspiAccount = createAndFetch(id, exist);
            if (putInCache) {
                cache.put(id, raspiAccount);
            }
            return raspiAccount;
        }, asyncExecutor).whenComplete((res, ex) -> loading.remove(id)));
    }


    /**
     * Helper Method to get RaspiAccount
     *
     * @param uuid  The Requested User by UUID
     * @param exist The return value from exist Check
     * @return A New Raspi Account for given userID
     */
    @Contract("_, _ -> new")
    private @NonNull RaspiAccount createAndFetch(@NotNull UUID uuid, boolean exist) {
        RaspiUser raspiUser = new RaspiUser(uuid);
        RaspiManagement raspiManagement = new RaspiManagement(uuid);
        RaspiUsernames raspiUsernames = new RaspiUsernames(uuid);
        RaspiSettings raspiRaspiSettings = new RaspiSettings(uuid);
        if (exist) {
            //Daten holen für existierenden Nutzer
            raspiUser.fetch();
            raspiManagement.fetch();
            raspiRaspiSettings.fetch();
            raspiUsernames.update();
        } else {
            //Spieler neu Schreiben.
            raspiUser.write();
            raspiManagement.write();
            raspiUsernames.update();
            raspiRaspiSettings.write();
            DatabaseManager.getUserExistCacheUserData().put(uuid, true);
        }
        return new RaspiAccount(uuid, raspiUser, raspiRaspiSettings, raspiUsernames, raspiManagement);
    }

    /**
     * Helper Method to save offline Player Data.
     *
     * @param raspiAccount The Account to save
     */
    public void saveIfOffline(@NotNull RaspiAccount raspiAccount) {
        if (!cache.containsKey(raspiAccount.getUuid())) {
            CompletableFuture.runAsync(raspiAccount::save, asyncExecutor);
        }
    }

    /**
     * Saving Data to DB for given RaspiAccountID
     *
     * @param uuid The RaspiAccount uuid
     * @return nothing
     */
    public CompletableFuture<Void> save(UUID uuid) {
        RaspiAccount account = cache.remove(uuid);
        if (account != null) {
            return CompletableFuture.runAsync(account::save, asyncExecutor);
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Check if Player is in cache
     *
     * @param uuid The Requested Player
     * @return True if Player is in Cache
     */
    public boolean isCached(UUID uuid) {
        return cache.containsKey(uuid);
    }
}
