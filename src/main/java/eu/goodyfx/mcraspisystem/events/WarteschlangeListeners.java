package eu.goodyfx.mcraspisystem.events;


import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.RequestCommand;
import eu.goodyfx.mcraspisystem.commands.SitCommand;
import eu.goodyfx.mcraspisystem.managers.LocationManager;
import eu.goodyfx.mcraspisystem.managers.RequestManager;
import eu.goodyfx.mcraspisystem.managers.UserManager;
import eu.goodyfx.mcraspisystem.managers.WarteschlangenManager;
import eu.goodyfx.mcraspisystem.utils.OldColors;
import eu.goodyfx.mcraspisystem.utils.PlayerNameController;
import eu.goodyfx.mcraspisystem.utils.PlayerValues;
import eu.goodyfx.mcraspisystem.utils.RaspiMessages;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarteschlangeListeners implements Listener {

    private final McRaspiSystem plugin;
    private final WarteschlangenManager settings;
    private final PlayerNameController playerNameController;
    private final RequestManager requestManager;
    private final UserManager userManager;
    private final RaspiMessages data;

    private final Map<InetAddress, String> IP_CONTAINER = new HashMap<>();


    public WarteschlangeListeners(McRaspiSystem plugin) {
        this.plugin = plugin;
        this.playerNameController = plugin.getModule().getPlayerNameController();
        this.requestManager = plugin.getModule().getRequestManager();
        this.userManager = plugin.getModule().getUserManager();
        this.settings = plugin.getModule().getWarteschlangenManager();
        this.data = plugin.getModule().getRaspiMessages();
        plugin.setListeners(this);
    }

    private void checkSystemLocations(Player player, LocationManager manager) {
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

    private void checkNewbie(Player player) {
        if (!player.isPermissionSet("group.spieler") && (!requestManager.isBlocked(player))) {
            Bukkit.getOnlinePlayers().forEach(all -> {
                if (all.isPermissionSet("system.moderator") || all.isOp()) {
                    all.sendRichMessage(data.getPrefix() + "<gray><italic>" + player.getName() + "  ist noch nicht registriert!");
                    all.sendRichMessage(data.getPrefix() + "<white>[<green><click:run_command:'/request accept " + player.getName() + "'>Annehmen<reset><white>] <white>[<red><click:run_command:'/request deny " + player.getName() + " -request_start'>Ablehnen<reset> <gray>(<green><click:suggest_command:'/request deny " + player.getName() + " '>+<reset><gray>)<white>] ");
                }
            });

        }
    }

    public void checkIP(Player player) {
        if (!IP_CONTAINER.containsKey(player.spigot().getRawAddress().getAddress())) {
            IP_CONTAINER.put(player.spigot().getRawAddress().getAddress(), player.getName());
        } else {
            Bukkit.getOnlinePlayers().forEach(all -> {
                if (all.isPermissionSet("group.team") && player.isPermissionSet("group.default")) {
                    String name = IP_CONTAINER.get(player.spigot().getRawAddress().getAddress());
                    if (!name.equalsIgnoreCase(player.getName())) {
                        all.sendRichMessage(player.getName() + " War heute bereits da! (AKA" + name + ")");
                    } else {
                        all.sendRichMessage(player.getName() + " War heute bereits da!");
                    }
                }
            });
        }
    }

    private void suitRequest(Player player) {
        checkNewbie(player);
        if (requestManager.isBlocked(player)) {
            Bukkit.getOnlinePlayers().forEach(all -> {
                all.sendRichMessage(data.getPrefix() + "<gray><italic>" + player.getName() + " wurde bereits von " + requestManager.getDeny(player) + " Abgelehnt!");

                if (all.isPermissionSet("system.moderator") || all.isOp()) {

                    if (requestManager.isBlocked("reason", player)) {
                        all.sendRichMessage(data.getPrefix() + "<gray><italic>Grund: <yellow>" + requestManager.getReason(player).replace("@", " "));
                    }
                    if (RequestCommand.freeToBan(player.getUniqueId())) {
                        all.sendRichMessage(data.getPrefix() + "<white>[<red><click:run_command:'/request kick " + player.getName() + "'>Kick<reset><white>] [<light_purple><click:run_command:'/request accept " + player.getName() + "'>Erlauben<reset><white>] // <white>[<gold><click:run_command:'/tempban " + player.getName() + " Überdenk dein Leben --MOD'>Ban<reset><white>]");
                    } else {
                        all.sendRichMessage(data.getPrefix() + "<white>[<red><click:run_command:'/request kick " + player.getName() + "'>Kick<reset><white>] [<light_purple><click:run_command:'/request accept " + player.getName() + "'>Erlauben<reset><white>]");

                    }
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoined(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        LocationManager manager = plugin.getModule().getLocationManager();
        checkSystemLocations(player, manager);
        checkIP(player);
        //Check if joining Player joins in Active World
        settings.join(player);
        settings.setHeader();
        //REQUEST
        suitRequest(player);

        plugin.getModule().getPrefixManager().checkOld(player);
        playerNameController.setPlayerList(player);
        playerJoinEvent.joinMessage(MiniMessage.miniMessage().deserialize(OldColors.convert(plugin.getModule().getJoinMessageManager().get(player))));

        if (userManager.hasTimePlayed(player, 100) && (!player.isPermissionSet("system.bypass"))) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " permission set system.bypass");

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

    }


}
