package eu.goodyfx.system.core.events;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.commands.SitCommandContainer;
import eu.goodyfx.system.core.database.RaspiAccount;
import eu.goodyfx.system.core.database.RaspiPlayer;
import eu.goodyfx.system.core.managers.WarteschlangenManager;
import eu.goodyfx.system.core.utils.Raspi;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerLifecycleListener implements Listener {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    public PlayerLifecycleListener() {
        McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
        plugin.setListeners(this);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent joinEvent) {
        Player player = joinEvent.getPlayer();
        plugin.getDebugger().info(String.format("[Lifecycle] Init %s to RaspiPLayers!", player.getName()));
        if (player.isInvulnerable()) {
            player.setInvulnerable(false);
        }
        if (!player.isCollidable()) {
            player.setCollidable(true);
        }
        Raspi.players().getContextPlayer(player.getUniqueId()).thenAccept(context -> {
            if (context instanceof RaspiPlayer online) {
                online.sendMessage("JOIN MESSAGE");
            }
        });

        banCheck(player.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player bukkitPlayer = event.getPlayer();

        SitCommandContainer.endSitting(bukkitPlayer); //Falls der Spieler gesessen hat.
        RaspiPlayer player = Raspi.players().getActive(event.getPlayer());
        if (plugin.getConfig().getBoolean("Utilities.afk.autoAFK")) {
            player.settings().setAuto_afk(true);
        }
        player.userData().setLastSeen(System.currentTimeMillis());
        if (player.settings().isAfk()) {
            Bukkit.dispatchCommand(player.getPlayer(), "afk");
            plugin.getLogger().info(player.getPlayer().getName() + "  was AFK while Disconnecting! Removed AFK status!");
        }
        player.nameController.resetRandom();
        if (plugin.getConfig().getBoolean("Utilities.leaveMessage")) {
            event.quitMessage(MiniMessage.miniMessage().deserialize(plugin.getModule().getRaspiMessages().getLeave(player.getColorName())));
        }
        warteschlange(bukkitPlayer); //ALT UND MUSS GETAUSCHT WERDEN


        Raspi.players().getActivePlayers().remove(bukkitPlayer.getUniqueId());

        RaspiAccount raspiAccount = Raspi.players().getAccountsCache().get(bukkitPlayer.getUniqueId());
        if (raspiAccount != null) {
            CompletableFuture.runAsync(raspiAccount::save, plugin.getAsyncExecutor());
        }
        plugin.getDebugger().info(String.format("[Lifecycle] Removed %s from RaspiPLayers!", bukkitPlayer.getName()));
        plugin.getHookManager().getDiscordIntegration().send(String.format("`[System] <%s> hat uns verlassen.`", bukkitPlayer.getName()));
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
        //Pre Load PlayerData from DB
        plugin.getDebugger().info(String.format("[Lifecycle] loading %s async to  RaspiPLayers!", event.getName()));
    }

    private void banCheck(UUID uuid) {

        Raspi.players().getContextPlayer(uuid).thenAccept(context -> {
            if (context instanceof RaspiPlayer player) {
                if (context.userManagement().isBanned()) {
                    if (System.currentTimeMillis() < context.userManagement().getBan_expire()) {
                        player.getPlayer().kick(MiniMessage.miniMessage().deserialize(String.format("<red>McRaspi.com <gray><b>-</b> <red>Disconnect<br><br><red><b>Du wurdest temporär gesperrt!</b><br>" + "<gray>Du wurdest von: <red>%s <gray>für folgendes gesperrt:<br>" + "<yellow>'%s'<br><br>" + "<gray>Du wirst am <red>%s <gray>entsperrt.", context.userManagement().getBan_owner(), context.userManagement().getBan_message(), new SimpleDateFormat("dd-MM-yyyy HH:mm").format(context.userManagement().getBan_expire()))), PlayerKickEvent.Cause.BANNED);
                    } else {
                        context.userManagement().performUnban();
                        plugin.getLogger().info(plugin.getModule().getRaspiMessages().getPrefix() + " " + player.getPlayer().getName() + " wurde Entsperrt weil seine sperrzeit abgelaufen ist.");
                    }
                }
            }
        });
    }

}
