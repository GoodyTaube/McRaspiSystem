package eu.goodyfx.system.core.utils;


import eu.goodyfx.system.McRaspiSystem;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
@Getter
public class PlayerNameController {

    private final Map<UUID, String> randomContainer = new HashMap<>();
    private final SecureRandom random = new SecureRandom();
    private final RaspiPlayer player;

    public PlayerNameController(RaspiPlayer player) {
        this.player = player;

    }

    public void setPlayerColor(String colorString) {
        if (colorString.equalsIgnoreCase("<LILA_BLASS_BLUE>")) {
            colorString = OldColors.LILA_BLASS_BLUE.getMinniString();
        }
        if (colorString.equalsIgnoreCase("<MINE_COIN_GOLD>")) {
            colorString = OldColors.MINE_COIN_GOLD.getMinniString();
        }
        player.getUser().setColor(colorString);
        //userManager.set(player, "playerColor", colorString);
        setPlayerList();
    }


    public String getColorString() {
        if (!player.isInitialized()) {
            return "<random>";
        }
        //String colorString = userManager.get("playerColor", player, String.class);
        String colorString = player.getColor();
        if(colorString == null){
            colorString = "<random>";
            setPlayerColor(colorString);
        }

        if (colorString.equalsIgnoreCase("<random>")) {
            //set random color by random Hex generated Code

            int randNumber = random.nextInt(0xffffff + 1);
            if (!randomContainer.containsKey(player.getUUID())) {
                String colorGen = String.format("#%06x", randNumber);
                colorGen = "<" + colorGen + ">"; //make color String
                randomContainer.put(player.getUUID(), colorGen);
            }
            return randomContainer.get(player.getUUID());
        }
        return colorString;

    }

    public void setPlayerList() {
        if (player.isInitialized() && player.getUserSettings().isAfk()) {
            player.getPlayer().playerListName(MiniMessage.miniMessage().deserialize(String.format("%s <gray><italic><underlined>AFK", getColorDisplayName())));
        } else {
            player.getPlayer().playerListName(MiniMessage.miniMessage().deserialize(getColorDisplayName()));

        }
    }


    public String getColorName() {
        return String.format("%s%s<reset>", getColorString(), player.getPlayer().getName());
    }

    public String getColorDisplayName() {

        if (player.isInitialized() && player.getPrefix() != null) {
            return String.format("%1$s[%2$s%1$s] <gray>: <reset>%1$s%3$s<reset>", getColorString(), player.getPrefix(), player.getPlayer().getName());
        }
        return String.format("%s %s <reset>", getColorString(), player.getPlayer().getName());
    }

    public String getColorDisplayName(String optMessage) {
        if (player.isInitialized() && player.getPrefix() != null) {
            return String.format("%1$s[%2$s%1$s] <gray>: <reset>%1$s%3$s<reset>", getColorString(), player.getPrefix(), player.getPlayer().getName());
        } else {
            return String.format("%s%s%s<reset>", optMessage, getColorString(), player.getPlayer().getName());
        }
    }

    public void resetRandom() {
        JavaPlugin.getPlugin(McRaspiSystem.class).getDebugger().info("NameController::RESET RANDOM");
        randomContainer.remove(player.getUUID());
    }

}
