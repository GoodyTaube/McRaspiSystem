package eu.goodyfx.system.core.events;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.commands.InHeadCommandContainer;
import eu.goodyfx.system.core.commands.SitCommandContainer;
import eu.goodyfx.system.core.database.RaspiManagement;
import eu.goodyfx.system.core.database.RaspiPlayer;
import eu.goodyfx.system.core.managers.WarteschlangenManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class PlayerLifecycleListener implements Listener {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    public PlayerLifecycleListener() {
        McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
        plugin.setListeners(this);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent joinEvent) {
        Player player = joinEvent.getPlayer();
        plugin.getDebugger().debug(String.format("[Lifecycle] Init %s to RaspiPLayers!", player.getName()));
        if (player.isInvulnerable()) {
            player.setInvulnerable(false);
        }
        if (!player.isCollidable()) {
            player.setCollidable(true);
        }
        Raspi.playerLifeCycleService().playerJoinHandler(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player bukkitPlayer = event.getPlayer();
        stopInHead(bukkitPlayer); //Remove InHead
        SitCommandContainer.endSitting(bukkitPlayer); //Falls der Spieler gesessen hat.
        RaspiPlayer player = Raspi.playerLifeCycleService().getRaspiPlayer(bukkitPlayer);
        RaspiManagement management = player.userManagement();
        if (plugin.getConfig().getBoolean("Utilities.afk.autoAFK")) {
            player.settings().setAuto_afk(true);
        }
        player.userData().setLastSeen(System.currentTimeMillis());
        if (player.settings().isAfk()) {
            Bukkit.dispatchCommand(player.getPlayer(), "afk");
            Raspi.debugger().debug(player.getPlayer().getName() + "  was AFK while Disconnecting! Removed AFK status!");
        }
        player.nameController.resetRandom();
        warteschlange(bukkitPlayer); //ALT UND MUSS GETAUSCHT WERDEN

        Raspi.playerLifeCycleService().playerLeaveHandler(bukkitPlayer);
        //MESSAGES

        if (!management.isBanned()) {
            plugin.getHookManager().getDiscordIntegration().send(String.format("`[System] <%s> hat uns verlassen.`", bukkitPlayer.getName()));
            if (plugin.getConfig().getBoolean("Utilities.leaveMessage")) {
                event.quitMessage(MiniMessage.miniMessage().deserialize(plugin.getModule().getRaspiMessages().getLeave(player.getColorName())));
            }
        }

    }


    private void stopInHead(Player bukkitPlayer) {
        UUID observerUUID = InHeadCommandContainer.getInHeadCache()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(bukkitPlayer.getUniqueId()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (observerUUID == null) return;

        Player observer = Bukkit.getPlayer(observerUUID);
        if (observer == null) return;

        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(observer);

        InHeadCommandContainer.stopInHead(plugin, raspiPlayer);
    }

    private void warteschlange(Player player) {
        WarteschlangenManager settings = plugin.getModule().getWarteschlangenManager();
        UUID uuid = player.getUniqueId();
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
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        //Check if UserMigration is Running
        if (Bukkit.getServer().hasWhitelist()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, MiniMessage.miniMessage().deserialize("<dark_red>User Migration Läuft! <red><br>Warte auf Abschluss...."));
            return;
        }
    }

}
