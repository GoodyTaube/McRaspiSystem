package eu.goodyfx.system.core.managers;

import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.database.RaspiPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class JoinMessageManager {

    private final UtilityFileManager manager;

    public JoinMessageManager(RaspiModuleManager moduleManager) {
        this.manager = new UtilityFileManager(moduleManager.getPlugin(), "config.yml");
    }

    public String get(RaspiPlayer player) {
        String name = player.getPlayer().getName();
        AtomicReference<String> ret = new AtomicReference<>("");

        if (Boolean.TRUE.equals(manager.contains("join.groups"))) {
            for (String groups : Objects.requireNonNull(manager.config().getConfigurationSection("join.groups")).getKeys(false)) {
                String groupPath = "join.groups.";
                if (Boolean.TRUE.equals(manager.contains(groupPath + groups + ".container"))) {
                    List<String> userContainer = manager.config().getStringList(groupPath + groups + ".container");
                    if (userContainer.contains(name)) {
                        ret.set(Objects.requireNonNull(manager.config().getString(groupPath + groups + ".message")).replace("{player}", player.getColorName()));
                        return ret.get();
                    }
                }
            }
        }

        ret.set(Objects.requireNonNull(manager.config().getString("join.default-welcome-message")).replace("{player}", player.getColorName()));
        return ret.get();
    }


}
