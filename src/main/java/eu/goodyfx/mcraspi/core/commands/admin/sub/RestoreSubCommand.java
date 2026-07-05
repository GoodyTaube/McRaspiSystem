package eu.goodyfx.mcraspi.core.commands.admin.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.commands.RaspiSubCommand;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RestoreSubCommand implements RaspiSubCommand {

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> build() {
        return Commands.literal("restore").then(Commands.literal("inv").then(Commands.argument("spieler", StringArgumentType.string()).suggests(suggestOnline()).executes(this::execute)));
    }


    private Integer execute(CommandContext<CommandSourceStack> context) {
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer((Player) context.getSource().getSender());
        String playerName = context.getArgument("spieler", String.class);
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            raspiPlayer.sendDebugMessage(playerName + " ist nicht online.");
            return 1;
        }

        raspiPlayer.sendDebugMessage("<green>Das inventar von " + playerName + " wurde zurückgesetzt.");
        return Command.SINGLE_SUCCESS;
    }
}
