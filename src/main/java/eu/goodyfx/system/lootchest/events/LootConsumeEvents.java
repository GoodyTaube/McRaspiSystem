package eu.goodyfx.system.lootchest.events;

import com.destroystokyo.paper.ParticleBuilder;
import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.lootchest.utils.LootItemManager;
import eu.goodyfx.system.lootchest.utils.LootItems;
import eu.goodyfx.system.lootchest.utils.Powers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

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
        if (stack.getType().equals(Material.GOLDEN_APPLE)) {

            LootItemManager manager = new LootItemManager(stack);


            if (manager.isItem(Powers.FLIGHT)) {

                timeStampMap.put(player.getUniqueId(), System.currentTimeMillis());
                player.setAllowFlight(true);
                player.setFlying(true);
                player.sendRichMessage("<green>Activation: " + Powers.FLIGHT.getLabel());
                return;
            }

            if (manager.isItem(Powers.NIGHT_VISION)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, (int) Powers.NIGHT_VISION.getTime(), 1, false, false));
                player.sendRichMessage("<green>Activation: " + Powers.NIGHT_VISION.getLabel());
                return;
            }
            if (manager.isItem(Powers.HASTE)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, (int) Powers.HASTE.getTime(), 9, false, false));
                player.sendRichMessage("<green>Activation: " + Powers.NIGHT_VISION.getLabel());
                return;
            }


            if (stack.hasItemMeta()) {
                ItemMeta meta = stack.getItemMeta();
                if (meta.hasLore()) {
                    List<Component> lore = meta.lore();
                    assert lore != null;
                    for (Component val : lore) {
                        String loreText = PlainTextComponentSerializer.plainText().serialize(val);
                        if (loreText.equalsIgnoreCase(loreMustHave)) {
                            action(player);
                            return;
                        }
                    }
                }
            }
        }

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


    /**
     * Animation Golden_APPLE
     *
     * @param player The player how sees animation
     */
    public void action(Player player) {
        player.stopSound(Sound.UI_TOAST_CHALLENGE_COMPLETE);
        Location location = player.getLocation();
        Vector vector = location.getDirection().multiply(4);

        Block blockHEAD = player.getTargetBlockExact(1);
        Block blockItem = player.getTargetBlockExact(6);

        if (blockHEAD == null && blockItem == null) {
            location.add(vector.getX(), 0, vector.getZ());
        } else {
            location.add(0, 2, 0);
        }


        ParticleBuilder portal = new ParticleBuilder(Particle.PORTAL);
        portal.location(location.clone().add(0, 1.1, 0));
        portal.count(10000);
        portal.extra(0.3d);

        ParticleBuilder fireWork = new ParticleBuilder(Particle.FIREWORK);
        fireWork.count(100);
        fireWork.extra(0.07d);
        fireWork.location(location.clone().add(0, 1.1, 0));

        player.getWorld().strikeLightningEffect(location.clone().add(0, 2, 0));

        portal.spawn();
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.RECORDS, 2f, 0.7f);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            fireWork.spawn();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getRandomCommand(location.clone().add(0, 1.1, 0)));
            }, 10);
        }, 23 * 2);


    }

    public String getRandomCommand(Location location) {
        plugin.reloadConfig();
        List<String> commands = plugin.getConfig().getStringList("loot.items");
        int random = new Random().nextInt(commands.size());
        String result = commands.get(random);
        plugin.getLogger().info(result);
        result = result.replace("%x%", String.valueOf(location.getX()));
        result = result.replace("%y%", String.valueOf(location.getY()));
        result = result.replace("%z%", String.valueOf(location.getZ()));


        return result;

    }

    /**
     * Get Current Power by item in Hand
     *
     * @param stack The Check Stack
     * @return Current Power Item.
     */
    public LootItems getPower(ItemStack stack) {
        LootItems item = null;
        for (LootItems per : LootItems.values()) {
            if (stack.getType().equals(per.getMaterial())) {
                item = per;
                break;
            }
        }
        return item;
    }

    public static Map<UUID, Long> getTimeStampMap() {
        return timeStampMap;
    }

    public static void remove(UUID uuid) {
        timeStampMap.remove(uuid);
    }


    public static int mathXP(int currentLevel) {
        int totalXP = 0;

        for (int i = 0; i < currentLevel; i++) {
            totalXP += getXpToNextLevel(i);
        }

        return totalXP;
    }

    public static int getXpToNextLevel(int level) {
        if (level >= 0 && level < 16) {
            return 2 * level + 7;
        } else if (level >= 16 && level < 31) {
            return 5 * level - 38;
        } else {
            return 9 * level - 158;
        }
    }

}
