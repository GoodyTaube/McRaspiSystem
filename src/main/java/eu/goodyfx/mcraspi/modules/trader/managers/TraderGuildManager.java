package eu.goodyfx.mcraspi.modules.trader.managers;

import eu.goodyfx.mcraspi.core.api.Raspi;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class TraderGuildManager {

    private Integer getIdentifier(ItemStack stack) {
        PersistentDataContainer container = stack.getItemMeta().getPersistentDataContainer();
        if (!container.has(Raspi.pluginKeys().ITEM_KEY, PersistentDataType.INTEGER)) {
            return null;
        }
        return container.get(Raspi.pluginKeys().ITEM_KEY, PersistentDataType.INTEGER);
    }

    public boolean isIdentifier(ItemStack stack, int id) {
        Integer identifier = getIdentifier(stack);
        if (identifier == null) {
            return false;
        }
        return identifier.equals(id);
    }


}
