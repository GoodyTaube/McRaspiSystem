package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackCommandContainer {

    @Getter
    private static final Map<UUID, Location> locationsCache = new HashMap<>();

    public static LiteralCommandNode<CommandSourceStack> backCommand() {
        return Commands.literal("back")
                .executes(context -> {
                    if (!(context.getSource().getSender() instanceof Player player)) {
                        return Command.SINGLE_SUCCESS;
                    }
                    UUID uuid = player.getUniqueId();
                    RaspiPlayer raspiPlayer = Raspi.players().get(uuid);
                    if (locationsCache.containsKey(uuid)) {
                        Location location = locationsCache.get(uuid);
                        raspiPlayer.sendActionBar("<green>Teleportation zur letzten bekannten position.");
                        raspiPlayer.getPlayer().teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                        Raspi.debugger().info(String.format("[BackCommand] Teleport %s to %s %s %s %s", player.getName(), location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                    } else {
                        raspiPlayer.sendMessage("<red>Deine letzte position ist <u>nicht</u> im Cache vorhanden.", true);
                        Raspi.debugger().info("[BackCommand] Failed to load last location for " + player.getName());
                    }
                    return Command.SINGLE_SUCCESS;
                }).build();
    }
}
