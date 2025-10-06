package eu.goodyfx.system.raspievents.events;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.raspievents.craftings.CanabolaCraftging;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CraftingEventListeners implements Listener {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    public CraftingEventListeners() {
        plugin.setListeners(this);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        RaspiPlayer player = Raspi.players().get(event.getPlayer());
        ItemStack stack = event.getItem();
        if (stack == null) {
            return;
        }
        if (compareStack(stack)) {
            Location location = player.getLocation();
            location.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, player.getLocation().add(0, 1, 0), 150, 0, 0.5, 0, 0.05, null, true);
            player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, CanabolaCraftging.duration, 1, false, false, false));
            player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, CanabolaCraftging.duration, 1, false, false, false));
            player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, CanabolaCraftging.duration, 1, false, false, false));

            stack.setAmount(stack.getAmount()-1);
            event.setCancelled(true);
        }
    }

    private boolean compareStack(ItemStack stack) {
        ItemStack toCompare = CanabolaCraftging.buildRecipe().getResult();
        return stack.getType().equals(toCompare.getType()) && stack.hasItemMeta() && stack.getItemMeta().hasCustomModelData() && stack.getItemMeta().getCustomModelData() == 1;
    }

}
