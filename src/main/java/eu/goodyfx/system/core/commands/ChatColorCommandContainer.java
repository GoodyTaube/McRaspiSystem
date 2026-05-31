package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.commands.arguments.ChatColorCommandArgument;
import eu.goodyfx.system.core.database.RaspiPlayer;
import eu.goodyfx.system.core.utils.RaspiFormatting;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class ChatColorCommandContainer {

    public static LiteralCommandNode<CommandSourceStack> chatColorCommand() {
        return Commands.literal("chatcolor")
                .then(Commands.argument("farbe", new ChatColorCommandArgument())
                        .executes(ChatColorCommandContainer::perform)).build();
    }

    public static int perform(CommandContext<CommandSourceStack> context) {

        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer((Player) context.getSource().getSender());
        RaspiFormatting color = context.getArgument("farbe", RaspiFormatting.class);
        String value = null;
        if (color == RaspiFormatting.HEX) {
            String[] input = context.getInput().split(" ");
            value = String.format(RaspiFormatting.HEX.getValue(), input[1]);
        }
        String output = value != null ? value : color.getValue();
        Component colorCleanUp = RaspiFormatting.COLOR_ONLY_MESSAGE.deserialize(output);
        String cleanedColor = MiniMessage.miniMessage().serialize(colorCleanUp);

        raspiPlayer.nameController.setPlayerColor(cleanedColor);
        raspiPlayer.sendMessage(String.format("<gray>Deine Neue Chat Farbe ist jetzt: %s▆▇ %s %s▇▆", raspiPlayer.getColor(), raspiPlayer.getColorName(), raspiPlayer.getColor()), true);
        return Command.SINGLE_SUCCESS;
    }

}
