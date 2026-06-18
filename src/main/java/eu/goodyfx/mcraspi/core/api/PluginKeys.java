package eu.goodyfx.mcraspi.core.api;

import eu.goodyfx.mcraspi.McRaspiSystem;
import lombok.Getter;
import org.bukkit.NamespacedKey;
@Getter
public class PluginKeys {

    public NamespacedKey ITEM_KEY;

    public PluginKeys(McRaspiSystem plugin) {
        ITEM_KEY = new NamespacedKey(plugin, "identifier");
    }

}
