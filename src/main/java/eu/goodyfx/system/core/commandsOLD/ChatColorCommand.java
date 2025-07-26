package eu.goodyfx.system.core.commandsOLD;


import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.OldColors;
import eu.goodyfx.system.core.utils.PlayerNameController;
import eu.goodyfx.system.core.utils.RaspiMessages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ChatColorCommand implements CommandExecutor, TabCompleter {

    private final PlayerNameController playerNameController;
    private final RaspiMessages data;

    private static final String RANDOM = "random";

    public ChatColorCommand(McRaspiSystem utilities) {
        this.data = utilities.getModule().getRaspiMessages();
        this.playerNameController = utilities.getModule().getPlayerNameController();
        utilities.setCommand("chatColor", this, this);
        setColors();
    }


    //List of Old Colors
    private List<String> colors;

    private void setColors() {
        colors = new ArrayList<>();
        for (OldColors per : OldColors.values()) {
            colors.add(per.name().toLowerCase(Locale.ROOT));
        }
    }


    @Override //Tab Complete Logic
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("chatColor") || command.getName().equals("cc")) { //Check if Command equal for needs
            List<String> result = colors;
            List<String> finalList = new ArrayList<>();
            result.add("#");
            result.add(RANDOM);
            Collections.sort(result);
            if (args.length == 1) {
                for (String per : result) {
                    if (per.startsWith(args[0])) {
                        finalList.add(per);
                        finalList.add("#");
                        finalList.add(RANDOM);
                    }
                }
            }
            Collections.sort(finalList);
            return finalList;
        }
        return null;
    }

    @Override //Command Logic
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                player.sendRichMessage(data.getUsage("/chatcolor <farbe>"));
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("info")) {
                    player.sendRichMessage("<green>Dein Aktueller Hex code ist: " + playerNameController.getColorString(player).replace("<", "").replace(">", ""));
                    return true;
                }
                String codeRaw = args[0];
                boolean defaultColor = false;
                //Prüfe ob der eingegebene code in der Liste steht
                for (String colorName : colors) {
                    if (colorName.equalsIgnoreCase(args[0])) {
                        defaultColor = true;
                        break;
                    }
                }
                // Wenn defaultColor = false dann wird nach dem HEX code gecheckt.
                if (!defaultColor && (!args[0].startsWith("#") || args[0].length() != 7)) {
                    player.sendRichMessage(data.getPrefix() + "<red>Bitte verwende einen Gültigen code! (#000000) oder (Farbe)");
                    return true;
                }

                if (codeRaw.equalsIgnoreCase(RANDOM)) {
                    playerNameController.resetRandom(player);
                }

                codeRaw = "<" + codeRaw + ">";
                playerNameController.setPlayerColor(codeRaw, player);
                player.sendRichMessage("<gray>Deine Neue Chat Farbe ist jetzt: " + playerNameController.getColorString(player) + "▆▇ " + player.getName() + " ▇▆");
                playerNameController.setPlayerList(player);
            }
        }
        return false;
    }


}
