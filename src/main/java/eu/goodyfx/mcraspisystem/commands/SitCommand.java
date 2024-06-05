package eu.goodyfx.mcraspisystem.commands;

import eu.goodyfx.goodysutilities.GoodysUtilities;
import eu.goodyfx.goodysutilities.utils.Data;
import eu.goodyfx.mcraspisystem.McRaspiSystem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SitCommand implements CommandExecutor {

    private static final Map<UUID, Entity> sits = new HashMap<>();

    private final Data data;

    public SitCommand(McRaspiSystem plugin) {
        this.data = plugin.getData();
        plugin.setCommand("sit", this);
    }

    public static void add(Player player, Location location, double sitAmp) {
        if (!location.getBlock().getRelative(BlockFace.UP).getType().isAir()) {
            return;
        }
        location.add(0.5, 0, 0.5);
        location.subtract(0, sitAmp, 0);
        Interaction entity = (Interaction) player.getWorld().spawnEntity(location, EntityType.INTERACTION);
        entity.setInteractionWidth(1f);
        entity.setInteractionHeight(1f);
        entity.setGravity(false);
        entity.addPassenger(player);
        sits.put(player.getUniqueId(), entity);
    }

    public static Map<UUID, Entity> getSits() {
        return sits;
    }

    public static void remove(Player player) {
        if (sits.containsKey(player.getUniqueId())) {
            Entity entity = sits.get(player.getUniqueId());
            entity.remove();
            sits.remove(player.getUniqueId());
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player && (args.length == 0)) {

            if (player.getLocation().subtract(0, 1, 0).getBlock().getType().equals(Material.AIR)) {
                player.sendRichMessage(data.getPrefix() + "<red>Du kannst nicht in der Luft sitzen.");
                return true;
            }

            if (sits.containsKey(player.getUniqueId())) {
                sits.get(player.getUniqueId()).remove();
                return true;
            }
            Interaction entity = (Interaction) player.getWorld().spawnEntity(player.getLocation().subtract(0, 0.05, 0), EntityType.INTERACTION);
            entity.setInteractionWidth(1f);
            entity.setInteractionHeight(1f);
            entity.setGravity(false);
            entity.addPassenger(player);
            sits.put(player.getUniqueId(), entity);


        }
        return false;
    }

}
