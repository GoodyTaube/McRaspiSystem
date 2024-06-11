package eu.goodyfx.mcraspisystem.managers;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.utils.PlayerValues;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PrefixManager {

    private final Map<UUID, String> prefixContainer = new HashMap<>();
    private final UserManager userManager;

    public PrefixManager(RaspiModuleManager module) {
        this.userManager = module.getUserManager();
        Bukkit.getLogger().info("Loading Prefix Manager.");
    }

    /**
     * Checks the yml file for any Prefix Data
     * Overrides the current one in Persistant if set**
     */
    public void checkOld(Player player) {
        if (this.userManager.contains("prefix", player)) {
            set(player, (String) userManager.get("prefix", player));
            userManager.remove("prefix", player);
            Bukkit.getLogger().info("Prefix update für " + player.getName() + " Abgeschlossen. (Änderung aus Config)");
        }
    }

    public void set(Player player, String prefixString) {
        update(prefixString, player);
        userManager.setPersistantValue(player, PlayerValues.PREFIX, prefixString);
    }


    public void updateUser(OfflinePlayer player) {
        if (player.isOnline()) {
            if (userManager.contains("prefix", player)) {
                set(player.getPlayer(), (String) userManager.get("prefix", player));
                userManager.remove("prefix", player);
            }
        }
    }

    public void remove(Player player) {
        prefixContainer.remove(player.getUniqueId());
        userManager.removePersistantValue(player, PlayerValues.PREFIX);
    }

    public String get(Player player) {

        String prefix = "";

        if (prefixContainer.containsKey(player.getUniqueId())) {
            prefix = prefixContainer.get(player.getUniqueId());
        }
        if (userManager.hasPersistantValue(player, PlayerValues.PREFIX)) {
            prefix = userManager.getPersistantValue(player, PlayerValues.PREFIX, String.class);

        }
        assert prefix != null;

        if (prefix.isEmpty()) {
            return prefix;
        }

        prefix = prefix.replaceAll("@", " ");

        return prefix;
    }

    public boolean exist(Player player) {
        return userManager.hasPersistantValue(player, PlayerValues.PREFIX);
    }


    /**
     * Performance reason List add //OLD
     *
     * @param prefixString The Prefix
     */
    private void update(String prefixString, Player player) {
        prefixContainer.put(player.getUniqueId(), prefixString);
    }


}
