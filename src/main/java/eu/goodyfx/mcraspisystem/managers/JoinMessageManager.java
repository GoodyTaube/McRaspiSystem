package eu.goodyfx.mcraspisystem.managers;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.utils.PlayerNameController;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class JoinMessageManager {

    private final PlayerNameController controller;
    private final UtilityFileManager manager;

    public JoinMessageManager(McRaspiSystem plugin) {
        this.controller = plugin.getPlayerNameController();
        this.manager = new UtilityFileManager(plugin, "join.yml");
    }
    public String get(Player player) {
        String name = player.getName();
        if (Boolean.TRUE.equals(manager.contains("groups"))) {
            for (String groups : Objects.requireNonNull(manager.config().getConfigurationSection("groups")).getKeys(false)) {
                String groupPath = "groups.";
                if (Boolean.TRUE.equals(manager.contains(groupPath + groups + ".container"))) {
                    List<String> userContainer = manager.config().getStringList(groupPath + groups + ".container");
                    if (userContainer.contains(name)) {
                        return Objects.requireNonNull(manager.config().getString(groupPath + groups + ".message")).replace("{player}", controller.getName(player));
                    }
                }
            }
        }
        return Objects.requireNonNull(manager.config().getString("default-welcome-message")).replace("{player}", controller.getName(player));
    }


}
