package eu.goodyfx.mcraspisystem.commands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RandomTeleportCommand implements CommandExecutor {

    private Random random = new Random();
    private final McRaspiSystem plugin;

    public RandomTeleportCommand(McRaspiSystem plugin) {
        this.plugin = plugin;
        plugin.setCommand("randomtp", this);
    }

    private static Map<UUID, Location> playerContainer = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (playerContainer.containsKey(player.getUniqueId())) {
                player.teleport(playerContainer.get(player.getUniqueId()));
                player.sendActionBar(MiniMessage.miniMessage().deserialize("<red>Du hast deinen <underlined>heutigen</underlined> Random TP schon benutzt!"));
                return true;
            }
            Location location = getCenterLocation();
            location.setX((random.nextInt(getRadius()) + location.getX()));
            location.setZ((random.nextInt(getRadius()) + location.getZ()));

            double highestBlockYAt = player.getWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ());
            location.setY(highestBlockYAt);

            if (!location.getBlock().getType().equals(Material.WATER)) {
                playerContainer.put(player.getUniqueId(), location);
                player.teleport(location.add(0, 1, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
            } else {
                player.performCommand("randomtp");
            }
        }
        return false;
    }

    public static Map<UUID, Location> getPlayerContainer() {
        return playerContainer;
    }

    public Location getCenterLocation() {
        Location location = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        if (plugin.getConfig().contains("Utilities.randomTP.center")) {
            String[] center = Objects.requireNonNull(plugin.getConfig().getString("Utilities.randomTP.center")).split(" ");
            double x = Double.parseDouble(center[0]);
            double y = Double.parseDouble(center[1]);
            double z = Double.parseDouble(center[2]);
            location.setX(x);
            location.setY(y);
            location.setZ(z);
        }

        return location;
    }

    public Integer getRadius() {
        int rad = 0;
        if (plugin.getConfig().contains("Utilities.randomTP.radius")) {
            rad = plugin.getConfig().getInt("Utilities.randomTP.radius");
        }
        return rad;
    }

}
