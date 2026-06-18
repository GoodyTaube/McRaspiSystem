package eu.goodyfx.mcraspi.modules.reise.commands.reise.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.commands.RaspiSubCommand;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.modules.reise.managers.ReiseLocationManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class RemoveSubCommand implements RaspiSubCommand {
    @Override
    public ArgumentBuilder<CommandSourceStack, ?> build() {
        return Commands.literal("remove").then(Commands.argument("id", IntegerArgumentType.integer(1, 9999999)).executes(this::removeExecute));
    }

    private int removeExecute(CommandContext<CommandSourceStack> context) {

        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer((Player) context.getSource().getSender());
        int id = context.getArgument("id", Integer.class);


        if (!ReiseLocationManager.exist(id)) {
            raspiPlayer.sendMessage("<red>Die ID existiert nicht.", true);
            return 1;
        }

        ReiseLocationManager.remove(id);
        raspiPlayer.sendMessage("<green>Du hast %s entfernt. Die ID ist nun wieder frei!", true);

        return Command.SINGLE_SUCCESS;
    }

}



