package eu.goodyfx.system.core.managers;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.database.RaspiUser;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiOfflinePlayer;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class RequestManager {

    private final File file;
    private final McRaspiSystem plugin;
    private UUID uuid;
    private FileConfiguration config;

    public RequestManager(RaspiModuleManager moduleManager) {
        this.file = new File(moduleManager.getPlugin().getDataFolder(), "reasons.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        this.plugin = moduleManager.getPlugin();
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getHookManager().getDiscordIntegration().sendError(this.getClass(), e);
        }
    }

    public void reload() {
        Validate.notNull(file, "File not Exist!");
        config = YamlConfiguration.loadConfiguration(file);
        final InputStream defConfigStream = plugin.getResource("join.yml");
        if (defConfigStream == null) {
            return;
        }
        config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)));
    }

    public void addReason(String reason) {
        List<String> reasons = new ArrayList<>(config.getStringList("reasons"));
        if (!reasons.contains(reason)) {
            reasons.add(reason);
            config.set("reasons", reasons);
            save();

        }
    }

    public List<String> getReasons() {
        return new ArrayList<>(config.getStringList("reasons"));
    }


    public void set(RaspiOfflinePlayer denyPlayer, String reason, Player player) {
        remove(denyPlayer.getRaspiUser());
        denyPlayer.getRaspiUser().setDenied_by(player.getName());
        denyPlayer.getRaspiUser().setState(false);
        denyPlayer.getRaspiUser().setDeny_reason(reason);
    }


    public void allow(RaspiUser player) {
        remove(player);
        player.setAllowed_since(new SimpleDateFormat("dd/MM/yyyy").format(new Date(System.currentTimeMillis())));
    }

    public void remove(RaspiUser player) {
        player.setState(null);
        player.setDenied_by(null);
        player.setDeny_reason(null);
        player.setAllowed_since(null);
        player.setAllowed_by(null);
    }


    public boolean isBlocked(RaspiUser player) {
        return player.getDenied_by() != null;
    }

    public String getDeny(RaspiUser player) {
        if (isBlocked(player)) {
            return Raspi.players().get(Bukkit.getPlayer(player.getDenied_by())).getColorName();
        }
        return null;
    }

    public String getReason(RaspiUser player) {
        if (isBlocked(player)) {
            return player.getDeny_reason();
        }
        return null;
    }


}
