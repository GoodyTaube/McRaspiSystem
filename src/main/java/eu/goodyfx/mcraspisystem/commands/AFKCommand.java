package eu.goodyfx.mcraspisystem.commands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.events.PlayerAFKEvent;
import eu.goodyfx.mcraspisystem.managers.LocationManager;
import eu.goodyfx.mcraspisystem.managers.WarteschlangenManager;
import eu.goodyfx.mcraspisystem.utils.PlayerValues;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AFKCommand implements CommandExecutor {

    private final WarteschlangenManager warteschlangenManager;
    private final LocationManager locationManager;

    private final McRaspiSystem plugin;

    public AFKCommand(McRaspiSystem plugin) {
        this.plugin = plugin;
        this.warteschlangenManager = plugin.getModule().getWarteschlangenManager();
        this.locationManager = plugin.getModule().getLocationManager();
        plugin.setCommand("afk", this);
    }

    @Getter
    private static final Map<UUID, Integer> playerIDLE = new HashMap<>();


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player playerD) {
            RaspiPlayer player = plugin.getRaspiPlayer(playerD);
            if (args.length == 0) {
                Location playerLocation = playerD.getLocation();

                if (player.userManager().hasPersistantValue(playerD, PlayerValues.AFK)) {
                    player.userManager().updateAFK(playerD);
                    warteschlangenManager.setHeader();
                    playerD.setCollidable(true);
                    playerD.setSleepingIgnored(false);
                    playerD.sendActionBar(MiniMessage.miniMessage().deserialize("<red>Du bist nicht mehr AFK"));
                    player.nameController().setPlayerList(playerD);
                    return true;

                }
                player.userManager().updateAFK(playerD);

                if (!playerLocation.getWorld().getName().equalsIgnoreCase(locationManager.getWorldName("waiting")) && warteschlangenManager.queueSize() > 0 && !warteschlangenManager.playersQueue.contains(playerD.getUniqueId())) {
                    warteschlangenManager.queue();
                    warteschlangenManager.addToQueue(playerD.getUniqueId(), playerLocation);
                }
                playerD.setInvulnerable(false);
                warteschlangenManager.setHeader();
                playerD.setSleepingIgnored(true);
                playerD.sendActionBar(MiniMessage.miniMessage().deserialize("<yellow>Du bist nun AFK"));
                player.nameController().setPlayerList(playerD);

                PlayerAFKEvent afkEvent = new PlayerAFKEvent(new RaspiPlayer(playerD));
                Bukkit.getPluginManager().callEvent(afkEvent);
                return true;
            }

        }
        return false;
    }

}
