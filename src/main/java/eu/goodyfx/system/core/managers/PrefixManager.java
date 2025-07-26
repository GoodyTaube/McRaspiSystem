package eu.goodyfx.system.core.managers;

import eu.goodyfx.system.McRaspiSystem;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PrefixManager {

    private final Map<UUID, String> prefixContainer = new HashMap<>();
    private final UserManager userManager;
    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    public PrefixManager(RaspiModuleManager module) {
        this.userManager = module.getUserManager();
    }


    public void set(Player player, String db_String) {
        userManager.set(player.getPlayer(), "prefix", db_String);
        prefixContainer.put(player.getUniqueId(), db_String);
        plugin.getModuleManager().getPlayerNameController().setPlayerList(player);
    }

    public String get(Player player) {
        String prefix;
        if (prefixContainer.containsKey(player.getUniqueId())) {
            prefix = prefixContainer.get(player.getUniqueId());
        } else {
            if (userManager.get("prefix", player, String.class) != null) {
                prefix = userManager.get("prefix", player, String.class);
            } else {
                prefix = "";
            }
            prefixContainer.put(player.getUniqueId(), prefix);
        }
        assert prefix != null;
        prefix = prefix.replace("@", " ");
        return prefix;
    }

    public void remove(Player player) {
        prefixContainer.remove(player.getUniqueId()); //Container remove
        userManager.remove("prefix", player.getPlayer());
        plugin.getModuleManager().getPlayerNameController().setPlayerList(player);
    }


}
