package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.commands.arguments.ChatColorCommandArgument;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiFormatting;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatColorCommandContainer {

    public static LiteralCommandNode<CommandSourceStack> chatColorCommand() {
        return Commands.literal("chatcolor")
                .then(Commands.argument("color", new ChatColorCommandArgument())
                        .executes(context -> {
                            final RaspiFormatting colors = context.getArgument("color", RaspiFormatting.class);
                            String colorString = "";
                            if (!(context.getSource().getSender() instanceof Player player)) {
                                return Command.SINGLE_SUCCESS;
                            }
                            McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);


                            RaspiPlayer raspiPlayer = Raspi.players().get(player);
                            //Testen ob wir ein HEX HABEN

                            switch (colors){
                                case HEX -> colorString = String.format(colors.getValue(), context.getInput().replace("chatcolor ", ""));
                                case RANDOM -> raspiPlayer.nameController().resetRandom();
                                default -> colorString = colors.getValue();
                            }


                            //Speichern
                            raspiPlayer.nameController().setPlayerColor(colorString);
                            //User senden
                            raspiPlayer.sendMessage(String.format("<gray>Deine Neue Chat Farbe ist jetzt: %s▆▇ %s %s▇▆", raspiPlayer.getColor(), raspiPlayer.getColorName(), raspiPlayer.getColor()), true);
                            return Command.SINGLE_SUCCESS;
                        })).build();
    }
}
