package eu.goodyfx.system.core.events;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.commands.BackCommandContainer;
import eu.goodyfx.system.core.commands.SitCommandContainer;
import eu.goodyfx.system.core.utils.Raspi;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public record TeleportListeners(McRaspiSystem plugin) implements Listener {


    public TeleportListeners {
        plugin.setListeners(this);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityTeleport(EntityTeleportEvent teleportEvent) {
        if (teleportEvent.getEntity() instanceof Animals animals) {
            animals.getNearbyEntities(5, 5, 5).forEach(enemy -> {
                if (enemy instanceof Player) {
                    if (animals.getPassengers().contains(enemy)) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                animals.addPassenger(enemy);
                            }
                        }.runTaskLaterAsynchronously(plugin, 7L);

                    }
                }
            });
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent teleportEvent) {
        Player player = teleportEvent.getPlayer();
        Location to = teleportEvent.getTo();
        BackCommandContainer.getLocationsCache().put(player.getUniqueId(), teleportEvent.getFrom());
        Raspi.debugger().info(String.format("[BackCommand] saved %s location. CAUSE::TELEPORT", player.getName()));
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
                            }
                        }.runTaskLater(plugin, 2L);

                    }
                }
            }
            // Teleport Player and Entity Player sits on
            if (!entity.getPassengers().isEmpty()) {
                for (Entity passenger : entity.getPassengers()) {
                    if (passenger.equals(player)) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                entity.teleport(Objects.requireNonNull(to), PlayerTeleportEvent.TeleportCause.PLUGIN);
                                entity.addPassenger(player);
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
    }


}
