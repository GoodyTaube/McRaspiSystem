package eu.goodyfx.mcraspisystem.managers;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import org.bukkit.OfflinePlayer;

public class TimeDBManager {

    private UtilityFileManager config;

    public TimeDBManager(McRaspiSystem system){
        this.config = new UtilityFileManager(system, "timeDB.yml");
    }

    public void add(OfflinePlayer player, long val){
        config.set("User." + player.getUniqueId() + ".timePlayed", val);
    }

    public Long get(OfflinePlayer player){
        return config.getConfig().getLong("User." +player.getUniqueId() + ".timePlayed");
    }

    public boolean contains(OfflinePlayer player){
        return config.contains("User." + player.getUniqueId() +".timePlayed");
    }

}
