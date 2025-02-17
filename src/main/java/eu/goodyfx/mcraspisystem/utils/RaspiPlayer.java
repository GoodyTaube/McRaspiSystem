package eu.goodyfx.mcraspisystem.utils;


import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.managers.PlayerSettingsManager;
import eu.goodyfx.mcraspisystem.managers.UserManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SuppressWarnings("unused")
@Getter
public class RaspiPlayer {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    private final Player player;

    public RaspiPlayer(Player player) {
        this.player = player;
    }

    public RaspiPlayer(UUID uuid) {
        this.player = Bukkit.getPlayer(uuid);
    }


    public void openInventory(Inventory inventory) {
        getPlayer().openInventory(inventory);
    }

    public void openInventory(InventoryView view) {
        getPlayer().openInventory(view);
    }

    public String getPrefix() {
        return plugin.getModule().getPrefixManager().get(player);
    }

    public boolean hasSetting(Settings setting) {
        return plugin.getModule().getPlayerSettingsManager().contains(setting, player);
    }

    public PlayerNameController nameController() {
        return plugin.getModule().getPlayerNameController();
    }

    public PlayerSettingsManager getPlayerSettingsManager() {
        return plugin.getModule().getPlayerSettingsManager();
    }

    /**
     * Get Player Name with color
     *
     * @return The Player Name With color
     */
    public String getName() {
        return nameController().getName(player);
    }

    /**
     * Get Current Player Color
     *
     * @return The current Color String
     */
    public String getColor() {
        return nameController().getColorString(player);
    }

    /**
     * Final DisplayName for McRaspi User
     *
     * @return Name Display with Prefix if set
     */
    public String getDisplayName() {
        return nameController().getNameDisplay(player);
    }

    /**
     * UserManager Referenz
     *
     * @return The User Manager
     */
    public UserManager userManager() {
        return plugin.getModule().getUserManager();
    }


    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(player.getUniqueId());
    }

    public UUID getUUID() {
        return player.getUniqueId();
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
        getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(message));
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
        getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(builder.toString()));
    }

    public void sendActionBar(String message) {
        getPlayer().sendActionBar(MiniMessage.miniMessage().deserialize(message));
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


}
