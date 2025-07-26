package eu.goodyfx.system.core.commandsOLD;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackCommand implements CommandExecutor {


    private final McRaspiSystem plugin;
    private static final Map<UUID, Location> locationMap = new HashMap<>();

    public BackCommand(McRaspiSystem plugin) {
        this.plugin = plugin;
        plugin.setCommand("back", this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            return playerCommand(player, args);
        } else {
            return senderCommand(sender, args);
        }
    }

    private boolean playerCommand(Player player, String[] args) {
        RaspiPlayer raspiPlayer = plugin.getRaspiPlayer(player);
        if (args.length == 0) {
            //TELEPORT BACK
            Location location = getLocation(player);
            if (location != null) {
                player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
            } else {
                raspiPlayer.sendMessage("<red>Du hast bisher noch keine Location verlassen.");
            }
            return true;
        }

        return false;
    }

    private Location getLocation(Player player) {
        UUID uuid = player.getUniqueId();
        if (!locationMap.containsKey(uuid)) {
            return null;
        }
        return locationMap.get(uuid);
    }

    private boolean senderCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                Location location = getLocation(target);
                if (location != null) {
                    target.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                } else sender.sendRichMessage(target.getName() + "<red> hat bisher noch keine Location verlassen.");
            } else{
                sender.sendRichMessage(plugin.getModule().getRaspiMessages().playerNotOnline(args[0]));
            }
        }
        return false;
    }

    public static Map<UUID, Location> getLocationMap() {
        return locationMap;
    }

}
