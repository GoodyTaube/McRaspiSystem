package eu.goodyfx.system.core.commandsOLD;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.database.RaspiPlayer;
import eu.goodyfx.system.core.utils.RaspiMessages;
import eu.goodyfx.system.core.utils.RaspiTimes;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
            RaspiPlayer player = Raspi.playerLifeCycleService().getRaspiPlayer(dummy);
            if (args.length > 2) {

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);

                if (!offlinePlayer.hasPlayedBefore()) {
                    player.sendMessage("<red>Der Spieler existiert nicht :X", true);
                    return true;
                }

                Raspi.playerLifeCycleService().getRaspiAccount(offlinePlayer.getUniqueId(), false).thenAccept(account -> {


                    // /tempban <player> 1w kleiner kek --MOD

                    if (args.length == 5 && (args[4].equals("--MOD"))) {
                        String reason = "RSP:6723@Überdenk@Dein@Leben";
                        Long expire = plugin.getConfig().getInt("Utilities.tempban.time") * RaspiTimes.MilliSeconds.HOUR.getTime();

                        account.getRaspiManagement().performTempBan(player.getPlayer(), reason, expire);
                        String output = String.format("%1$s<red>%2$s <gray>wurde von: <red>%3$s <gray>für: <yellow>%4$s <gray>3 Stunde(n) gesperrt.", data.getPrefix(), account.getRaspiUser().getUsername(), player.getPlayer().getName(), reason);
                        Bukkit.getOnlinePlayers().forEach(all -> all.sendRichMessage(output));
                        kickPlayer(offlinePlayer);
                        return;


                    }

                    if (!player.hasPermission("group.op")) {
                        player.sendMessage("<red>Du hast keine Rechte.", true);
                        return;
                    }

                    if (account.getRaspiManagement().isBanned()) {
                        player.sendMessage("<red>Der Spieler ist bereits gesperrt.", true);
                        return;
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
                            return;
                        }

                        account.getRaspiManagement().performTempBan(player.getPlayer(), reason.toString(), time.getTime() + System.currentTimeMillis());
                        RaspiTimes.MilliSeconds finalTime = time;
                        Bukkit.getOnlinePlayers().forEach(all -> all.sendRichMessage(data.getPrefix() + "<red>" + account.getRaspiUser().getUsername() + " <gray>wurde von: <red>" + dummy.getName() + " <gray>für: <yellow>" + reason.toString().replace("@", " ") + " <gray>" + multiplier + " " + finalTime.getLabel() + " gesperrt."));
                        kickPlayer(offlinePlayer);

                    } catch (NumberFormatException e) {
                        player.sendMessage(data.getPrefix() + "<red>Bitte gib einen validen wert an zb '<yellow>1w<red>' für 1 Woche ban.");
                    }
                    Raspi.accountService().saveIfOffline(account);
                });

            } else {
                sender.sendRichMessage(data.getPrefix() + "<red>Bitte verwende einen Grund! <white>[<green><click:suggest_command:'/tempban " + args[0] + " " + args[1] + " '>Korrektur<reset><white>]");
            }
            return true;
        }
        return false;

    }

    private void kickPlayer(OfflinePlayer target) {
        if (target.getPlayer().isOnline()) {
            RaspiPlayer targetP = Raspi.playerLifeCycleService().getRaspiPlayer(target.getPlayer());
            assert target.getPlayer() != null;
            Objects.requireNonNull(target.getPlayer().getPlayer()).kick(MiniMessage.miniMessage().deserialize("<red>Du wurdest Temporär gesperrt.\n\n<gray>Du wurdest von: <aqua>" + targetP.userManagement().getBan_owner() + " <gray>für folgendes gesperrt:\n'<yellow>" + targetP.userManagement().getBan_message() + "<gray>'\n\n<gray>Du wirst am <green>" + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(targetP.userManagement().getBan_expire()) + " <gray>entsperrt."));
        }
    }


}
