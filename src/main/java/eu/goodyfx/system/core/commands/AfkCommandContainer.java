package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AfkCommandContainer {

    private static final Map<UUID, Location> afkCache = new HashMap<>();

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("afk").executes(source -> {
            if (!(source.getSource().getSender() instanceof Player player)) {
                return Command.SINGLE_SUCCESS;
            }
            RaspiPlayer raspiPlayer = Raspi.players().get(player);
            if (raspiPlayer.isInitialized()) {
                boolean afkState = raspiPlayer.getUserSettings().isAfk();
                afkState = !afkState;
                raspiPlayer.getUserSettings().setAfk(afkState);
            }
            return Command.SINGLE_SUCCESS;
        }).build();
    }

    public static int targetAFK(CommandContext<CommandSourceStack> context) {
        if(!(context.getSource().getSender() instanceof Player player)) {

        }
        return Command.SINGLE_SUCCESS;
    }


    private static void setAFK(RaspiPlayer raspiPlayer, boolean state) {

    }


}
