package eu.goodyfx.system.core.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.database.RaspiManagement;
import eu.goodyfx.system.core.database.RaspiUser;
import eu.goodyfx.system.core.managers.ExtraInfos;
import io.papermc.paper.ban.BanListType;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerInfo {

    private final OfflinePlayer player;
    private final RaspiUser raspiUser;
    private final RaspiManagement management;

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    private final List<String> playerInfoAssets = new ArrayList<>();

    public PlayerInfo(RaspiOfflinePlayer player) {

        this.raspiUser = player.getRaspiUser();
        this.management = player.getManagement();
        this.player = player.getPlayer();
        //lastNames();
        firstDocumentation();
        parseGroups();
        registration();
        timePlayed();
        lastDeath();
        lastSeen();
        playerXP();
        playerServerInfos();
    }

    private void parseValueToInfo(PlayerInfosValues info, String value) {
        playerInfoAssets.add(String.format("%s %s<reset>", info.getLabel(), value));
    }

    public String buildPlayerInfos() {

        StringBuilder playerInfos = new StringBuilder("<gray>Player Infos: <aqua>" + player.getName() + "<reset><br>");
        for (String inf : playerInfoAssets) {
            playerInfos.append(inf);
        }
        playerInfos.setLength(playerInfos.length() - 4);
        return playerInfos.toString();
    }


    public Dialog buildPlayerInfosDialog(OfflinePlayer player, Player performer) {
        List<DialogBody> dialogBodies = new ArrayList<>();

        ItemStack stack = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwningPlayer(player);
        stack.setItemMeta(meta);

        dialogBodies.add(DialogBody.item(stack, DialogBody.plainMessage(Component.empty()), false, false, 10, 10));
        dialogBodies.add(DialogBody.plainMessage(MiniMessage.miniMessage().deserialize(String.format("<gray>Spieler Infos: <aqua>%s", player.getName())), 1024));
        for (String info : playerInfoAssets) {
            dialogBodies.add(DialogBody.plainMessage(MiniMessage.miniMessage().deserialize(info), 1024));
        }

        ExtraInfos extraInfos = new ExtraInfos(performer);
        dialogBodies.add(DialogBody.plainMessage(MiniMessage.miniMessage().deserialize(extraInfos.getExtraInfosForDialog(player))));

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.empty()).body(dialogBodies).canCloseWithEscape(true).build()).type(DialogType.notice()));
    }

    private void registration() {
        Boolean state = raspiUser.getState();
        if (state == null) {
            parseValueToInfo(PlayerInfosValues.REGISTRATION, "<red>Noch nicht Freigeschaltet.");
            return;
        }
        String val;
        if (state) {
            val = "<green>Freigeschaltet";
        } else {
            val = String.format("<red>Abgelehnt von %s<br>(%s)", raspiUser.getDenied_by(), raspiUser.getDeny_reason());
        }
        parseValueToInfo(PlayerInfosValues.REGISTRATION, val);
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
        parseValueToInfo(PlayerInfosValues.FIRST_JOIN, new SimpleDateFormat("dd/MM/yyyy").format(raspiUser.getFirst_join()));
    }

    private void lastNames() {
        List<String> names = List.of("<red>Momentan nicht Verfügbar");

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
        if (management.isMuted()) {
            parseValueToInfo(PlayerInfosValues.MUTED, management.getMute_message());
        }
        PlayerProfile profile = Bukkit.createProfile(player.getUniqueId());
        BanList<PlayerProfile> entry = Bukkit.getBanList(BanListType.PROFILE);
        if (management.isBanned()) {
            parseValueToInfo(PlayerInfosValues.BANNED, management.getBan_message());
        }
        if (entry.isBanned(profile)) {
            parseValueToInfo(PlayerInfosValues.BANNED, entry.getBanEntry(profile).getReason());
        }
    }

    private void timePlayed() {
        long time = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        parseValueToInfo(PlayerInfosValues.TIME_PLAYED, String.format("%s Stunde(n)", raspiUser.getOnlineHours()));
    }

    private void lastDeath() {
        long death = player.getStatistic(Statistic.TIME_SINCE_DEATH);
        parseValueToInfo(PlayerInfosValues.LAST_DEATH, RaspiTimes.Ticks.getTimeUnit(death));

    }

    public void lastSeen() {
        if (raspiUser.getLastSeen() == null) {
            parseValueToInfo(PlayerInfosValues.LAST_SEEN, "<red>E:404");
            return;
        }
        if (player.isOnline()) {
            parseValueToInfo(PlayerInfosValues.BACK_AFTER, RaspiTimeUtils.formatDuration(RaspiTimeUtils.getBetween(raspiUser.getLastSeen())));
        } else {
            parseValueToInfo(PlayerInfosValues.LAST_SEEN, RaspiTimeUtils.formatDuration(RaspiTimeUtils.getBetween(raspiUser.getLastSeen())));
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