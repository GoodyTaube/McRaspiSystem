package eu.goodyfx.system.core.events;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.database.RaspiPlayer;
import eu.goodyfx.system.core.utils.RaspiFormatting;
import eu.goodyfx.system.core.utils.RaspiPermission;
import eu.goodyfx.system.core.utils.RaspiTimes;
import eu.goodyfx.system.core.utils.Settings;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

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


        String rawPlayerInput = LegacyComponentSerializer.legacyAmpersand().serialize(chatEvent.message());
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(chatEvent.getPlayer());
        if (checkUp(raspiPlayer)) {
            return;
        }
        if (teamIntegration(raspiPlayer, rawPlayerInput)) {
            return;
        }
        this.lastMessage = rawPlayerInput;
        Component cleaned = RaspiFormatting.parseInputText(rawPlayerInput);

        for (RaspiPlayer online : Raspi.playerLifeCycleService().getCachedRaspiPlayers()) {
            String username = online.userData().getUsername();

            cleaned = cleaned.replaceText(config -> config.match(Pattern.compile("(?i)\\b" + Pattern.quote(username) + "\\b"))
                    .replacement(match -> MiniMessage.miniMessage().deserialize(online.getColorName())));

        }


        for (RaspiPlayer active : Raspi.playerLifeCycleService().getCachedRaspiPlayers()) {
            Component finalCleaned = cleaned;
            Bukkit.getScheduler().runTask(plugin, () -> {
                send(raspiPlayer, active, finalCleaned);
            });
        }
        String log = String.format("[RaspiChat] <%s> %s", raspiPlayer.getPlayer().getName(), PlainTextComponentSerializer.plainText().serialize(cleaned));
        plugin.getLogger().info(log);
        //Send Discord Message! 2025
        try {
            plugin.getHookManager().getDiscordIntegration().send("<" + raspiPlayer.getPlayer().getName() + ">" + " " + PlainTextComponentSerializer.plainText().serialize(cleaned));
        } catch (Exception ignore) {
        }
    }


    public void send(RaspiPlayer sendPlayer, RaspiPlayer player, Component finalMessage) {

        //String team = getTeamMarker(sendPlayer.getPlayer(), player, finalMessage);
        if (player.settings().get(Settings.ADVANCED_CHAT)) {
            String commandClick = commandClick(String.format("/playerinfo %s", sendPlayer.getPlayer().getName()));
            String hoverText = hoverText(String.format("<gray>PlayerInfos<br>Bisher Gespielt: <aqua>%s<br><gray><italic>Klicke um mehr Infos zu bekommen.", RaspiTimes.Ticks.getTimeUnit(sendPlayer.getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE)))); //REPLACE DURCH ONLINE_HOURS
            String optMessage = String.format("%s%s", commandClick, hoverText);
            String hoverMessageClock = hoverText(String.format("<aqua>%s", new SimpleDateFormat("HH:mm").format(System.currentTimeMillis())));
            String message = String.format("<%s%s> %s<message>", optMessage, sendPlayer.getDisplayName(), hoverMessageClock);

            Component parsedMessage = MiniMessage.miniMessage().deserialize(message, Placeholder.component("message", finalMessage));

            player.sendMessage(parsedMessage);
            return;
        }
        String sendFormat = String.format("<%s> <message>", sendPlayer.getDisplayName());
        Component endMessage = MiniMessage.miniMessage().deserialize(sendFormat, Placeholder.component("message", finalMessage));
        player.sendMessage(endMessage);
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
                        mabeTeam.sendMessage("<gold><b>TEAM<reset><gray>: " + player.getDisplayName() + " : " + message.substring(1));
                        mabeTeam.getPlayer().sendPlainMessage(" ");
                    } else {
                        mabeTeam.sendMessage("<gold><b>TEAM<reset><gray>: " + player.getDisplayName() + " : " + message.substring(1));
                    }
                    lastMessage = message;
                }
            });


        }
        return isTeam;
    }


    private String commandClick(String command) {
        return "<click:run_command:'" + command + "'>";
    }

    private String hoverText(String text) {
        return "<hover:show_text:'" + text + "'>";
    }


}
