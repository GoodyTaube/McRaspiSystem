package eu.goodyfx.system.core.commandsOLD;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.database.DatabaseTables;
import eu.goodyfx.system.core.utils.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class TempBanCommand implements CommandExecutor {

    private final RaspiMessages data;
    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    public TempBanCommand(McRaspiSystem plugin) {
        this.data = plugin.getModule().getRaspiMessages();
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
        if (sender instanceof Player dummy) {
            RaspiPlayer player = Raspi.players().get(dummy);
            if (args.length > 2) {
                RaspiOfflinePlayer target = Raspi.players().getRaspiOfflinePlayer(Bukkit.getOfflinePlayer(args[0]));
                if (!plugin.getDatabaseManager().userExistInTable(target.getPlayer().getUniqueId(), DatabaseTables.USER_DATA)) {
                    player.sendMessage("<red>Der Spieler war noch nie hier.", true);
                    return true;
                }


                if (args.length == 5 && (args[4].equals("--MOD"))) {
                    String reason = "RSP:6723@Überdenk@Dein@Leben";
                    Long expire = plugin.getConfig().getInt("Utilities.tempban.time") * RaspiTimes.MilliSeconds.HOUR.getTime();

                    target.getManagement().performTempBan(player.getPlayer(), reason, expire);
                    String output = String.format("%1$s<red>%2$s <gray>wurde von: <red>%3$s <gray>für: <yellow>%4$s <gray>3 Stunde(n) gesperrt.", data.getPrefix(), target.getPlayer().getName(), player.getPlayer().getName(), reason);
                    Bukkit.getOnlinePlayers().forEach(all -> all.sendRichMessage(output));
                    kickPlayer(target);
                    return true;


                }

                if (!player.hasPermission("group.op")) {
                    player.sendMessage("<red>Du hast keine Rechte.", true);
                    return true;
                }

                if (target.getManagement().isBanned()) {
                    player.sendMessage("<red>Der Spieler ist bereits gesperrt.", true);
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
                                player.sendMessage(data.getPrefix() + multiplier + timeVal + " ist nicht Gültig\n" + "<red>" + multiplier + "y: Jahr,\n" + "<red>" + multiplier + "M: Monat,\n" + "<red>" + multiplier + "w: Woche,\n" + "<red>" + multiplier + "d: Tag,\n" + "<red>" + multiplier + "h: Stunde,\n" + "<red>" + multiplier + "m: Minute,\n" + "<red>" + multiplier + "s: Sekunde  ");
                    }
                    if (time == null) {
                        return true;
                    }

                    target.getManagement().performTempBan(player.getPlayer(), reason.toString(), time.getTime() + System.currentTimeMillis());
                    RaspiTimes.MilliSeconds finalTime = time;
                    Bukkit.getOnlinePlayers().forEach(all -> all.sendRichMessage(data.getPrefix() + "<red>" + target.getPlayer().getName() + " <gray>wurde von: <red>" + dummy.getName() + " <gray>für: <yellow>" + reason.toString().replace("@", " ") + " <gray>" + multiplier + " " + finalTime.getLabel() + " gesperrt."));
                    kickPlayer(target);

                } catch (NumberFormatException e) {
                    player.sendMessage(data.getPrefix() + "<red>Bitte gib einen validen wert an zb '<yellow>1w<red>' für 1 Woche ban.");
                    return true;
                }
                return true;
            } else {
                sender.sendRichMessage(data.getPrefix() + "<red>Bitte verwende einen Grund! <white>[<green><click:suggest_command:'/tempban " + args[0] + " " + args[1] + " '>Korrektur<reset><white>]");
            }
        }
        return false;

    }

    private void kickPlayer(RaspiOfflinePlayer target) {
        if (target.getPlayer().isOnline()) {
            assert target.getPlayer() != null;
            Objects.requireNonNull(target.getPlayer().getPlayer()).kick(MiniMessage.miniMessage().deserialize("<red>Du wurdest Temporär gesperrt.\n\n<gray>Du wurdest von: <aqua>" + target.getManagement().getBan_owner() + " <gray>für folgendes gesperrt:\n'<yellow>" + target.getManagement().getBan_message() + "<gray>'\n\n<gray>Du wirst am <green>" + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(target.getManagement().getBan_expire()) + " <gray>entsperrt."));
        }
    }


}
