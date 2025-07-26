package eu.goodyfx.system.core.commandsOLD;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.managers.PlayerBanManager;
import eu.goodyfx.system.core.managers.UserManager;
import eu.goodyfx.system.core.utils.RaspiMessages;
import eu.goodyfx.system.core.utils.RaspiTimes;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;

public class TempBanCommand implements CommandExecutor {

    private final RaspiMessages data;
    private final McRaspiSystem plugin;
    private final PlayerBanManager playerBanManager;
    private final UserManager userManager;

    public TempBanCommand(McRaspiSystem plugin) {
        this.data = plugin.getModule().getRaspiMessages();
        this.plugin = plugin;
        this.playerBanManager = plugin.getModule().getPlayerBanManager();
        this.userManager = plugin.getModule().getUserManager();
        plugin.setCommand("tempban", this);
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            return false;
        }

        if (args.length == 1) {
            return false;
        }
        if (sender instanceof Player player) {


            if (args.length > 2) {


                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

                if (args.length == 5 && (args[4].equals("--MOD"))) {
                    playerBanManager.tempBanPlayerStandard(target, new StringBuilder("RSP:6723@Überdenk@Dein@Leben"), (plugin.getConfig().getInt("Utilities.tempban.time") * RaspiTimes.MilliSeconds.HOUR.getTime()), player.getName());
                    String output = String.format("%1$s<red>%2$s <gray>wurde von: <red>%3$s <gray>für: <yellow>%4$s <gray>3 Stunde(n) gesperrt.", data.getPrefix(), target.getName(), player.getName(), playerBanManager.reason(target));
                    Bukkit.getOnlinePlayers().forEach(all -> all.sendRichMessage(output));
                    kickPlayer(target);
                    return true;


                }

                if (!player.isPermissionSet("group.op")) {
                    player.sendRichMessage(data.getPrefix() + "<red>Du hast keine Rechte.");
                    return true;
                }

                if (playerBanManager.contains(target)) {
                    player.sendRichMessage(data.getPrefix() + "<red>Der Spieler ist bereits gesperrt.");
                    return true;
                }

                if (!userManager.userExist(target)) {
                    sender.sendRichMessage(data.getPrefix() + "<red>Der Spieler hat hier noch nicht gespielt.");
                    return true;
                }

                StringBuilder reason = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    reason.append(args[i]).append("@");
                }
                reason.setLength(reason.length() - 1);

                try {

                    int multiplier = Integer.parseInt(args[1].substring(0, args[1].length() - 1));
                    String timeVal = args[1].substring(args[1].length() - 1);

                    RaspiTimes.MilliSeconds time = null;

                    switch (timeVal) {
                        case "y" -> time = RaspiTimes.MilliSeconds.YEAR;
                        case "M" -> time = RaspiTimes.MilliSeconds.MONTH;
                        case "w" -> time = RaspiTimes.MilliSeconds.WEEK;
                        case "d" -> time = RaspiTimes.MilliSeconds.DAY;
                        case "h" -> time = RaspiTimes.MilliSeconds.HOUR;
                        case "m" -> time = RaspiTimes.MilliSeconds.MINUTE;
                        case "s" -> time = RaspiTimes.MilliSeconds.SECOND;
                        default ->
                                player.sendRichMessage(data.getPrefix() + multiplier + timeVal + " ist nicht Gültig\n" + "<red>" + multiplier + "y: Jahr,\n" + "<red>" + multiplier + "M: Monat,\n" + "<red>" + multiplier + "w: Woche,\n" + "<red>" + multiplier + "d: Tag,\n" + "<red>" + multiplier + "h: Stunde,\n" + "<red>" + multiplier + "m: Minute,\n" + "<red>" + multiplier + "s: Sekunde  ");
                    }
                    if (time == null) {
                        return true;
                    }
                    playerBanManager.tempBanPlayer(target, reason, time, multiplier, player.getName());
                    RaspiTimes.MilliSeconds finalTime = time;
                    Bukkit.getOnlinePlayers().forEach(all -> all.sendRichMessage(data.getPrefix() + "<red>" + target.getName() + " <gray>wurde von: <red>" + player.getName() + " <gray>für: <yellow>" + reason.toString().replace("@", " ") + " <gray>" + multiplier + " " + finalTime.getLabel() + " gesperrt."));
                    kickPlayer(target);

                } catch (NumberFormatException e) {
                    player.sendRichMessage(data.getPrefix() + "<red>Bitte gib einen validen wert an zb '<yellow>1w<red>' für 1 Woche ban.");
                    return true;
                }
                return true;
            } else {
                sender.sendRichMessage(data.getPrefix() + "<red>Bitte verwende einen Grund! <white>[<green><click:suggest_command:'/tempban " + args[0] + " " + args[1] + " '>Korrektur<reset><white>]");
            }
        } else {
            if (args.length > 2) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                if (!userManager.userExist(target)) {
                    sender.sendRichMessage(data.getPrefix() + "<red>Der Spieler hat hier noch nicht gespielt.");
                    return true;
                }
                StringBuilder reason = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    reason.append(args[i]).append("@");
                }
                reason.setLength(reason.length() - 1);
                try {
                    int multiplier = Integer.parseInt(args[1].substring(0, args[1].length() - 1));
                    String timeVal = args[1].substring(args[1].length() - 1);

                    RaspiTimes.MilliSeconds time = null;

                    switch (timeVal) {
                        case "y" -> time = RaspiTimes.MilliSeconds.YEAR;
                        case "M" -> time = RaspiTimes.MilliSeconds.MONTH;
                        case "w" -> time = RaspiTimes.MilliSeconds.WEEK;
                        case "d" -> time = RaspiTimes.MilliSeconds.DAY;
                        case "h" -> time = RaspiTimes.MilliSeconds.HOUR;
                        case "m" -> time = RaspiTimes.MilliSeconds.MINUTE;
                        case "s" -> time = RaspiTimes.MilliSeconds.SECOND;
                        default ->
                                sender.sendRichMessage(data.getPrefix() + multiplier + timeVal + " ist nicht Gültig\n" + "<red>" + multiplier + "y: Jahr,\n" + "<red>" + multiplier + "M: Monat,\n" + "<red>" + multiplier + "w: Woche,\n" + "<red>" + multiplier + "d: Tag,\n" + "<red>" + multiplier + "h: Stunde,\n" + "<red>" + multiplier + "m: Minute,\n" + "<red>" + multiplier + "s: Sekunde  ");
                    }
                    if (time == null) {
                        return true;
                    }
                    playerBanManager.tempBanPlayer(target, reason, time, multiplier, "SERVER");
                    RaspiTimes.MilliSeconds finalTime = time;
                    Bukkit.getOnlinePlayers().forEach(all -> all.sendRichMessage(data.getPrefix() + "<red>" + target.getName() + " <gray>wurde von: <red>" + "SERVER" + " <gray>für: <yellow>" + reason.toString().replace("@", " ") + " <gray>" + multiplier + " " + finalTime.getLabel() + " gesperrt."));
                    sender.sendRichMessage(data.getPrefix() + "<gray>Der Spieler wurde für: <yellow>" + multiplier + time.getLabel() + " <gray>gesperrt.");
                    kickPlayer(target);
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendRichMessage(data.getPrefix() + "<red>Bitte gib einen validen wert an zb '<yellow>1w<red>' für 1 Woche ban.");
                    return true;
                }

            }
            return false;


        }


        return false;

    }

    private void kickPlayer(OfflinePlayer target) {
        if (target.isOnline()) {
            assert target.getPlayer() != null;
            target.getPlayer().kick(MiniMessage.miniMessage().deserialize("<red>Du wurdest Temporär gesperrt.\n\n<gray>Du wurdest von: <aqua>" + playerBanManager.performer(target) + " <gray>für folgendes gesperrt:\n'<yellow>" + playerBanManager.reason(target) + "<gray>'\n\n<gray>Du wirst am <green>" + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(playerBanManager.expire(target)) + " <gray>entsperrt."));
        }
    }


}
