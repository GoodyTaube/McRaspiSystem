package eu.goodyfx.mcraspisystem.events;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.managers.UserManager;
import eu.goodyfx.mcraspisystem.utils.*;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.text.SimpleDateFormat;
import java.util.Arrays;

public class PlayerChatListeners implements Listener {


    private final McRaspiSystem plugin;
    private final UserManager userManager;

    private final PlayerNameController playerNameController;

    public PlayerChatListeners(McRaspiSystem plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getModule().getUserManager();
        this.playerNameController = plugin.getModule().getPlayerNameController();
        plugin.setListeners(this);
    }

    private String lastMessage = "";


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent chatEvent) {
        if(chatEvent.isCancelled()){
            return;
        }

        chatEvent.setCancelled(true);//Disabled the core funktion of normal Minecraft Chat.
        String[] legacy = LegacyComponentSerializer.legacyAmpersand().serialize(chatEvent.message()).split(" ");
        StringBuilder builder = new StringBuilder();
        Arrays.stream(legacy).forEach(val -> {
            if (val.startsWith("https://") || val.startsWith("http://")) {
                builder.append(val.replace("&", "$")).append(" ");
            } else {
                builder.append(val).append(" ");
            }
        });
        chatEvent.message(LegacyComponentSerializer.legacyAmpersand().deserialize(builder.toString()));
        String plainMessage = LegacyComponentSerializer.legacyAmpersand().serialize(chatEvent.message()); //Message as Plain Message
        Player player = chatEvent.getPlayer();

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
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (plugin.getModule().getPlayerSettingsManager().contains(Settings.ADVANCED_CHAT, onlinePlayer)) {
                onlinePlayer.sendRichMessage("<" + commandClick("/playerinfo " + player.getName()) + hoverText("<gray>Player Infos<br>" +
                        "Bisher Gespielt: <aqua>" + RaspiTimes.Ticks.getTimeUnit(player.getStatistic(Statistic.PLAY_ONE_MINUTE)) + "<br>" +
                        "<gray><italic>Klicke um mehr Infos zu bekommen.") + playerNameController.getNameDisplay(player) + "> " + hoverText("<aqua>" + new SimpleDateFormat("HH:mm").format(System.currentTimeMillis())) + finalPlainMessage);

            } else if (!plugin.getModule().getPlayerSettingsManager().contains(Settings.ADVANCED_CHAT, onlinePlayer)) {
                onlinePlayer.sendRichMessage("<" + playerNameController.getNameDisplay(player) + "> " + finalPlainMessage);
            }

        }
        String log = String.format("[RaspiChat] <%s> %s", player.getName(), PlainTextComponentSerializer.plainText().serialize(MiniMessage.miniMessage().deserialize(finalPlainMessage)));
        plugin.getLogger().info(log);
        plugin.getHookManager().getDiscordIntegration().send("<" + player.getName() + ">" + " " + PlainTextComponentSerializer.plainText().serialize(MiniMessage.miniMessage().deserialize(finalPlainMessage)));
    }

    private boolean checkUp(Player player) {
        boolean failed = false;
        if (userManager.isMuted(player)) {
            player.sendActionBar(MiniMessage.miniMessage().deserialize("<red>Du kannst den Chat nicht benutzen."));
            failed = true;
        }

        if (plugin.getModule().getWarteschlangenManager().isQueue(player) && plugin.getModule().getRaspiMessages().blockChat()) {
            player.sendRichMessage(plugin.getModule().getRaspiMessages().blocking());
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
    private boolean teamIntegration(Player player, String message) {
        boolean isTeam = false;
        if (player.isPermissionSet("system.team") && message.startsWith("!")) {
            isTeam = true;

            Bukkit.getOnlinePlayers().forEach(teamMember -> {
                if (teamMember.isPermissionSet("system.team")) {
                    if (!lastMessage.startsWith("!")) {
                        teamMember.sendPlainMessage(" ");
                        teamMember.sendRichMessage("<gold><b>TEAM<reset><gray>: " + player.getName() + " : " + url(message.substring(1)));
                        teamMember.sendPlainMessage(" ");
                    } else {
                        teamMember.sendRichMessage("<gold><b>TEAM<reset><gray>: " + player.getName() + " : " + url(message.substring(1)));
                    }
                }
            });
            lastMessage = message;
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
            int finalI = i;

            Bukkit.getOnlinePlayers().forEach(online -> {
                if (args[finalI].toLowerCase().contains(online.getName().toLowerCase()) && !args[finalI].startsWith("<blue><underlined><click")) {
                    args[finalI] = args[finalI].replaceAll(online.getName().toLowerCase(), playerNameController.getName(online));
                    args[finalI] = args[finalI].replaceAll(online.getName(), playerNameController.getName(online));

                }
            });
            preResult.append(args[finalI]).append(" ");
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
