package eu.goodyfx.mcraspi.core.database;

import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.utils.PlayerNameController;
import eu.goodyfx.mcraspi.core.utils.RaspiFormatting;
import eu.goodyfx.mcraspi.core.utils.RaspiPermission;
import eu.goodyfx.mcraspi.core.utils.RaspiSounds;
import lombok.Getter;
import lombok.Setter;
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

    @Getter
    @Setter
    public boolean afk = false;


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

    //================================================================================================
    //                                        Message
    //================================================================================================

    /**
     * Master Method to send Player chat Message as Component
     *
     * @param message    The RAW message
     * @param prefix     Set true for prefix before Message
     * @param onlyColor  Set true to limit input for only Colors
     * @param raspiSound Set {@link RaspiSounds} to play Sound after sending Message
     */
    public void sendMessage(@Nullable String message, boolean prefix, boolean onlyColor, @Nullable RaspiSounds raspiSound) {
        if (message == null) {
            player.sendMessage(Component.empty());
            return;
        }
        //Message Parsing
        MiniMessage parser = onlyColor ? RaspiFormatting.COLOR_ONLY_MESSAGE : MiniMessage.miniMessage();
        Component parsedMessage = parser.deserialize("<gray> " + message);
        if (!prefix) {
            //Message without prefix
            player.sendMessage(parsedMessage);
            return;
        }
        Component prefixComponent = MiniMessage.miniMessage().deserialize(plugin.getModule().getRaspiMessages().getPrefix());
        //Message with prefix
        player.sendMessage(prefixComponent.append(parsedMessage));
        if (raspiSound != null) {
            //Play Raspi Sound
            playSound(raspiSound);
        }
    }

    public void sendMessage(@Nullable String message) {
        sendMessage(message, false, false, null);
    }

    public void sendMessage(@Nullable String message, boolean prefix) {
        sendMessage(message, prefix, false, null);
    }

    public void sendMessage(@Nullable String message, boolean prefix, boolean onlyColor) {
        sendMessage(message, prefix, onlyColor, null);
    }

    public void sendMessage(String message, boolean prefix, RaspiSounds raspiSounds) {
        sendMessage(message, prefix, false, raspiSounds);
    }

    public void sendMessage(Component message) {
        sendMessage(message, false);
    }

    public void sendMessage(Component message, boolean prefix) {
        if (!prefix) {
            player.sendMessage(message);
            return;
        }
        Component prefixComponent = MiniMessage.miniMessage().deserialize(plugin.getModule().getRaspiMessages().getPrefix());
        player.sendMessage(prefixComponent.append(message));
    }

    public void sendActionBar(String message) {
        player.sendActionBar(MiniMessage.miniMessage().deserialize(message));
    }


    //=======================================================================================================

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
