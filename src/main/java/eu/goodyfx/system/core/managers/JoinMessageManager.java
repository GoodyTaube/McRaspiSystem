package eu.goodyfx.system.core.managers;

import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class JoinMessageManager {

    private final UtilityFileManager manager;

    public JoinMessageManager(RaspiModuleManager moduleManager) {
        this.manager = new UtilityFileManager(moduleManager.getPlugin(), "config.yml");
    }

    public String get(Player player) {
        String name = player.getName();
        RaspiPlayer raspiPlayer = Raspi.players().get(player);
        if (Boolean.TRUE.equals(manager.contains("join.groups"))) {
            for (String groups : Objects.requireNonNull(manager.config().getConfigurationSection("join.groups")).getKeys(false)) {
                String groupPath = "join.groups.";
                if (Boolean.TRUE.equals(manager.contains(groupPath + groups + ".container"))) {
                    List<String> userContainer = manager.config().getStringList(groupPath + groups + ".container");
                    if (userContainer.contains(name)) {
                        return Objects.requireNonNull(manager.config().getString(groupPath + groups + ".message")).replace("{player}", raspiPlayer.getColorName());
                    }
                }
            }
        }
        return Objects.requireNonNull(manager.config().getString("join.default-welcome-message")).replace("{player}", raspiPlayer.getColorName());
    }


}
