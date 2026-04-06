package eu.goodyfx.system.pvp.events;

import eu.goodyfx.system.pvp.PvPSubSystem;
import eu.goodyfx.system.pvp.utils.PvPManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.util.BlockVector;

import java.util.UUID;

public class PvPEvents implements Listener {

    private final PvPSubSystem system;
    private final PvPManager pvPManager;

    public PvPEvents(PvPSubSystem system) {
        this.system = system;
        this.pvPManager = system.getPvPManager();
        system.getPlugin().setListeners(this);
    }

    @EventHandler
    public void onBlockPlace(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        if (event.getBucket() != Material.LAVA_BUCKET) {
            return;
        }
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());

        Bukkit.getScheduler().runTask(system.getPlugin(), () -> {
            if (block.getType() == Material.LAVA) {
                BlockVector key = block.getLocation().toVector().toBlockVector();
                pvPManager.getLavaPlaced().put(key, player.getUniqueId());
            }
        });
    }

    @EventHandler
    public void onFade(BlockFadeEvent event) {
        pvPManager.getLavaPlaced().remove(
                event.getBlock().getLocation().toVector().toBlockVector()
        );
    }

    @EventHandler
    public void onIgnite(BlockIgniteEvent event) {

        if (event.getCause() != BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL)
            return;

        Player player = event.getPlayer();


        Block block = event.getBlock();

        BlockVector key = block.getLocation().toVector().toBlockVector();
        pvPManager.getFirePlaced().put(key, player.getUniqueId());
    }


    @EventHandler
    public void onBlockPlace(BlockFromToEvent event) {

        if (!(event.getBlock().getBlockData() instanceof Levelled)) {
            return;
        }
        BlockVector from = event.getBlock().getLocation().toVector().toBlockVector();
        BlockVector to = event.getToBlock().getLocation().toVector().toBlockVector();
        UUID owner = pvPManager.getLavaPlaced().get(from);
        if (owner != null) {
            pvPManager.getLavaPlaced().put(to, owner);
        }
    }


    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        normalCheck(event, player);

        if (event.getCause() != EntityDamageEvent.DamageCause.LAVA
                && event.getCause() != EntityDamageEvent.DamageCause.FIRE
                && event.getCause() != EntityDamageEvent.DamageCause.FIRE_TICK) return;


        Block block = player.getLocation().clone().subtract(0, 0.1, 0).getBlock();
        if (block.getType() != Material.LAVA) {
            return;
        }

        BlockVector key = block.getLocation().toVector().toBlockVector();
        UUID att = pvPManager.getLavaPlaced().get(key);

        if (att == null) {
            return;
        }
        Player attack = Bukkit.getPlayer(att);
        if (attack == null) {
            return;
        }
        if (!pvPManager.canPvP(attack, player)) {
            event.setCancelled(true);
            pvPManager.sendCanceledDamageWarning(player, attack);
        }

    }


    private void normalCheck(EntityDamageEvent event, Player player) {
        if (!(event.getDamageSource().getCausingEntity() instanceof Player damager)) {
            return;
        }

        if (!(pvPManager.canPvP(damager, player))) {
            event.setCancelled(true);
            pvPManager.sendCanceledDamageWarning(player, damager);
        }

    }


}
