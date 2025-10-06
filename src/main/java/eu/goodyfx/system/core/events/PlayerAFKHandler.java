package eu.goodyfx.system.core.events;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.commandsOLD.AFKCommand;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerAFKHandler implements Listener {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    private final List<UUID> changedWorld = new ArrayList<>();

    public PlayerAFKHandler() {
        plugin.setListeners(this);
    }

    public void checkUp(Player player) {
        RaspiPlayer raspiPlayer = Raspi.players().get(player);
        if (raspiPlayer.getUserSettings().isAfk()) {
            raspiPlayer.performCommand("/afk");
        }
    }

    @EventHandler
    public void playerChangeWorld(PlayerChangedWorldEvent event) {
        changedWorld.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        checkUp(event.getPlayer());
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        checkUp(event.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent moveEvent) {
        Player player = moveEvent.getPlayer();
        RaspiPlayer raspiPlayer = Raspi.players().get(player);
        AFKCommand.getPlayerIDLE().remove(player.getUniqueId());

        if (raspiPlayer.getUserSettings().isAfk()) {

            if (!changedWorld.contains(player.getUniqueId())) {
                Location locationStart = Raspi.players().getAfkContainer().get(player.getUniqueId());
                if (locationStart == null) {
                    return;
                }
                if (locationStart.distance(moveEvent.getTo()) > 2 && !plugin.getModule().getWarteschlangenManager().playersQueue.contains(player.getUniqueId())) {
                    raspiPlayer.performCommand("afk");
                }
            } else {
                Raspi.players().getAfkContainer().put(player.getUniqueId(), player.getLocation());
                changedWorld.remove(player.getUniqueId());
            }
        }

    }


}
