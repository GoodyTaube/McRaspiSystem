package eu.goodyfx.system.core.api;

import eu.goodyfx.system.McRaspiSystem;
import lombok.Getter;
import org.bukkit.NamespacedKey;
@Getter
public class PluginKeys {

    public NamespacedKey ITEM_KEY;

    public PluginKeys(McRaspiSystem plugin) {
        ITEM_KEY = new NamespacedKey(plugin, "identifier");
    }

}
