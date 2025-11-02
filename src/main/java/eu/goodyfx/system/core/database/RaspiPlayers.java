package eu.goodyfx.system.core.database;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

@Getter
public class RaspiPlayers {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    private final ConcurrentHashMap<UUID, RaspiPlayer> cacheBundle = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, RaspiManagement> managementCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, RaspiUser> userCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, UserSettings> settingsCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, RaspiUsernames> userNameCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, RaspiOfflinePlayer> cacheBundleOffline = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Location> afkContainer = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Boolean> userPlayedBefore = new ConcurrentHashMap<>();

    public List<RaspiPlayer> getRaspiPlayers() {
        return Bukkit.getOnlinePlayers().stream().map(this::get).toList();
    }

    public List<RaspiPlayer> getRaspiTeamPlayers() {
        return getRaspiPlayers().stream().filter(player -> player.hasPermission(RaspiPermission.TEAM)).toList();
    }

    public List<RaspiPlayer> getRaspiModPlayers() {
        return getRaspiPlayers().stream().filter(player -> player.hasPermission(RaspiPermission.MOD)).toList();
    }

    public RaspiManagement getManagement(UUID uuid) {
        return managementCache.get(uuid);
    }

    public RaspiUsernames getUserNameCache(UUID uuid) {
        return userNameCache.get(uuid);
    }

    public UserSettings getUserSettings(UUID uuid) {
        return settingsCache.get(uuid);
    }

    public RaspiUser getRaspiUser(UUID uuid) {
        return userCache.get(uuid);
    }

    public CompletableFuture<Void> loadAsyncFuture(UUID uuid, boolean offline) {
        return CompletableFuture.runAsync(() -> loadAsync(uuid, offline), plugin.getAsyncExecutor());
    }


    private void loadAsync(UUID uuid, boolean offline) { //Lag prävention
        RaspiUser raspiUser = new RaspiUser(uuid);
        RaspiManagement management = new RaspiManagement(uuid);
        UserSettings userSettings = new UserSettings(uuid);
        RaspiUsernames raspiUsernames = new RaspiUsernames(uuid);
        boolean playedBefore = false;
        if (plugin.getDatabaseManager().userExistInTable(uuid, DatabaseTables.USER_DATA)) {
            plugin.getDebugger().info(String.format("[RP.loadAsync] Fetching Dataset for %s", uuid));
            raspiUser.fetch();
            management.fetchData();
            userSettings.fetch();
            raspiUsernames.update();
            playedBefore = true;
        } else {
            if (offline) {
                plugin.getDebugger().info("[RP.loadAsync#OFFLINE] Offline User! No Data Creation required.");
                return;
            }
            plugin.getDebugger().info(String.format("[RP.loadAsync#ONLINE] Creating NEW Dataset for %s", uuid));
            management.writeUser();
            raspiUser.write();
            userSettings.write();
            raspiUsernames.update();
        }
        userPlayedBefore.put(uuid, playedBefore);
        userCache.put(uuid, raspiUser);
        managementCache.put(uuid, management);
        settingsCache.put(uuid, userSettings);
        userNameCache.put(uuid, raspiUsernames);

    }

    public void initPlayer(Player player) {
        if (cacheBundleOffline.containsKey(player.getUniqueId())) {
            saveAndRemove(player.getUniqueId());
        }
        RaspiPlayer raspiPlayer = new RaspiPlayer(player);
        RaspiUser user = getRaspiUser(player.getUniqueId());
        RaspiManagement management = getManagement(player.getUniqueId());
        UserSettings settings = getUserSettings(player.getUniqueId());
        RaspiUsernames usernames = getUserNameCache(player.getUniqueId());
        raspiPlayer.initData(user, management, settings, usernames);
        raspiPlayer.nameController().setPlayerList();
        cacheBundle.put(player.getUniqueId(), raspiPlayer);
    }

    public void initPlayer(OfflinePlayer player) {
        if (!userPlayedBefore.containsKey(player.getUniqueId())) {
            loadAsync(player.getUniqueId(), true);
            return;
        }
        RaspiOfflinePlayer raspiOfflinePlayer = new RaspiOfflinePlayer(player);
        RaspiUser user = getRaspiUser(player.getUniqueId());
        RaspiManagement management = getManagement(player.getUniqueId());
        UserSettings userSettings = getUserSettings(player.getUniqueId());
        RaspiUsernames usernames = getUserNameCache(player.getUniqueId());
        raspiOfflinePlayer.init(user, userSettings, management, usernames);
        cacheBundleOffline.put(player.getUniqueId(), raspiOfflinePlayer);
    }


    public CompletableFuture<RaspiOfflinePlayer> getRaspiOfflinePlayer(OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        if (cacheBundleOffline.containsKey(uuid)) {
            return CompletableFuture.completedFuture(cacheBundleOffline.get(uuid));
        }

        return loadAsyncFuture(uuid, true).thenApplyAsync(v -> {
            initPlayer(offlinePlayer);
            return cacheBundleOffline.get(uuid);
        }, runnable -> Bukkit.getScheduler().runTask(plugin, runnable));
    }

    public RaspiPlayer getOrCreate(Player player) {
        if (!cacheBundle.containsKey(player.getUniqueId())) {
            initPlayer(player);
        }
        return cacheBundle.get(player.getUniqueId());
    }

    public RaspiPlayer get(Player player) {
        return getOrCreate(player);
    }

    public RaspiPlayer get(UUID uuid) {
        if (cacheBundle.containsKey(uuid)) {
            return cacheBundle.get(uuid);
        }
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            return getOrCreate(player);
        }
        return null;
    }

    public void saveAndRemove(UUID uuid) {

        try {
            if (userCache.containsKey(uuid)) {
                userCache.get(uuid).updateUserData();
                plugin.getDebugger().info(String.format("[RP.saveAndRemove] removed %s from userCache", uuid));
            } else {
                plugin.getDebugger().info(String.format("[RP.saveAndRemove] no RaspiUser found for %s.", uuid));
            }
            if (managementCache.containsKey(uuid)) {
                managementCache.get(uuid).updateUserData();
                plugin.getDebugger().info(String.format("[RP.saveAndRemove] removed %s from managementCache", uuid));
            } else {
                plugin.getDebugger().info(String.format("[RP.saveAndRemove] no RaspiManagement found for %s. ", uuid));
            }
            if (settingsCache.containsKey(uuid)) {
                plugin.getDebugger().info(String.format("[RP.saveAndRemove] removed %s from settingsCache", uuid));
                settingsCache.get(uuid).update();
            } else {
                plugin.getDebugger().info(String.format("[RP.saveAndRemove] no RaspiSetting found for %s.", uuid));
            }

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, String.format("[RP.saveAndRemove] Failed to Save and Remove Data for %s", uuid), e);
        } finally {
            clearCacheFor(uuid);
        }
    }


    public void checkOldContents() {
        long start = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(JavaPlugin.getPlugin(McRaspiSystem.class), () -> {
            FallBackManager fallBackManager = JavaPlugin.getPlugin(McRaspiSystem.class).getDatabaseManager().getFallBackManager();
            for (String uuidString : Objects.requireNonNull(fallBackManager.getConfig().getConfigurationSection("User")).getKeys(false)) {

                loadAsync(UUID.fromString(uuidString), false);
                UUID uuid = UUID.fromString(uuidString);
                String username = MojangPlayerWrapper.getName(uuid);
                if (username == null) {
                    Raspi.debugger().info(String.format("No Data found for %s have to skip user!", uuid));
                    continue;
                }
                fallBackManager.getConfig().set("User." + uuidString + ".userName", null);

                RaspiUser user = getRaspiUser(uuid);
                if (user == null) {
                    getRaspiOfflinePlayer(Bukkit.getOfflinePlayer(uuid)).thenAcceptAsync(raspiOfflinePlayer -> {
                        if (raspiOfflinePlayer == null) {
                            return;
                        }
                        fallBackManager.perform(raspiOfflinePlayer.getRaspiUser());
                        fallBackManager.perform(raspiOfflinePlayer.getManagement());
                        fallBackManager.perform(raspiOfflinePlayer.getUserSettings());
                        fallBackManager.perform(getUserNameCache(uuid));
                    });
                }
                if (Objects.requireNonNull(fallBackManager.getConfig().getConfigurationSection("User." + uuidString)).getKeys(false).isEmpty()) {
                    fallBackManager.getConfig().set("User." + uuidString, null);
                    fallBackManager.save();
                }


                cleanUp();
                Raspi.debugger().info(String.format("Checkup for %s DONE", username));
            }
            Raspi.debugger().info("OLD Data Migration DONE! Cleaning up");
            end(start);
        });
    }

    private void end(long start) {
        Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(McRaspiSystem.class), () -> Bukkit.getServer().setWhitelist(false));
        Raspi.debugger().info(String.format("CleanUp User Migration Done! Time(%s)", RaspiTimeUtils.formatDuration(RaspiTimeUtils.getBetween(start))));
        plugin.getConfig().set("Utilities.wartung", false);
        Bukkit.getServer().shutdown();
    }

    private void cleanUp() {
        this.userCache.clear();
        this.settingsCache.clear();
        this.cacheBundle.clear();
        this.managementCache.clear();
        this.cacheBundleOffline.clear();
    }

    public void clearCacheFor(UUID uuid) {
        cacheBundle.remove(uuid);
        cacheBundleOffline.remove(uuid);
        userCache.remove(uuid);
        managementCache.remove(uuid);
        settingsCache.remove(uuid);
        userNameCache.remove(uuid);
        plugin.getDebugger().info(String.format("[RP.clearCacheFor] cleared all Caches for %s", uuid));
    }

}
