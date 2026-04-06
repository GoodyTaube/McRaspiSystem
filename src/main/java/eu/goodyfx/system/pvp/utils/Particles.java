package eu.goodyfx.system.pvp.utils;

import eu.goodyfx.system.pvp.PvPSubSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

public class Particles extends BukkitRunnable {

    private final PvPSubSystem system;

    public Particles(PvPSubSystem system) {
        this.system = system;
        start();
    }

    private void start() {
        this.runTaskTimerAsynchronously(system.getPlugin(), 0, 20);
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (system.getPvPManager().getActivePvPPlayersCache().contains(player.getUniqueId())) {
                new BukkitRunnable() {
                    double phi = 0;

                    public void run() {
                        phi += Math.PI / 10;
                        Location loc = player.getLocation();
                        loc.add(Math.cos(phi), 0, Math.sin(phi));
                        player.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, loc, 1, 0, 0, 0, 0, new Particle.DustTransition(
                                org.bukkit.Color.RED,
                                org.bukkit.Color.BLACK, // Ziel-Farbe
                                1f
                        ));
                        if (phi > 2 * Math.PI) {
                            this.cancel();
                        }
                    }
                }.runTaskTimerAsynchronously(system.getPlugin(), 0, 1);
            }
        });
    }


}
