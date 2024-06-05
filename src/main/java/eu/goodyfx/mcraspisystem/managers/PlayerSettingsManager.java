package eu.goodyfx.mcraspisystem.managers;

import eu.goodyfx.goodysutilities.utils.Settings;
import eu.goodyfx.mcraspisystem.McRaspiSystem;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSettingsManager {

    private final UserManager userManager;

    public PlayerSettingsManager(McRaspiSystem plugin) {
        this.userManager = plugin.getUserManager();
    }


    private final Map<UUID, Map<Settings, Boolean>> settingsContainer = new HashMap<>();

    public void set(Settings settings, Player player) {
        userManager.set(player, settings.getLabel(), true);
    }

    public void remove(Settings settings, Player player) {
        userManager.remove(settings.getLabel(), player);
    }

    public boolean contains(Settings settings, Player player) {
        if (!settingsContainer.containsKey(player.getUniqueId())) {
            refresh(player);
        }
        Map<Settings, Boolean> settingsHashMap = settingsContainer.get(player.getUniqueId());
        return settingsHashMap.get(settings);
    }

    public void refresh(Player player) {
        HashMap<Settings, Boolean> containerPre = new HashMap<>();
        for (Settings setting : Settings.values()) {
            if (userManager.contains(setting.getLabel(), player)) {
                containerPre.put(setting, true);
            } else {
                containerPre.put(setting, false);
            }
        }
        settingsContainer.put(player.getUniqueId(), containerPre);
    }


}
