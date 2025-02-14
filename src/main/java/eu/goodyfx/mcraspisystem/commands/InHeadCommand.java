package eu.goodyfx.mcraspisystem.commands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.utils.RaspiPermission;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
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

    @Getter
    private final Map<UUID, UUID> inHeadContainer = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player dummy && args.length == 1) {
            RaspiPlayer raspiPlayer = new RaspiPlayer(dummy);
            if (dummy.isPermissionSet(RaspiPermission.MOD.getPermissionValue())) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    if(target == dummy){
                        raspiPlayer.sendMessage("Du kannst dich nicht selber prüfen.");
                        return true;
                    }
                    if (!plugin.getModule().getUserManager().hasTimePlayed(target, 10)) {
                        raspiPlayer.sendMessage("<red>Der Spieler ist nicht NEU und kann nicht geprüft werden.");
                        return true;
                    }
                    inHeadContainer.put(dummy.getUniqueId(), target.getUniqueId());
                    raspiPlayer.sendMessage("<gray>Du beobachtest nun: <aqua>" + plugin.getRaspiPlayer(target).getName());
                } else raspiPlayer.sendMessage(plugin.getModule().getRaspiMessages().playerNotOnline(args[0]));
            }
        }

        return false;
    }

}
