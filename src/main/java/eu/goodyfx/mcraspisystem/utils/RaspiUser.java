package eu.goodyfx.mcraspisystem.utils;

import eu.goodyfx.mcraspisystem.managers.UtilityFileManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class RaspiUser {

    private final Player player;
    private final UUID uuid;
    private final UtilityFileManager fileManager;

    private final String userPath = "User.";

    public RaspiUser(RaspiPlayer player) {
        this.player = player.getPlayer();
        this.uuid = player.getUUID();
        this.fileManager = new UtilityFileManager(player.getSystem(), "UserDB.yml");
    }

    public String getColor() {
        return fileManager.get(userPath + uuid + ".playerColor", String.class);
    }

    public List<String> getUserNames() {
        return fileManager.getStringList(userPath + uuid + ".userNames");
    }

    public Long getFirstDoc() {
        return Long.valueOf(fileManager.get(userPath + uuid + ".firstDoc", String.class));
    }

    public boolean isMuted() {
        return fileManager.contains(".mute") && fileManager.get(userPath + uuid + ".mute", Boolean.class);
    }

    public OfflinePlayer getMuteOwner() {
        if (!isMuted()) {
            return null;
        }
        return Bukkit.getOfflinePlayer(fileManager.get(userPath + uuid + ".muteOwner", String.class));
    }

    public String getMuteReason() {
        if (!isMuted()) {
            return "";
        }
        return fileManager.get(userPath + uuid + ".muteReason", String.class).replace("@", " ");
    }

    public boolean isDenied() {
        return fileManager.contains(userPath + uuid + ".state");
    }


}
