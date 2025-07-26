package eu.goodyfx.mcraspisystem.commands.container;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.arguments.ChatColorCommandArgument;
import eu.goodyfx.mcraspisystem.utils.PlayerNameController;
import eu.goodyfx.mcraspisystem.utils.RaspiFormatting;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
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


                            RaspiPlayer raspiPlayer = plugin.getRaspiPlayer(player);
                            PlayerNameController playerNameController = plugin.getModule().getPlayerNameController();
                            //Testen ob wir ein HEX HABEN

                            switch (colors){
                                case HEX -> colorString = String.format(colors.getValue(), context.getInput().replace("chatcolor ", ""));
                                case RANDOM -> plugin.getModule().getPlayerNameController().resetRandom(player);
                                default -> colorString = colors.getValue();
                            }


                            //Speichern
                            playerNameController.setPlayerColor(colorString, player);
                            //User senden
                            raspiPlayer.sendMessage(String.format("<gray>Deine Neue Chat Farbe ist jetzt: %s▆▇ %s %s▇▆", playerNameController.getColorString(player), playerNameController.getName(player), playerNameController.getColorString(player)), true);
                            return Command.SINGLE_SUCCESS;
                        })).build();
    }
}
