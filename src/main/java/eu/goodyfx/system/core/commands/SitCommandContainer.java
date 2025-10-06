package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.McRaspiSystem;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SitCommandContainer {

    public static Map<UUID, Entity> sitContainer = new HashMap<>();

    public static LiteralCommandNode<CommandSourceStack> sitCommand() {
        return Commands.literal("sit").executes(context -> {
            Entity entity = context.getSource().getExecutor();
            if (!(entity instanceof Player player)) {
                return Command.SINGLE_SUCCESS;
            }

            if (!sitContainer.containsKey(player.getUniqueId())) {
                performSit(player, player.getLocation(), 0.01);
            } else {
                endSitting(player);
            }

            return Command.SINGLE_SUCCESS;
        }).build();
    }

    public static void performSit(Player player, Location location, double sitAmp) {
        location.subtract(0, sitAmp, 0);
        if (!location.getBlock().getRelative(BlockFace.UP).getType().isAir() || player.getLocation().subtract(0, 1, 0).getBlock().getType().equals(Material.AIR)) {
            player.sendRichMessage("<red>ERROR::<yellow>AIR SITTING");
            return;
        }
        Interaction sitBase = (Interaction) player.getWorld().spawnEntity(location, EntityType.INTERACTION);
        sitBase.setInteractionWidth(1f);
        sitBase.setInteractionHeight(1f);
        sitBase.setGravity(false);
        sitBase.addPassenger(player);
        sitContainer.put(player.getUniqueId(), sitBase);
        JavaPlugin.getPlugin(McRaspiSystem.class).getDebugger().info(String.format("%s::SITTING_STATE_CHANGE::ADDED", player.getName()));
    }

    public static void endSitting(Player player) {
        if (sitContainer.containsKey(player.getUniqueId())) {
            JavaPlugin.getPlugin(McRaspiSystem.class).getDebugger().info(String.format("%s::SITTING_STATE_CHANGE::REMOVED", player.getName()));
            Entity entity = sitContainer.get(player.getUniqueId());
            if(entity != null){
                entity.remove();
            }
        }
        sitContainer.remove(player.getUniqueId());

    }


}
