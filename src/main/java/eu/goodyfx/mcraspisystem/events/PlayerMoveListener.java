package eu.goodyfx.mcraspisystem.events;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.AFKCommand;
import eu.goodyfx.mcraspisystem.managers.UserManager;
import eu.goodyfx.mcraspisystem.managers.WarteschlangenManager;
import eu.goodyfx.mcraspisystem.utils.PlayerValues;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class   PlayerMoveListener implements Listener {

    private final McRaspiSystem plugin;
    private final WarteschlangenManager warteschlangenManager;
    private final UserManager userManager;
    private final List<UUID> playerChangedWorld = new ArrayList<>();


    public PlayerMoveListener(McRaspiSystem plugin) {
        this.warteschlangenManager = plugin.getModule().getWarteschlangenManager();
        this.userManager = plugin.getModule().getUserManager();
        this.plugin = plugin;
        plugin.setListeners(this);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        playerChangedWorld.add(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSneak(PlayerToggleSneakEvent sneakEvent) {

        if (!sneakEvent.isSneaking() && (warteschlangenManager.getQueuedPlayers().contains(sneakEvent.getPlayer().getUniqueId()) && (userManager.hasPersistantValue(sneakEvent.getPlayer(), PlayerValues.AFK)))) {
            Bukkit.dispatchCommand(sneakEvent.getPlayer(), "afk");
        }
    }

    @EventHandler
    public void onJump(PlayerJumpEvent jumpEvent) {
        if (warteschlangenManager.getQueuedPlayers().contains(jumpEvent.getPlayer().getUniqueId()) && (userManager.hasPersistantValue(jumpEvent.getPlayer(), PlayerValues.AFK))) {
            Bukkit.dispatchCommand(jumpEvent.getPlayer(), "afk");
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();


        AFKCommand.getPlayerIDLE().remove(player.getUniqueId());

        if (userManager.hasPersistantValue(player, PlayerValues.AFK)) {
            if (!playerChangedWorld.contains(player.getUniqueId())) {
                if (userManager.getAfkContainer().containsKey(player.getUniqueId()) && userManager.getAfkContainer().get(player.getUniqueId()).distance(event.getTo()) > 2 && !warteschlangenManager.playersQueue.contains(player.getUniqueId())) {
                    Bukkit.dispatchCommand(event.getPlayer(), "afk");

                }
            } else {
                userManager.getAfkContainer().put(player.getUniqueId(), player.getLocation());
                playerChangedWorld.remove(event.getPlayer().getUniqueId());
            }
        }
    }


}
