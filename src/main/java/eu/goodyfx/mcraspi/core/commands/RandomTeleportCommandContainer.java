package eu.goodyfx.mcraspi.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.entity.TeleportFlag;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;

public class RandomTeleportCommandContainer extends RaspiCommand {

    private final Random random;

    public RandomTeleportCommandContainer(McRaspiSystem plugin) {
        super(plugin);
        this.random = plugin.getRandom();
    }

    @Override
    public String getName() {
        return "randomTP";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return command();
    }

    @Override
    public String[] getAliases() {
        return new String[]{"rtp"};
    }

    public LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("randomTP").executes(this::execute).build();
    }

    @Getter
    private static final Map<UUID, Location> locationCache = new HashMap<>();

    private static final String WARN_ALREADY_USED = "<red>Du hast deinen Random Teleport <underlined>heute</underlined> schon benutzt.";
    private static final String CONFIG_PATH_CENTER = "Utilities.randomTP.center";
    private static final String CONFIG_PATH_RADIUS = "Utilities.randomTP.radius";

    private Integer execute(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            context.getSource().getSender().sendRichMessage("COMMAND_PLAYER_ONLY");
            return 1;
        }

        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        UUID playerUUID = raspiPlayer.getUUID();
        if (isCached(playerUUID)) {
            raspiPlayer.sendActionBar(WARN_ALREADY_USED);
        }
        teleportAsync(player);
        return Command.SINGLE_SUCCESS;
    }


    private void teleportAsync(Player player) {

        if (isCached(player.getUniqueId())) {
            Location cached = locationCache.get(player.getUniqueId());
            player.teleport(cached, PlayerTeleportEvent.TeleportCause.PLUGIN, TeleportFlag.Relative.VELOCITY_ROTATION);
            player.playSound(cached, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
            return;
        }

        Location base = getConfigCenterLocation().clone();
        int radius = Math.max(1, getConfigRadius());
        int x = random.nextInt(radius * 2) - radius;
        int z = random.nextInt(radius * 2) - radius;
        base.add(x, 0, z); //Random Location

        //ASYNC
        base.getWorld().getChunkAtAsync(base).thenAccept(chunk -> {
            int y = chunk.getWorld().getHighestBlockYAt(base.getBlockX(), base.getBlockZ());
            Location safeLocation = new Location(chunk.getWorld(), base.getX(), y + 1, base.getZ());

            //SYNC
            Bukkit.getScheduler().runTask(getPlugin(), () -> {
                if (!player.isOnline()) {
                    return;
                }
                if (!safeLocation.getBlock().isPassable() || !safeLocation.clone().add(0, 1, 0).getBlock().isPassable()) {
                    teleportAsync(player);
                    return;
                }
                Raspi.debugger().debug(String.format("New Random Teleport location for %s @%s", player.getUniqueId(), Raspi.debugger().formatLocation(safeLocation)));
                locationCache.put(player.getUniqueId(), safeLocation);
                player.teleport(safeLocation, PlayerTeleportEvent.TeleportCause.PLUGIN, TeleportFlag.Relative.VELOCITY_ROTATION);
            });

        });
    }


    /**
     * Check if players random location is Cached
     *
     * @param player The Player UUID
     * @return True if player has random location cached.
     */
    public static boolean isCached(UUID player) {
        return locationCache.containsKey(player);
    }

    /**
     * Get the config Center Point to math random teleport Radius;
     *
     * @return The Config Center Location
     */
    private Location getConfigCenterLocation() {
        Location location = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        FileConfiguration config = getPlugin().getConfig();
        if (!config.contains(CONFIG_PATH_CENTER)) {
            Raspi.debugger().debug(String.format("Cant find random Teleport Center Position on Path:%s", CONFIG_PATH_CENTER));
            return location;
        }
        String[] compactedCenterLocation = Objects.requireNonNull(config.getString(CONFIG_PATH_CENTER)).split(" ");
        double x = Double.parseDouble(compactedCenterLocation[0]);
        double y = Double.parseDouble(compactedCenterLocation[1]);
        double z = Double.parseDouble(compactedCenterLocation[2]);
        location.setX(x);
        location.setY(y);
        location.setZ(z);
        return location;
    }

    /**
     * Read Config Radius value
     *
     * @return The Radius or 0
     */
    private int getConfigRadius() {
        if (!getPlugin().getConfig().contains(CONFIG_PATH_RADIUS)) {
            return 0;
        }
        return getPlugin().getConfig().getInt(CONFIG_PATH_RADIUS);
    }

}
