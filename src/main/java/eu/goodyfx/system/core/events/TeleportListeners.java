package eu.goodyfx.system.core.events;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.commands.BackCommandContainer;
import eu.goodyfx.system.core.commands.SitCommandContainer;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record TeleportListeners(McRaspiSystem plugin) implements Listener {


    public TeleportListeners {
        plugin.setListeners(this);
    }

    private static final Map<UUID, Entity> cache = new HashMap<>();


    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityTeleport(EntityTeleportEvent teleportEvent) {
        if (teleportEvent.getEntity() instanceof Animals animals) {
            animals.getNearbyEntities(5, 5, 5).forEach(enemy -> {
                if (enemy instanceof Player) {
                    if (animals.getPassengers().contains(enemy) && !animals.getType().equals(EntityType.HORSE)) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                animals.addPassenger(enemy);
                            }
                        }.runTaskLater(plugin, 7L);

                    }
                }
            });
        }
    }

    public double distance(Location location1, Location location2) {
        if (!location1.getWorld().getName().equalsIgnoreCase(location2.getWorld().getName())) {
            return 100.0D;
        }
        return location1.distanceSquared(location2);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent teleportEvent) {
        Player player = teleportEvent.getPlayer();
        Location to = teleportEvent.getTo();
        BackCommandContainer.getLocationsCache().put(player.getUniqueId(), teleportEvent.getFrom());
        Raspi.debugger().debug(String.format("[BackCommand] saved %s location. CAUSE::TELEPORT", player.getName()));


        double distance = distance(teleportEvent.getFrom(), to);
        if (distance > 25) {
            if (cache.containsKey(player.getUniqueId())) {
                Entity mount = cache.get(player.getUniqueId());

                if (mount != null) {

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Location target = teleportEvent.getTo().clone().add(0, 0.1, 0);
                            mount.teleport(teleportEvent.getTo(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                            mount.setVelocity(new Vector(0, 0, 0));
                            mount.addPassenger(player);

                        }
                    }.runTask(plugin);

                }
                cache.remove(player.getUniqueId());
            }
        }
        player.getNearbyEntities(8, 8, 8).forEach(entity -> {
            if (entity instanceof Animals animal) {
                if (animal.isLeashed()) {
                    if (animal.getLeashHolder().equals(player)) {
                        animal.setLeashHolder(null);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                animal.teleport(Objects.requireNonNull(to), PlayerTeleportEvent.TeleportCause.PLUGIN);
                                animal.setLeashHolder(player);
                                Raspi.debugger().debug("TELEPORT HORSE");
                            }
                        }.runTaskLater(plugin, 2L);

                    }
                }
            }

        });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDismount(EntityDismountEvent dismountEvent) {
        if (dismountEvent.getEntity().getType().equals(EntityType.PLAYER) && dismountEvent.getDismounted().getType().equals(EntityType.INTERACTION)) {
            Player player = (Player) dismountEvent.getEntity();
            SitCommandContainer.endSitting(player);
            dismountEvent.getDismounted().remove();
            player.teleport(player.getLocation().add(0, 1, 0));
        }

        if (dismountEvent.getEntity() instanceof Player player) {
            cache.remove(player.getUniqueId());
        }

    }

    @EventHandler
    public void onMount(EntityMountEvent mountEven) {
        if (mountEven.getEntity() instanceof Player player) {
            cache.put(player.getUniqueId(), mountEven.getMount());
        }
    }


}
