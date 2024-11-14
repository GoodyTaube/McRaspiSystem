package eu.goodyfx.mcraspisystem.managers;


import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.utils.PlayerValues;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

@SuppressWarnings("unused")
public class UserManager {

    private final List<UUID> muteContainer = new ArrayList<>();
    private final Map<UUID, Location> afkContainer = new HashMap<>();

    private final File file;
    private FileConfiguration config;
    private final McRaspiSystem plugin;

    public UserManager(McRaspiSystem plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "UserDB.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void reloadFile() {
        Validate.notNull(file, "File not Exist!");
        config = YamlConfiguration.loadConfiguration(file);
        final InputStream stream = plugin.getResource("UserDB.yml");
        if (stream == null) {
            return;
        }
        config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8)));
    }

    private void saveFile() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while Save UserDB", e);
        }
    }

    /**
     * Checks if player has played Time by value
     *
     * @param amount Amount in hours
     * @return True if player has played more than valued Hours
     */
    public boolean hasTimePlayed(Player player, int amount) {
        long timePlayed = player.getStatistic(Statistic.PLAY_ONE_MINUTE); //ticks Played 20 Ticks = 1 Second
        return timePlayed / 60 / 60 / 20 >= amount;

    }

    private UUID getUUID(OfflinePlayer player) {
        return player.getUniqueId();
    }

    /**
     * Converts offlinePlayer
     *
     * @param player The Player
     * @return The OfflinePlayer off {@param player}
     */
    public OfflinePlayer convertOfflinePlayer(Player player) {
        return Bukkit.getOfflinePlayer(player.getUniqueId());
    }

    //Standard Update
    public void update(OfflinePlayer player) {
        if (!config.contains("User." + getUUID(player))) {
            setupPlayerData(player);
            update(player);
            return;
        }
        if (!contains("firstDoc", player)) {
            setupPlayerData(player);
        }

        if (!contains("playerColor", player)) {
            set(player, "playerColor", "<random>");
        }

        if (player.getPlayer() != null && (!hasPersistantValue(player.getPlayer(), PlayerValues.DEATH))) {
            setPersistantValue(player.getPlayer(), PlayerValues.DEATH, 2L);
        }
        reloadFile();
        checkCurrentUsername(player);
    }

    public void updateAFK(Player player) {
        if (hasPersistantValue(player, PlayerValues.AFK)) {
            afkContainer.remove(player.getUniqueId());
            removePersistantValue(player, PlayerValues.AFK);
        } else {
            afkContainer.put(player.getUniqueId(), player.getLocation());
            setPersistantValue(player, PlayerValues.AFK, 1);
        }
    }

    public Map<UUID, Location> getAfkContainer() {
        return this.afkContainer;
    }

    public void setupPlayerData(OfflinePlayer player) {
        List<String> usernames = new ArrayList<>();
        usernames.add(player.getName());
        config.set("User." + getUUID(player) + ".userName", player.getName());
        config.set("User." + getUUID(player) + ".userNames", usernames);
        config.set("User." + getUUID(player) + ".firstDoc", System.currentTimeMillis());
        saveFile();
    }

    /**
     * Check if User is Muted currently.
     *
     * @param player The current Player
     * @return True if player is Muted
     */
    public boolean isMuted(OfflinePlayer player) {
        if (!muteContainer.contains(player.getUniqueId())) {
            if (contains("muted", player)) {
                muteContainer.add(player.getUniqueId());
                return true;
            } else {
                return false;
            }
        }
        return true;
    }


    /**
     * Updates the mute status of User to TRUE
     *
     * @param player The current Player
     * @param muter  The mute performer
     * @param reason The raw Reason
     */
    public void muteUser(OfflinePlayer player, @NotNull CommandSender muter, String reason) {
        set(player, "muted", true);
        set(player, "muteReason", reason);
        set(player, "muteOwner", muter.getName());
        muteContainer.add(player.getUniqueId());
    }

    /**
     * Updates the mute status of User to FALSE
     *
     * @param player The current Player
     */
    public void unMuteUser(OfflinePlayer player) {
        remove("muted", player);
        remove("muteReason", player);
        remove("muteOwner", player);
        muteContainer.remove(player.getUniqueId());
    }


    /**
     * Allow a player to Play on Server
     *
     * @param player  The OfflinePlayer to Allow
     * @param allower The Allower
     */
    public void allowPlayer(OfflinePlayer player, @Nullable Player allower) {
        if (allower != null) {
            set(player, "allower", allower.getName());
        }
        set(player, "allowed", true);
    }

    /**
     * Check if User is Allowed to Play
     *
     * @param player The current Player
     * @return True if User is Allowed
     */
    public boolean isAllowed(OfflinePlayer player) {
        return !contains("denyPerson", player);
    }

    public long lastSeen(OfflinePlayer player) {
        if (contains("last", player)) {
            return (System.currentTimeMillis() - (long) get("last", player));
        }
        return 0;
    }

    public void lastSeen(OfflinePlayer player, long value) {
        set(player, "last", value);
    }

    /**
     * Checks the current username and Set the optional new one.
     *
     * @param player The checked Player
     */
    private void checkCurrentUsername(OfflinePlayer player) {
        reloadFile();
        //Update UserName
        if (!Objects.requireNonNull(config.getString("User." + getUUID(player) + ".userName")).equalsIgnoreCase(player.getName())) {
            config.set("User." + getUUID(player) + ".userName", player.getName());
            saveFile();
            updateUserNames(player);
        }

    }

    /**
     * Updates the current userName and add the last to History
     *
     * @param player The current Player
     */
    private void updateUserNames(OfflinePlayer player) {
        List<String> usernames = config.getStringList("User." + getUUID(player) + ".userNames");
        usernames.add(player.getName());
        config.set("User." + getUUID(player) + ".userNames", usernames);
        saveFile();
    }

    /**
     * Get UserName History of current Player
     *
     * @return The UserName History
     */
    public List<String> getUserNames(OfflinePlayer player) {
        return config.getStringList("User." + player.getUniqueId() + ".userNames");
    }

    /**
     * Get all registered UUIDS
     *
     * @return All UUID's
     */
    public List<String> getAllUsersUUID() {
        return new ArrayList<>(Objects.requireNonNull(config.getConfigurationSection("User")).getKeys(false));
    }

    /**
     * Get all registered UserNames
     *
     * @return All UserNames
     */
    public List<String> getAllUsers() {
        List<String> result = new ArrayList<>();
        for (String key : Objects.requireNonNull(config.getConfigurationSection("User")).getKeys(false)) {

            result.add(config.getString("User." + key + ".userName"));
        }
        return result;
    }


    /**
     * Gets a List of UserNames with specific value set.
     *
     * @param toCheck The value to Check
     * @return List of UserNames with value set
     */
    public List<String> getAllUsersWhoSets(String toCheck) {
        List<String> users = new ArrayList<>();
        for (String user : getAllUsersUUID()) {
            if (config.contains("User." + user + "." + toCheck)) {
                users.add(config.getString("User." + user + ".userName"));
            }
        }
        return users;
    }

    /**
     * Get the current UserDB.yml
     *
     * @return The current UserDB.yml
     */
    public FileConfiguration getConfigurationFile() {
        reloadFile();
        return config;
    }


    /**
     * Get value from User
     *
     * @param subPath The value to get
     * @param player  The current Player
     * @return The value
     */
    public Object get(String subPath, OfflinePlayer player) {
        return config.get("User." + getUUID(player) + "." + subPath);
    }

    public List<?> getList(String subPath, OfflinePlayer player) {
        return config.getList("User." + getUUID(player) + "." + subPath);
    }

    /**
     * Set value to User
     *
     * @param player  The current Player
     * @param subPath The path
     * @param obj     The value
     */
    public void set(OfflinePlayer player, String subPath, @Nullable Object obj) {
        config.set("User." + getUUID(player) + "." + subPath, obj);
        saveFile();
    }

    public void set(Player player, String subPath, @Nullable Object obj) {
        config.set("User." + getUUID(player) + "." + subPath, obj);
        saveFile();
    }

    /**
     * Remove value from User
     *
     * @param sub    The path
     * @param player The current Player
     */
    public void remove(String sub, OfflinePlayer player) {
        set(player, sub, null);
        saveFile();
    }

    /**
     * Check if Player is Registered.
     *
     * @param player The requested Player
     * @return True if player is Registered,
     * False if player is unknown
     */
    public boolean userExist(OfflinePlayer player) {
        reloadFile();
        return config.contains("User." + player.getUniqueId());
    }

    /**
     * Check if User has Value set
     *
     * @param subPath The Value
     * @param player  The current Player
     * @return True if value is set
     */
    public boolean contains(String subPath, OfflinePlayer player) {
        return config.contains("User." + getUUID(player) + "." + subPath);
    }


    //PERSISTANT_START
    // Persistant is important to store values in Objects/OfflinePlayers (Reload Ignore)

    /**
     * Check if player has Defined value {@link PlayerValues}
     *
     * @param player  The OfflinePlayer to test
     * @param askType The Defined Value
     * @return True if player has Defined Value set.
     */
    public boolean hasPersistantValue(Player player, PlayerValues askType) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        for (PlayerValues perVal : PlayerValues.values()) {
            if (askType.equals(perVal)) {
                return container.has(plugin.getNameSpaced(perVal.getLabel()));
            }
        }
        return false;
    }

    /**
     * Save a value in OfflinePlayer by defined Values {@link PlayerValues}
     *
     * @param player  The OfflinePlayer to apply
     * @param askType The PersistentDataType
     * @param val     The value to Store
     */
    public void setPersistantValue(Player player, PlayerValues askType, Object val) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        if (askType.getPersistentDataType().equals(PersistentDataType.INTEGER)) {
            container.set(plugin.getNameSpaced(askType.getLabel()), PersistentDataType.INTEGER, (Integer) val);
        } else if (askType.getPersistentDataType().equals(PersistentDataType.STRING)) {
            container.set(plugin.getNameSpaced(askType.getLabel()), PersistentDataType.STRING, (String) val);
        } else if (askType.getPersistentDataType().equals(PersistentDataType.LONG)) {
            container.set(plugin.getNameSpaced(askType.getLabel()), PersistentDataType.LONG, (long) val);
        }
    }

    /**
     * Get a Defined value "{@link PlayerValues}" of OfflinePlayer if Stored.
     *
     * @param player The OfflinePlayer to get Value.
     * @param values The Defined Value
     * @return NULL if no Value is Set. The Object//String or Intger
     */
    public @Nullable <T> T getPersistantValue(Player player, PlayerValues values, Class<T> clazz) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        if (hasPersistantValue(player, values)) {
            return clazz.cast(container.get(plugin.getNameSpaced(values.getLabel()), values.getPersistentDataType()));
        }
        return null;
    }

    /**
     * Remove Definded Value {@link PlayerValues} of OfflinePlayer
     *
     * @param player  The OfflinePlayer
     * @param askType The Defined Value
     */
    public void removePersistantValue(Player player, PlayerValues askType) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        if (hasPersistantValue(player, askType)) {
            container.remove(plugin.getNameSpaced(askType.getLabel()));
        }
    }

    //PERSISTENT END

}
