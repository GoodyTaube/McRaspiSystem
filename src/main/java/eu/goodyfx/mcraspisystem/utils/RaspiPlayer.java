package eu.goodyfx.mcraspisystem.utils;


import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.managers.PlayerSettingsManager;
import eu.goodyfx.mcraspisystem.managers.UserManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SuppressWarnings("unused")
public class RaspiPlayer {

    private final McRaspiSystem plugin;
    private final Player player;

    public RaspiPlayer(McRaspiSystem plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public RaspiPlayer(McRaspiSystem plugin, UUID uuid) {
        this.plugin = plugin;
        this.player = Bukkit.getPlayer(uuid);
    }

    public Player getPlayer() {
        return this.player;
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

    public McRaspiSystem getSystem() {
        return this.plugin;
    }

}
