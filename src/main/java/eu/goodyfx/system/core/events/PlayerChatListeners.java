package eu.goodyfx.system.core.events;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.database.RaspiPlayer;
import eu.goodyfx.system.core.utils.RaspiFormatting;
import eu.goodyfx.system.core.utils.RaspiPermission;
import eu.goodyfx.system.core.utils.RaspiTimes;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Arrays;

public class PlayerChatListeners implements Listener {


    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    public PlayerChatListeners() {
        plugin.setListeners(this);
    }

    private String lastMessage = "";

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent chatEvent) {
        if (chatEvent.isCancelled()) {
            return;
        }
        chatEvent.setCancelled(true);//Disabled the core funktion of normal Minecraft Chat.
        String[] legacy = LegacyComponentSerializer.legacyAmpersand().serialize(chatEvent.message()).split(" ");
        StringBuilder builder = new StringBuilder();
        Arrays.stream(legacy).forEach(value -> {
            if (value.startsWith("https://") || value.startsWith("http://")) {
                builder.append(value.replace("&", "$")).append(" ");
            } else {
                builder.append(value).append(" ");
            }
        });

        RaspiPlayer player = Raspi.playerLifeCycleService().getRaspiPlayer(chatEvent.getPlayer());
        if (player == null) {
            return;
        }


        chatEvent.message(LegacyComponentSerializer.legacyAmpersand().deserialize(builder.toString()));
        String plainMessage = LegacyComponentSerializer.legacyAmpersand().serialize(chatEvent.message()); //Message as Plain Message

        if (checkUp(player)) {
            return;
        }
        if (teamIntegration(player, plainMessage)) {
            return;
        }
        lastMessage = plainMessage;
        plainMessage = cleanUpMessage(plainMessage);
        plainMessage = url(plainMessage);
        plainMessage = appendPlayerNameColors(plainMessage);


        String finalPlainMessage = plainMessage;
        Component checkMessage = MiniMessage.miniMessage().deserialize(finalPlainMessage);
        if (PlainTextComponentSerializer.plainText().serialize(checkMessage).isEmpty()) {
            return;
        }


        for (RaspiPlayer active : Raspi.playerLifeCycleService().getCachedRaspiPlayers()) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                send(player, active, finalPlainMessage);
            });
        }
        String log = String.format("[RaspiChat] <%s> %s", player.getPlayer().getName(), PlainTextComponentSerializer.plainText().serialize(MiniMessage.miniMessage().deserialize(finalPlainMessage)));
        plugin.getLogger().info(log);
        //Send Discord Message! 2025
        try {
            plugin.getHookManager().getDiscordIntegration().send("<" + player.getPlayer().getName() + ">" + " " + PlainTextComponentSerializer.plainText().serialize(MiniMessage.miniMessage().deserialize(finalPlainMessage)));
        } catch (Exception ignore) {
        }
    }


    public void send(RaspiPlayer sendPlayer, RaspiPlayer player, String finalMessage) {

        //String team = getTeamMarker(sendPlayer.getPlayer(), player, finalMessage);
        if (player.settings().isOpt_chat()) {
            String commandClick = commandClick(String.format("/playerinfo %s", sendPlayer.getPlayer().getName()));
            String hoverText = hoverText(String.format("<gray>PlayerInfos<br>Bisher Gespielt: <aqua>%s<br><gray><italic>Klicke um mehr Infos zu bekommen.", RaspiTimes.Ticks.getTimeUnit(sendPlayer.getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE)))); //REPLACE DURCH ONLINE_HOURS
            String optMessage = String.format("%s%s", commandClick, hoverText);
            String hoverMessageClock = hoverText(String.format("<aqua>%s", new SimpleDateFormat("HH:mm").format(System.currentTimeMillis())));
            String message = String.format("<%s%s> %s%s", optMessage, sendPlayer.getDisplayName(), hoverMessageClock, finalMessage);
            player.sendMessage(message);
            return;
        }
        player.sendMessage(String.format("<%s> %s", sendPlayer.getDisplayName(), finalMessage));
    }

    private boolean checkUp(RaspiPlayer player) {
        boolean failed = false;
        if (player.userManagement().isMuted()) {
            failed = true;
            player.sendActionBar("<red>Du kannst den Chat nicht benutzen. <yellow>(Stummgeschaltet)");
        }
        if (plugin.getModule().getWarteschlangenManager().isQueue(player.getPlayer()) && plugin.getModule().getRaspiMessages().blockChat()) {
            player.sendMessage(plugin.getModule().getRaspiMessages().blocking());
            failed = true;
        }

        return failed;
    }


    private String url(String raw) {
        raw = raw.replace("$", "&");
        String[] args = raw.split(" ");
        StringBuilder preResult = new StringBuilder();

        for (String arg : args) {

            if (arg.startsWith("www")) {
                arg = arg.replace(arg, "<aqua><underlined><click:open_url:'http://" + arg + "'>" + arg + "<reset>");

            } else if (arg.startsWith("http")) {
                arg = arg.replace(arg, "<aqua><underlined><click:open_url:'" + arg + "'>" + arg + "<reset>");

            }


            preResult.append(arg).append(" ");
        }
        String result = preResult.toString();
        return result.substring(0, result.length() - 1);
    }

    /**
     * If player is Team Member unlock usage of TeamChat
     *
     * @param player The possible Team Member
     */
    private boolean teamIntegration(RaspiPlayer player, String message) {
        boolean isTeam = false;

        if (player.hasPermission(RaspiPermission.TEAM) && message.startsWith("!") && message.length() > 1) {
            isTeam = true;
            Raspi.playerLifeCycleService().getCachedRaspiPlayers().forEach(mabeTeam -> {
                if (mabeTeam.hasPermission(RaspiPermission.TEAM)) {
                    if (!lastMessage.startsWith("!")) {
                        mabeTeam.getPlayer().sendPlainMessage(" ");
                        mabeTeam.sendMessage("<gold><b>TEAM<reset><gray>: " + player.getDisplayName() + " : " + url(message.substring(1)));
                        mabeTeam.getPlayer().sendPlainMessage(" ");
                    } else {
                        mabeTeam.sendMessage("<gold><b>TEAM<reset><gray>: " + player.getDisplayName() + " : " + url(message.substring(1)));
                    }
                }
            });


        }
        return isTeam;
    }

    private String cleanUpMessage(String message) {
        return RaspiFormatting.formattingChatMessage(message);
    }

    private String appendPlayerNameColors(String rawMessage) {

        String[] args = rawMessage.split(" ");
        StringBuilder preResult = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            for (RaspiPlayer raspiOnlinePlayer : Raspi.playerLifeCycleService().getCachedRaspiPlayers()) {
                if (args[i].toLowerCase().contains(raspiOnlinePlayer.getPlayer().getName().toLowerCase()) && !args[i].startsWith("<blue><underlined><click")) {
                    args[i] = args[i].replaceAll(raspiOnlinePlayer.getPlayer().getName().toLowerCase(), raspiOnlinePlayer.getColorName());
                    args[i] = args[i].replaceAll(raspiOnlinePlayer.getPlayer().getName(), raspiOnlinePlayer.getColorName());

                }

            }
            preResult.append(args[i]).append(" ");
        }
        String result = preResult.toString();
        return result.substring(0, result.length() - 1);
    }


    private String commandClick(String command) {
        return "<click:run_command:'" + command + "'>";
    }

    private String hoverText(String text) {
        return "<hover:show_text:'" + text + "'>";
    }


}
