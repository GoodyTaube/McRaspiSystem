package eu.goodyfx.mcraspisystem.events;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.RequestCommand;
import eu.goodyfx.mcraspisystem.commands.SitCommand;
import eu.goodyfx.mcraspisystem.managers.LocationManager;
import eu.goodyfx.mcraspisystem.managers.RequestManager;
import eu.goodyfx.mcraspisystem.managers.UserManager;
import eu.goodyfx.mcraspisystem.managers.WarteschlangenManager;
import eu.goodyfx.mcraspisystem.utils.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class RaspiPlayerConnectionEvents implements Listener {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    private final RaspiMessages data = plugin.getModule().getRaspiMessages();
    private final WarteschlangenManager settings = plugin.getModule().getWarteschlangenManager();
    private final PlayerNameController playerNameController = plugin.getModule().getPlayerNameController();
    private final RequestManager requestManager = plugin.getModule().getRequestManager();
    private final UserManager userManager = plugin.getModuleManager().getUserManager();

    private final Map<UUID, PlayerTime> timeContainer = new HashMap<>();

    public RaspiPlayerConnectionEvents() {
        plugin.setListeners(this);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoined(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        RaspiPlayer raspiPlayer = plugin.getRaspiPlayer(player);
        LocationManager manager = plugin.getModule().getLocationManager();

        checkSystemLocationsExists(player, manager);

        //Check if joining Player joins in Active World
        settings.join(player);
        settings.setHeader();
        //REQUEST
        raspiRequest(raspiPlayer);

        playerNameController.setPlayerList(player);
        playerJoinEvent.joinMessage(MiniMessage.miniMessage().deserialize(OldColors.convert(plugin.getModule().getJoinMessageManager().get(player))));

        if ((!player.isPermissionSet("system.bypass") && (userManager.hasTimePlayed(player, 100)))) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " permission set system.bypass");
        }

        plugin.getHookManager().getDiscordIntegration().send(String.format("`[System] <%s> ist zurückgekehrt.`", player.getName()));

        timeContainer.put(player.getUniqueId(), new PlayerTime(player));

        welcomeMessage(plugin.getRaspiPlayer(player));
    }

    private void welcomeMessage(RaspiPlayer player) {
        if (plugin.getConfig().contains("Utilities.welcome") && plugin.getConfig().getBoolean("Utilities.welcome")) {
            try (BufferedReader reader = new BufferedReader(new FileReader(new File(plugin.getDataFolder(), "willkommen.txt"), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.replace("{player}", player.getName());
                    player.sendMessage(line);
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "willkommen.txt konnte nicht gefunden werden.");
            }
        }
    }

    private void raspiRequest(RaspiPlayer raspiPlayer) {
        Player player = raspiPlayer.getPlayer();
        //Checkt ob der Spieler bereits angenommen wurde
        handleNewbie(player);
        //Was passiert mit einem Spieler, welcher abgelehnt wurde
        handleBlocked(raspiPlayer);
    }

    private void handleBlocked(RaspiPlayer raspiPlayer) {
        Player player = raspiPlayer.getPlayer();
        if (requestManager.isBlocked(player)) {
            plugin.getRaspiPlayersPerPermission(RaspiPermission.MOD).forEach(team -> {
                team.sendMessage(String.format("<gray><italic>%s wurde bereits von %s <gray><italic>Abgelehnt!", player.getName(), requestManager.getDeny(player)), true);
                if (requestManager.isBlocked("reason", player)) {
                    team.sendMessage(String.format("<gray><italic>Grund: <yellow>%s", requestManager.getReason(player).replace("@", " ")), true);
                }
                String message = String.format("<white>[<red>%s<white>] [<green>%s<white>]", String.format("<click:run_command:'/request kick %s'>Kicken<reset>", player.getName()), String.format("<click:run_command:'/request accept %s'>Erlauben<reset>", player.getName()));
                String banMessage = String.format("// <white>[<gold><click:run_command:'/tempban %s RSP:6723 Überdenk Dein Leben --MOD'>Ban<reset><white>]", player.getName());
                if (RequestCommand.freeToBan(player.getUniqueId())) {
                    team.sendMessage(message + banMessage, true);
                } else {
                    team.sendMessage(message, true);
                }
            });
        }
    }

    private void handleNewbie(Player player) {
        if (!player.isPermissionSet("group.spieler") && (!requestManager.isBlocked(player))) {
            plugin.getRaspiPlayersPerPermission(RaspiPermission.MOD).forEach(moderator -> {
                moderator.sendMessage(String.format("<gray><italic>%s ist noch nicht Registriert!", player.getName()), true);
                moderator.sendMessage(String.format("<white>[<green>%s<white>] [<red>%s<white>]", String.format("<click:run_command:'/request accept %s'>Annehmen<reset>", player.getName()), String.format("<click:run_command:'request deny %1$2s -request_start'>Ablehnen <gray>(<green><click:suggest_command:'/request deny %1$2s'>+<reset><gray>)", player.getName())), true);
            });
        }
    }

    private void checkSystemLocationsExists(Player player, LocationManager manager) {
        //Check if Locations Exist
        if (player.isOp()) {
            if (!manager.exist("warteraum")) {
                player.sendRichMessage(data.noSpawnPoint(2));
            }
            if (!manager.exist("spawn")) {
                player.sendRichMessage(data.noSpawnPoint(1));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent playerQuitEvent) {
        //Quit Player
        Player player = playerQuitEvent.getPlayer();
        UUID uuid = player.getUniqueId();
        userManager.lastSeen(player, System.currentTimeMillis());
        SitCommand.remove(player);

        if (plugin.getConfig().getBoolean("Utilities.leaveMessage")) {
            playerQuitEvent.quitMessage(MiniMessage.miniMessage().deserialize(data.getLeave(player)));
        }
        //IF player was in ActiveWorld
        if (settings.isInActiveWorld(player)) {
            //IF Player was in QUEUE
            if (settings.isQueue(player)) {
                settings.removeFromQueue(uuid);
            }

            int online = Bukkit.getOnlinePlayers().size();
            online = online - 1;

            //   - Left Player
            if (online - settings.getQueuedPlayers().size() < settings.getMaxPlayers()) {
                settings.queue();
            }
        }

        //Set Player Header
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getModule().getWarteschlangenManager().setHeader();
            }
        }.runTaskLater(plugin, 30L);

        if (userManager.hasPersistantValue(player, PlayerValues.AFK)) {
            plugin.getLogger().info(player.getName() + "  was AFK while Disconnecting! Removed AFK status!");
            Bukkit.dispatchCommand(player, "afk");
        }

        playerNameController.resetRandom(player);
        plugin.getHookManager().getDiscordIntegration().send(String.format("`[System] <%s> hat uns verlassen.`", player.getName()));
        if (timeContainer.containsKey(player.getUniqueId())) {
            PlayerTime playerTime = timeContainer.get(player.getUniqueId());
            playerTime.end(plugin.getModule().getTimeDBManager());
        }
    }


    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        event.motd(plugin.getModule().getMotdManager().getMessage());

    }

}
