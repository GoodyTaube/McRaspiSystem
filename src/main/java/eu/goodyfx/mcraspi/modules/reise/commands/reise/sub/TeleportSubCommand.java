package eu.goodyfx.mcraspi.modules.reise.commands.reise.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.commands.RaspiSubCommand;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.core.utils.RaspiSounds;
import eu.goodyfx.mcraspi.modules.reise.managers.ReiseLocationManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportSubCommand implements RaspiSubCommand {
    @Override
    public ArgumentBuilder<CommandSourceStack, ?> build() {
        return Commands.literal("teleport").then(Commands.argument("id", IntegerArgumentType.integer(1, 9999999)).executes(this::idPort));
    }


    private int idPort(CommandContext<CommandSourceStack> context) {
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer((Player) context.getSource().getSender());
        int id = context.getArgument("id", Integer.class);

        if (!ReiseLocationManager.exist(id)) {
            raspiPlayer.sendMessage("<red>Die Id existiert nicht.", true);
            return 1;
        }

        if (!ReiseLocationManager.hasEntry(id, "FREE")) {
            Location location = ReiseLocationManager.get(id);
            raspiPlayer.getPlayer().teleport(location);
        } else {
            raspiPlayer.sendMessage("<gray>Dieser Platz ist noch Frei! Lade freunde ein <3");
        }
        raspiPlayer.playSound(RaspiSounds.SUCCESS);
        return Command.SINGLE_SUCCESS;
    }
}
