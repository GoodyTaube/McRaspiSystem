package eu.goodyfx.system.core.events;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.api.Raspi;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;

public class RaspiWorldEvents implements Listener {

    public RaspiWorldEvents(McRaspiSystem plugin) {
        plugin.setListeners(this);
    }

    @EventHandler
    public void onFire(BlockBurnEvent burnEvent) {
        burnEvent.setCancelled(true);
    }

    @EventHandler
    public void onItemFrameDestroy(EntityDamageByEntityEvent damageByEntityEvent) {
        EntityType type = damageByEntityEvent.getEntityType();
        List<EntityType> blocked = new ArrayList<>();
        blocked.add(EntityType.ITEM_FRAME);
        blocked.add(EntityType.GLOW_ITEM_FRAME);
        blocked.add(EntityType.PAINTING);
        if ((blocked.contains(type)) && !(damageByEntityEvent.getDamager() instanceof Player)) {
            damageByEntityEvent.setCancelled(true);
            Raspi.debugger().debug(String.format("Blocked Destroying %s by %s %s", type.name(), damageByEntityEvent.getDamager().getType().name(), Raspi.debugger().formatLocation(damageByEntityEvent.getEntity().getLocation())));
        }

    }

    @EventHandler
    public void onFireSpread(BlockSpreadEvent burnEvent) {
        if (burnEvent.getSource().getType().equals(Material.FIRE)) {
            burnEvent.setCancelled(true);
            Location location = burnEvent.getBlock().getLocation();
            Raspi.debugger().debug(String.format("Prevent Fire from Spreading %s", Raspi.debugger().formatLocation(location)));
        }
    }

    @EventHandler
    public void onBlockDamage(EntityExplodeEvent explodeEvent) {
        EntityType entityType = explodeEvent.getEntityType();
        if (!(entityType.equals(EntityType.TNT) || entityType.equals(EntityType.END_CRYSTAL))) {
            explodeEvent.blockList().clear();
            Raspi.debugger().debug(String.format("Cleared Blocklist for %s", explodeEvent.getEntityType().name()));
        }

    }

    @EventHandler
    public void onMobGrief(EntityChangeBlockEvent changeBlockEvent) {
        List<EntityType> blocked = new ArrayList<>();
        blocked.add(EntityType.ENDERMAN);
        blocked.add(EntityType.ENDER_DRAGON);
        blocked.add(EntityType.WITHER);
        if (blocked.contains(changeBlockEvent.getEntityType())) {
            Location location = changeBlockEvent.getBlock().getLocation();
            changeBlockEvent.setCancelled(true);
            Raspi.debugger().debug(String.format("Blocked block Damage %s caused by %s", Raspi.debugger().formatLocation(location), changeBlockEvent.getEntityType().name()));
        }
    }

}
