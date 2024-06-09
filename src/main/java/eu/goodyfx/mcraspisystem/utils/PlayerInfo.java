package eu.goodyfx.mcraspisystem.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.managers.UserManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerInfo {

    private final OfflinePlayer player;
    private final UserManager userManager;

    private final McRaspiSystem plugin;

    private final List<String> playerInfoAssets = new ArrayList<>();

    public PlayerInfo(McRaspiSystem plugin, OfflinePlayer player) {
        this.plugin = plugin;
        this.userManager = plugin.getModule().getUserManager();
        this.player = player;

        parseGroups();
        registration();
        firstDocumentation();
        lastNames();
        timePlayed();
        lastDeath();
        lastSeen();
        playerXP();
        playerServerInfos();
    }

    private void parseValueToInfo(PlayerInfosValues info, String value) {
        playerInfoAssets.add(String.format("%s %s<reset><br>", info.getLabel(), value));
    }

    public String buildPlayerInfos() {
        StringBuilder builder = new StringBuilder("<gray>Player Infos: <aqua>" + player.getName() + "<reset><br>");
        for (String inf : playerInfoAssets) {
            builder.append(inf);
        }
        builder.setLength(builder.length() - 4);
        return builder.toString();
    }

    private void registration() {
        Boolean state = userManager.contains("allowed-since", player);
        String val;
        if (Boolean.TRUE.equals(state)) {
            val = "<green>Erfolgte";
            parseValueToInfo(PlayerInfosValues.REGISTRATION, val);
        }
    }

    private void parseGroups() {

        StringBuilder builder = new StringBuilder();
        Set<String> groups = new HashSet<>();
        User user = plugin.getHookManager().getLuckPerms().getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            groups = user.getNodes(NodeType.INHERITANCE)
                    .stream()
                    .map(InheritanceNode::getGroupName)
                    .collect(Collectors.toSet());
        }
        for (String group : groups) {
            builder.append(group).append(",").append(" ");
        }
        if (builder.length() > 1) {
            builder.setLength(builder.length() - 2);
        } else {
            builder.append("<red>Kein Eintrag");
        }
        if (player.isOnline()) {
            parseValueToInfo(PlayerInfosValues.GROUPS, builder.toString());
        }
    }

    private void firstDocumentation() {
        parseValueToInfo(PlayerInfosValues.FIRST_JOIN, new SimpleDateFormat("dd/MM/yyyy").format(userManager.get("firstDoc", player)));
    }

    private void lastNames() {
        List<String> names = userManager.getUserNames(player);
        StringBuilder builder = new StringBuilder();
        for (String val : names) {
            if (val.equalsIgnoreCase(player.getName())) {
                continue;
            }
            builder.append(val).append(",").append(" ");
        }
        if (builder.length() > 1) {
            builder.setLength(builder.length() - 2);
            parseValueToInfo(PlayerInfosValues.LAST_NAMES, builder.toString());
        }
    }

    private void playerServerInfos() {
        if (userManager.contains("muted", player)) {
            parseValueToInfo(PlayerInfosValues.MUTED, (String) userManager.get("muteReason", player));
        }
        PlayerProfile profile = Bukkit.createProfile(player.getUniqueId());
        BanList<PlayerProfile> entry = Bukkit.getBanList(BanList.Type.PROFILE);
        if (plugin.getModule().getPlayerBanManager().contains(player)) {
            parseValueToInfo(PlayerInfosValues.BANNED, plugin.getModule().getPlayerBanManager().reason(player));
        }
        if (entry.isBanned(profile)) {
            parseValueToInfo(PlayerInfosValues.BANNED, entry.getBanEntry(profile).getReason());
        }
        if (plugin.getModule().getRequestManager().isBlocked(player)) {
            parseValueToInfo(PlayerInfosValues.DENYED, "(" + plugin.getModule().getRequestManager().getReason(player).replace("@", " ") + ")");
        }

    }

    private void timePlayed() {
        long time = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        parseValueToInfo(PlayerInfosValues.TIME_PLAYED, RaspiTimes.Ticks.getTimeUnit(time));
    }

    private void lastDeath() {
        long death = player.getStatistic(Statistic.TIME_SINCE_DEATH);
        parseValueToInfo(PlayerInfosValues.LAST_DEATH, RaspiTimes.Ticks.getTimeUnit(death));

    }

    public void lastSeen() {
        if (player.isOnline()) {

            parseValueToInfo(PlayerInfosValues.BACK_AFTER, RaspiTimes.MilliSeconds.getTimeUnit(userManager.lastSeen(player)));
        } else {
            parseValueToInfo(PlayerInfosValues.LAST_SEEN, RaspiTimes.MilliSeconds.getTimeUnit(player.getLastSeen()));
        }
    }


    public void playerXP() {
        if (player.isOnline()) {
            Player online = player.getPlayer();
            assert online != null;
            int xp = online.getLevel();
            parseValueToInfo(PlayerInfosValues.PLAYER_XP, String.valueOf(xp));
        }
    }


}