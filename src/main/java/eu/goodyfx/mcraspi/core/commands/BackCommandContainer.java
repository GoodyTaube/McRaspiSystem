package eu.goodyfx.mcraspi.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackCommandContainer extends RaspiCommand {

    @Getter
    private static final Map<UUID, Location> locationsCache = new HashMap<>();

    @Override
    public String getName() {
        return "back";
    }

    @Override
    public String getDescription() {
        return "Command to reverse Location";
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return backCommand();
    }

    public BackCommandContainer(McRaspiSystem plugin) {
        super(plugin);
    }


    public LiteralCommandNode<CommandSourceStack> backCommand() {
        return Commands.literal(getName())
                .executes(context -> {
                    if (!(context.getSource().getSender() instanceof Player player)) {
                        return Command.SINGLE_SUCCESS;
                    }
                    RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
                    if (locationsCache.containsKey(player.getUniqueId())) {
                        Location location = locationsCache.get(player.getUniqueId());
                        raspiPlayer.sendActionBar("<green>Teleportation zur letzten bekannten position.");
                        raspiPlayer.getPlayer().teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                        Raspi.debugger().debug(String.format("[BackCommand] Teleport %s to %s", player.getName(), Raspi.debugger().formatLocation(location)));
                    } else {
                        raspiPlayer.sendMessage("<red>Deine letzte position ist <u>nicht</u> im Cache vorhanden.", true);
                        Raspi.debugger().debug("[BackCommand] Failed to load last location for " + player.getName());
                    }
                    return Command.SINGLE_SUCCESS;
                }).build();
    }

}
