package eu.goodyfx.mcraspisystem.managers;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;

@SuppressWarnings("unused")
public class ExtraInfos {

    private final File file;
    private final File fileMod;

    private final FileConfiguration configuration;
    private final FileConfiguration modConfig;

    private static final String CONTENT_PATH = ".content";
    private static final String USER_PATH = "User.";
    private final Player player;


    public ExtraInfos(Player player) {
        this.player = player;
        this.file = new File("plugins/" + McRaspiSystem.class.getSimpleName() + "/Extras/", player.getName() + ".yml");
        this.fileMod = new File("plugins/" + McRaspiSystem.class.getSimpleName() + "/Extras/", "ModeratorDummy.yml");
        this.configuration = YamlConfiguration.loadConfiguration(file);
        this.modConfig = YamlConfiguration.loadConfiguration(fileMod);
    }

    private String get(OfflinePlayer target, String id, boolean mod) {

        if (mod) {
            if (containsCheck(modConfig, target, id)) {
                String finalString = modConfig.getString(pathContent(target, id));
                finalString = Objects.requireNonNull(finalString).replace("@", " ");
                return finalString;
            }
            return "";
        }

        if (containsCheck(configuration, target, id)) {
            String finalString = configuration.getString(pathContent(target, id));
            finalString = Objects.requireNonNull(finalString).replace("@", " ");
            return finalString;
        }
        return "";
    }

    private String pathContent(OfflinePlayer target, String id) {
        return String.format("User.%s.%s.content", target.getUniqueId(), id);
    }

    private String pathContent(String... replace) {
        if (replace.length == 1) {
            return String.format("User.%s", replace[0]);
        }
        if (replace.length == 2) {
            return String.format("User.%s.%s", replace[0], replace[1]);
        }
        return String.format("User.%s.%s%s", replace[0], replace[1], replace[2]);
    }

    private boolean containsCheck(FileConfiguration config, OfflinePlayer target, String id) {
        String path = String.format("User.%s.%s", target.getUniqueId(), id);
        return config.contains(path);
    }

    public void remove(OfflinePlayer target, String id, boolean mod) {
        if (mod) {
            modConfig.set(pathContent(target.getUniqueId().toString(), id, CONTENT_PATH), null);
            modConfig.set(pathContent(target.getUniqueId().toString(), id), null);

            if (hasEntry(target, true) && (Objects.requireNonNull(modConfig.getConfigurationSection(pathContent(target.getUniqueId().toString()))).getKeys(false).isEmpty())) {
                modConfig.set(pathContent(target.getUniqueId().toString()), null);
            }
            save(true);
        } else {
            configuration.set(pathContent(target.getUniqueId().toString(), id, CONTENT_PATH), null);
            configuration.set(pathContent(target.getUniqueId().toString(), id), null);
            if (hasEntry(target, false) && (Objects.requireNonNull(configuration.getConfigurationSection(pathContent(target.getUniqueId().toString()))).getKeys(false).isEmpty())) {
                configuration.set(pathContent(target.getUniqueId().toString()), null);
            }
            save(false);

        }
    }

    private void modCheck(OfflinePlayer target) {
        if (player.isPermissionSet("system.team")) {
            if (modConfig.contains(USER_PATH)) {
                if (hasEntry(target, true)) {
                    Set<String> list = Objects.requireNonNull(modConfig.getConfigurationSection(pathContent(target.getUniqueId().toString()))).getKeys(false);
                    if (list.isEmpty()) {
                        return;
                    }
                    StringBuilder builder = new StringBuilder("<gold>Team Infos:<br>");
                    for (String key : list) {
                        builder.append("<gray>-").append(" ").append(get(target, key, true)).append(" ").append(getRemoveDisplay(target, key, true)).append("<br>");
                    }
                    builder.setLength(builder.length() - 4);
                    player.sendRichMessage(builder.toString());
                }
            } else {
                player.sendRichMessage("<red>Mod Infos not Exist.");
            }
        }
    }

    public void getExtraInfos(OfflinePlayer target) {
        modCheck(target); //Check if Asked Player is Moderator
        if (hasEntry(target, false)) {
            StringBuilder builder = new StringBuilder("<green>Private Infos:<br>");
            for (String key : Objects.requireNonNull(configuration.getConfigurationSection(pathContent(target.getUniqueId().toString()))).getKeys(false)) {
                builder.append("<gray>-").append(" ").append(get(target, key, false)).append(" ").append(getRemoveDisplay(target, key, false)).append("<br>");
            }
            builder.setLength(builder.length() - 4);
            player.sendRichMessage(builder + "<br>" + getAddDisplay(target));
        } else {
            player.sendRichMessage(getAddDisplay(target));
        }
    }

    private boolean hasEntry(OfflinePlayer target, boolean mod) {

        if (mod) {
            if (!fileMod.exists()) {
                return false;
            }

            return modConfig.contains(pathContent(target.getUniqueId().toString()));
        }
        if (!file.exists()) {
            return false;
        }
        return configuration.contains(pathContent(target.getUniqueId().toString()));
    }


    public void add(OfflinePlayer target, String content, boolean mod) {
        if (mod) {
            modConfig.set(pathContent(target.getUniqueId().toString(), String.valueOf(generateID()), CONTENT_PATH), content);
            save(true);

        } else {
            configuration.set(pathContent(target.getUniqueId().toString(), String.valueOf(generateID()), CONTENT_PATH), content);
            save(false);

        }

    }


    private long generateID() {
        return System.currentTimeMillis();
    }

    private String getRemoveDisplay(OfflinePlayer target, String id, boolean mod) {
        if (mod) {
            return "<hover:show_text:'<red>Info Löschen.'><white><click:run_command:'/playerinfo " + target.getName() + " removeMod " + id + "'><red>\uD83D\uDDD1<reset>";
        }
        return "<hover:show_text:'<red>Info Löschen.'><white><click:run_command:'/playerinfo " + target.getName() + " remove " + id + "'><red>\uD83D\uDDD1<reset>";
    }

    private String getAddDisplay(OfflinePlayer target) {
        if (player.isPermissionSet("system.team")) {
            return String.format("<hover:show_text:'<green>Füge eine Info hinzu.'><white>[<click:suggest_command:'/playerinfo %1$s add '><green>✎<reset>] " +
                    "<hover:show_text:'<green>Füge eine Mod Info hinzu.'><white>[<click:suggest_command:'/playerinfo %1$s addMod '><gold>✎<reset>]", target.getName());
        }
        return String.format("<hover:show_text:'<green>Füge eine Info hinzu.'><white>[<click:suggest_command:'/playerinfo %s add '><green>✎<reset>]", target.getName());
    }


    /**
     * Saves the YML File
     */
    private void save(boolean mod) {
        try {
            if (mod) {
                modConfig.save(fileMod);
            }
            configuration.save(file);
        } catch (IOException e) {
            JavaPlugin.getPlugin(McRaspiSystem.class).getLogger().log(Level.SEVERE, "Failed to save Extra Infos.", e);
        }
    }


}
