package eu.goodyfx.mcraspi.modules.loot.staticlootchest.events;

import eu.goodyfx.mcraspi.McRaspiSystem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class LootConsumeEvents implements Listener {


    private final McRaspiSystem plugin;

    public LootConsumeEvents(McRaspiSystem plugin) {
        this.plugin = plugin;
        plugin.setListeners(this);
    }

    private final List<Material> checkList = new ArrayList<>();
    private static Map<UUID, Long> timeStampMap = new HashMap<>();


    @EventHandler
    public void onConsume(PlayerItemConsumeEvent consumeEvent) {
        Player player = consumeEvent.getPlayer(); //Lies Mich Bitte
        ItemStack stack = player.getInventory().getItemInMainHand();
        String loreMustHave = plugin.getConfig().getString("item.lore");

        levelFleisch(consumeEvent, stack);
    }

    private void levelFleisch(PlayerItemConsumeEvent consumeEvent, ItemStack stack) {
        Player player = consumeEvent.getPlayer();
        if (stack.getType().equals(Material.COOKED_BEEF) && (stack.hasItemMeta() && stack.getItemMeta().hasCustomModelData() && stack.getItemMeta().getCustomModelData() == 1)) {
            ItemMeta meta = stack.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (container.has(new NamespacedKey(plugin, "level"), PersistentDataType.STRING)) {
                float level = Integer.valueOf(container.get(new NamespacedKey(plugin, "level"), PersistentDataType.STRING));
                level = (level + player.getExp());
                player.setExp(level);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            } else {
                consumeEvent.setCancelled(true);
            }
        }
    }

}
