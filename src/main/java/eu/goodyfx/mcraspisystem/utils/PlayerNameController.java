package eu.goodyfx.mcraspisystem.utils;


import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.managers.PrefixManager;
import eu.goodyfx.mcraspisystem.managers.UserManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public class PlayerNameController {

    private final Map<UUID, String> colorMap = new HashMap<>();
    private final Map<UUID, String> randomContainer = new HashMap<>();
    private Scoreboard board;
    private final UserManager userManager;
    private final PrefixManager prefixManager;

    private final SecureRandom random = new SecureRandom();


    public PlayerNameController(McRaspiSystem utilities) {
        this.userManager = utilities.getUserManager();
        this.prefixManager = utilities.getPrefixManager();
    }

    public void setPlayerColor(String colorString, Player player) {

        if (colorString.equalsIgnoreCase("<LILA_BLASS_BLUE>")) {
            colorString = OldColors.LILA_BLASS_BLUE.getMinniString();
        }
        if (colorString.equalsIgnoreCase("<MINE_COIN_GOLD>")) {
            colorString = OldColors.MINE_COIN_GOLD.getMinniString();
        }
        userManager.setPersistantValue(player, PlayerValues.COLOR, colorString);
    }


    public String getColorString(Player player) {
        if (!playerHasColor(player)) {
            //set player random color if not set by player
            setPlayerColor("<random>", player);
        }

        String colorString = userManager.getPersistantValue(player, PlayerValues.COLOR, String.class);
        assert colorString != null; //Wir setzen den ja default auf Random

        if (colorString.equalsIgnoreCase("<random>")) {
            //set random color by random Hex generated Code

            int randNumber = random.nextInt(0xffffff + 1);
            if (!randomContainer.containsKey(player.getUniqueId())) {
                String colorGen = String.format("#%06x", randNumber);
                colorGen = "<" + colorGen + ">"; //make color String
                randomContainer.put(player.getUniqueId(), colorGen);
            }
            return randomContainer.get(player.getUniqueId());
        }

        return colorString;

    }

    public void setPlayerList(Player player) {
        if (userManager.hasPersistantValue(player, PlayerValues.AFK)) {
            player.playerListName(MiniMessage.miniMessage().deserialize(getNameDisplay(player) + " <gray><italic><underlined>AFK"));
        } else {
            player.playerListName(MiniMessage.miniMessage().deserialize(getNameDisplay(player)));
        }

    }

    public String getNameDisplay(Player player) {
        if (!prefixManager.get(player).isEmpty()) {
            return String.format("%1$s[%2$s%1$s] <gray>: <reset>%1$s%3$s<reset>", getColorString(player), prefixManager.get(player), player.getName());
        } else {
            return getColorString(player) + player.getName() + "<reset>";
        }
    }

    public String getName(Player player) {
        return getColorString(player) + player.getName() + "<reset>";
    }

    public String getNameDisplay(Player player, String optString) {
        if (!prefixManager.get(player).isEmpty()) {
            return String.format("%1$s[%2$s%1$s] <gray>: <reset>%1$s%3$s<reset>", getColorString(player), prefixManager.get(player), player.getName());
        } else {
            return optString + getColorString(player) + player.getName() + "<reset>";
        }
    }

    private boolean playerHasColor(Player player) {
        return userManager.hasPersistantValue(player, PlayerValues.COLOR);
    }

    public void resetRandom(Player player) {
        randomContainer.remove(player.getUniqueId());
    }

    public Map<UUID, String> getColorMap() {
        return colorMap;
    }

}
