package eu.goodyfx.system.core.database;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.PlayerNameController;
import eu.goodyfx.system.core.utils.RaspiPermission;
import eu.goodyfx.system.core.utils.RaspiSounds;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RaspiPlayer implements RaspiUserContext {

    @Getter
    private final Player player;
    private final RaspiAccount raspiAccount;
    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    public final PlayerNameController nameController;


    public RaspiPlayer(Player player, RaspiAccount account) {
        this.player = player;
        this.raspiAccount = account;
        this.nameController = new PlayerNameController(this);
    }

    @Override
    public UUID getUUID() {
        return player.getUniqueId();
    }

    @Override
    public RaspiAccount account() {
        return this.raspiAccount;
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    public void openInventory(InventoryView view) {
        player.openInventory(view);
    }

    public void openInventory(Inventory view) {
        player.openInventory(view);
    }

    public String getPrefix() {
        String prefix = userData().getPrefix();
        if (prefix != null) {
            prefix = prefix.replace("@", " ");
        }
        return prefix;
    }

    public void setPrefix(String db_prefix) {
        account().getRaspiUser().setPrefix(db_prefix);
        nameController.setPlayerList();
    }

    public void removePrefix() {
        account().getRaspiUser().setPrefix(null);
        nameController.setPlayerList();
    }


    /**
     * Get Current Player Color
     *
     * @return The current Color String
     */
    public String getColor() {
        return account().getRaspiUser().getColor();
    }

    /**
     * Final DisplayName for McRaspi User
     *
     * @return Name Display with Prefix if set
     */
    public String getDisplayName() {
        return nameController.getColorDisplayName();
    }

    public String getColorName() {
        return nameController.getColorName();
    }


    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(player.getUniqueId());
    }


    /**
     * Send player a Message with Component API
     *
     * @param message The Plain Text Message
     */
    public void sendMessage(@Nullable String message) {
        if (message == null) {
            player.sendMessage(Component.empty());
            return;
        }
        player.sendMessage(MiniMessage.miniMessage().deserialize(message));
    }

    public String convertLink(String url) {
        return String.format("<click:open_url:'%s'>%s", url, url);
    }

    public String convertLink(String url, String linkDisplay) {
        return String.format("<click:open_url:'%s'>%s", url, linkDisplay);
    }

    /**
     * Checks if player has played Time by value
     *
     * @param amount Amount in hours
     * @return True if player has played more than valued Hours
     */
    public boolean hasTimePlayed(int amount) {
        long timePlayed = player.getStatistic(Statistic.PLAY_ONE_MINUTE); //ticks Played 20 Ticks = 1 Second
        long timeHours = timePlayed / 20 / 60 / 60;
        plugin.getDebugger().info(String.format("TIME_REQUEST:: %s Spielzeit: %s. Soll-Mindestens: %s.", player.getName(), timeHours, amount));
        return timePlayed / 20 / 60 / 60 > amount;
    }


    /**
     * Checks if player has raspiPermission
     *
     * @param raspiPermission The User Permission
     * @return true if Player has permission
     */
    public boolean hasPermission(RaspiPermission raspiPermission) {
        return player.isPermissionSet(raspiPermission.getPermissionValue());
    }

    /**
     * Checks if this object contains an override for the specified
     * permission, by fully qualified name
     *
     * @param permission Name of the permission
     * @return true if the permission is set, otherwise false
     */
    public boolean hasPermission(String permission) {
        return player.isPermissionSet(permission);
    }


    /**
     * Send player a Message with Component API
     *
     * @param message The Plain Text Message
     */
    public void sendMessage(@Nullable String message, boolean prefix) {
        if (message == null) {
            player.sendMessage(Component.empty());
            return;
        }
        StringBuilder builder = new StringBuilder();
        if (prefix) {
            builder.append(plugin.getModule().getRaspiMessages().getPrefix());
        }
        builder.append(message);
        player.sendMessage(MiniMessage.miniMessage().deserialize(builder.toString()));
    }

    public void sendActionBar(String message) {
        player.sendActionBar(MiniMessage.miniMessage().deserialize(message));
    }


    public McRaspiSystem getSystem() {
        return this.plugin;
    }


    /**
     * Plays a sound for the player at their current location with the specified volume and pitch.
     *
     * @param sound  The sound to be played.
     * @param volume The volume at which to play the sound. Must be a positive float value.
     * @param pitch  The pitch at which to play the sound. Must be a positive float value.
     */
    public void playSound(RaspiSounds sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound.getSound(), volume, pitch);
    }

    /**
     * Plays a sound for the player at their current location.
     *
     * @param sound The sound to be played, encapsulating the sound type, volume, and pitch.
     */
    public void playSound(RaspiSounds sound) {
        player.playSound(player.getLocation(), sound.getSound(), sound.getVolume(), sound.getPitch());
    }

    /**
     * Get Player current Location
     *
     * @return The Player Location
     */
    public Location getLocation() {
        return player.getLocation();
    }

    /**
     * Check if player is Default
     *
     * @return TRUE if player is NEWBIE
     */
    public boolean isDefault() {
        return player.isPermissionSet("group.default") && !player.isPermissionSet("group." + plugin.getConfig().getString("Utilities.playerGroup"));
    }

    /**
     * Sends a formatted Debug Message with "DEBUG://" PREFIX
     *
     * @param message The Debug Message
     */
    public void sendDebugMessage(String message) {
        player.sendRichMessage(String.format("<dark_red>DEBUG:// <gray>%s", message));
    }

    /**
     * Perform player Command
     *
     * @param command The Command without "/"
     */
    public void performCommand(String command) {
        Bukkit.dispatchCommand(player, command.replace("/", ""));
    }


}
