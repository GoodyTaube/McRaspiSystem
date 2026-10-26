package eu.goodyfx.mcraspi.core.utils;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

@Getter
public class Prefix {

    private final String prefixText;
    private final Component prefix;

    public Prefix(String formattedText) {
        this.prefixText = formattedText;
        this.prefix = MiniMessage.miniMessage().deserialize(formattedText);
    }


}
