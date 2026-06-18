package eu.goodyfx.mcraspi.core.events;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.commands.AFKCommandContainer;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
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
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        if (raspiPlayer.isAfk()) {
            raspiPlayer.performCommand("afk");
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

        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);

        AFKCommandContainer.getPlayerIDLE().remove(player.getUniqueId());

        if (raspiPlayer.isAfk()) {
            if (!changedWorld.contains(player.getUniqueId())) {
                Location locationStart = Raspi.playerLifeCycleService().getAfkContainer().get(player.getUniqueId());
                if (locationStart == null) {
                    return;
                }
                if (locationStart.distance(moveEvent.getTo()) > 2 && !plugin.getModule().getWarteschlangenManager().playersQueue.contains(player.getUniqueId())) {
                    raspiPlayer.performCommand("afk");
                }
            } else {
                Raspi.playerLifeCycleService().getAfkContainer().put(player.getUniqueId(), player.getLocation());
                changedWorld.remove(player.getUniqueId());
            }
        }


    }

}
