package eu.goodyfx.mcraspisystem.managers;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
    private final UserManager userManager;
    private FileConfiguration config;

    public RequestManager(RaspiModuleManager moduleManager) {
        this.userManager = moduleManager.getUserManager();
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


    public void set(OfflinePlayer denyPlayer, String reason, Player player) {
        userManager.set(denyPlayer, "state", false);
        userManager.set(denyPlayer, "denyPerson", player.getName());
        userManager.set(denyPlayer, "reason", reason);
    }


    public void allow(OfflinePlayer player) {
        userManager.set(player, "allowed-since", new SimpleDateFormat("dd/MM/yyyy").format(new Date(System.currentTimeMillis())));
    }

    public void remove(OfflinePlayer player) {
        userManager.set(player, "state", null);
        userManager.set(player, "denyPerson", null);
        userManager.set(player, "reason", null);
    }


    public boolean isBlocked(OfflinePlayer player) {
        return userManager.contains("state", player);
    }

    public boolean isBlocked(String value, Player player) {
        return userManager.contains(value, player);
    }


    public String getDeny(Player player) {
        String name = (String) userManager.get("denyPerson", player);
        if (Bukkit.getPlayer(name) != null) {
            return plugin.getRaspiPlayer(Bukkit.getPlayer(name)).getName();
        }
        return name;
    }

    public String getReason(OfflinePlayer player) throws NullPointerException {
        return (String) userManager.get("reason", player);
    }


}
