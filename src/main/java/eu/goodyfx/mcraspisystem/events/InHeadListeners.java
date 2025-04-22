package eu.goodyfx.mcraspisystem.events;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.InHeadCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class InHeadListeners implements Listener {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    public InHeadListeners() {
        plugin.setListeners(this);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent teleportEvent) {
        Player player = teleportEvent.getPlayer();
        if (InHeadCommand.getInHeadContainer().containsValue(player.getUniqueId())) {
            for (UUID uuid : InHeadCommand.getInHeadContainer().keySet()) {
                if (InHeadCommand.getInHeadContainer().get(uuid).equals(player.getUniqueId())) {
                    Player target = Bukkit.getPlayer(uuid);
                    assert target != null;

                    target.setSpectatorTarget(null);
                    target.sendMessage(MiniMessage.miniMessage().deserialize("<red>Teleport Erkannt!"));
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onSpec(PlayerStopSpectatingEntityEvent event) {
        Player player = event.getPlayer();
        if (InHeadCommand.getInHeadContainer().containsKey(player.getUniqueId())) {
            player.sendActionBar(MiniMessage.miniMessage().deserialize("<red>Bitte nutze /inHead um den Modus zu verlassen."));
        }
    }

}
