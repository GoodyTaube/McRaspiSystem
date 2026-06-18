package eu.goodyfx.mcraspi.core.utils;


import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;

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

    public PlayerNameController(RaspiPlayer account) {
        this.player = account;
    }

    public void setPlayerColor(String colorString) {
        if (colorString.equalsIgnoreCase("<LILA_BLASS_BLUE>")) {
            colorString = OldColors.LILA_BLASS_BLUE.getMinniString();
        }
        if (colorString.equalsIgnoreCase("<MINE_COIN_GOLD>")) {
            colorString = OldColors.MINE_COIN_GOLD.getMinniString();
        }
        player.userData().setColor(colorString);
        //userManager.set(player, "playerColor", colorString);
        setPlayerList();
    }

    public void updateRandom() {
        UUID uuid = player.getUUID();
        String randomHex = String.format("#%06x", random.nextInt(0xFFFFFF + 1));
        String colorTag = String.format("<%s>", randomHex);
        randomContainer.put(uuid, colorTag);
        setPlayerList();
    }


    public String getColorString() {
        //String colorString = userManager.get("playerColor", player, String.class);
        String colorString = player.userData().getColor();
        if (colorString == null) {
            colorString = "<random>";
            setPlayerColor(colorString);
        }

        if (colorString.equalsIgnoreCase("<random>")) {
            //set random color by random Hex generated Code
            if (!randomContainer.containsKey(player.getUUID())) {
                updateRandom();
            }
            return randomContainer.get(player.getUUID());
        }
        return colorString;
    }

    public void setPlayerList() {
        if (player.settings() != null && player.isAfk()) {
            player.getPlayer().playerListName(MiniMessage.miniMessage().deserialize(String.format("%s <gray><italic><underlined>AFK", getColorDisplayName())));
        } else {
            player.getPlayer().playerListName(MiniMessage.miniMessage().deserialize(getColorDisplayName()));
        }
    }


    public String getColorName() {
        return String.format("%s%s<reset>", getColorString(), player.getPlayer().getName());
    }

    public String getColorDisplayName() {

        if (player.getPrefix() != null) {
            return String.format("%1$s[%2$s%1$s] <gray>: <reset>%1$s%3$s<reset>", getColorString(), player.getPrefix(), player.getPlayer().getName());
        }
        return String.format("%s%s<reset>", getColorString(), player.getPlayer().getName());
    }

    public String getColorDisplayName(String optMessage) {
        if (player.getPrefix() != null) {
            return String.format("%1$s[%2$s%1$s] <gray>: <reset>%1$s%3$s<reset>", getColorString(), player.getPrefix(), player.getPlayer().getName());
        } else {
            return String.format("%s%s%s<reset>", optMessage, getColorString(), player.getPlayer().getName());
        }
    }

}
