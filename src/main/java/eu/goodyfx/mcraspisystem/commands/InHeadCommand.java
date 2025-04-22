package eu.goodyfx.mcraspisystem.commands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.utils.RaspiPermission;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InHeadCommand implements CommandExecutor {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    public InHeadCommand() {
        plugin.setCommand("inHead", this);
    }

    @Getter
    private static final Map<UUID, UUID> inHeadContainer = new HashMap<>();
    @Getter
    private static final Map<UUID, Location> inHeadLocation = new HashMap<>();
    @Getter
    private static final Map<UUID, GameMode> inHeadGamemode = new HashMap<>();


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (sender instanceof Player dummy) {
            RaspiPlayer raspiPlayer = new RaspiPlayer(dummy);

            if (args.length == 0 && inHeadContainer.containsKey(dummy.getUniqueId())) {
                removeInHead(raspiPlayer);
            }

            if (args.length == 1) {
                if (dummy.isPermissionSet(RaspiPermission.MOD.getPermissionValue())) {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target != null) {
                        if (target == dummy) {
                            raspiPlayer.sendMessage("<red>Du kannst dich nicht selber prüfen.", true);
                            return true;
                        }
                        /**
                         if (!plugin.getModule().getUserManager().hasTimePlayed(target, 10)) {
                         raspiPlayer.sendMessage("<red>Der Spieler ist nicht NEU und kann nicht geprüft werden.");
                         return true;
                         }**/

                        performInhead(raspiPlayer, target);

                    } else raspiPlayer.sendMessage(plugin.getModule().getRaspiMessages().playerNotOnline(args[0]));
                }
            }
        }

        return false;
    }

    private void performInhead(RaspiPlayer player, Player target) {
        inHeadContainer.put(player.getUUID(), target.getUniqueId());
        inHeadLocation.put(player.getUUID(), player.getLocation());
        inHeadGamemode.put(player.getUUID(), player.getPlayer().getGameMode());
        player.getPlayer().setGameMode(GameMode.SPECTATOR);
        player.getPlayer().setSpectatorTarget(target);
        player.sendMessage("<gray>Du beobachtest nun: <aqua>" + plugin.getRaspiPlayer(target).getName(), true);
        plugin.getHookManager().getDiscordIntegration().send(String.format("`Raspi-InHead:: %s ----> %s`", player.getPlayer().getName(), target.getName()));

    }

    private void removeInHead(RaspiPlayer player) {
        Location location = inHeadLocation.get(player.getUUID());
        inHeadLocation.remove(player.getUUID());
        player.getPlayer().teleport(location);
        player.getPlayer().setGameMode(inHeadGamemode.get(player.getUUID()));
        inHeadGamemode.remove(player.getUUID());
        player.sendMessage("<gray>Du beobachtest: <aqua>" + Bukkit.getOfflinePlayer(inHeadContainer.get(player.getUUID())).getName() + " <gray>nicht mehr.", true);
        inHeadContainer.remove(player.getUUID());

    }

}
