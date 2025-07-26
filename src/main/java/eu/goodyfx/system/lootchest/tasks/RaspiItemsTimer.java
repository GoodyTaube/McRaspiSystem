package eu.goodyfx.system.lootchest.tasks;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.lootchest.events.LootConsumeEvents;
import eu.goodyfx.system.lootchest.utils.Powers;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.UUID;

public class RaspiItemsTimer extends BukkitRunnable {

    private final McRaspiSystem plugin;

    public RaspiItemsTimer(McRaspiSystem plugin) {
        this.plugin = plugin;
    }

    protected final long minutes = Powers.FLIGHT.getTime() * 1000;
    private final SimpleDateFormat format = new SimpleDateFormat("HH:mm");

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        for (UUID uuid : LootConsumeEvents.getTimeStampMap().keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                return;
            }

            long diff = (LootConsumeEvents.getTimeStampMap().get(uuid) + minutes) - now;

            player.sendActionBar(MiniMessage.miniMessage().deserialize(getMessage(diff)));

            if (now >= (LootConsumeEvents.getTimeStampMap().get(uuid) + minutes)) {

                if (player.isFlying()) {
                    Location location = player.getLocation().clone();
                    World world = player.getWorld();
                    while (location.getBlock().getType().equals(Material.AIR)) {
                        location = location.subtract(0, 1, 0); // Teste nach festen boden unter den füßen
                    }
                    Location finalLocation = location;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.teleport(finalLocation);
                        }
                    }.runTask(plugin);
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    player.sendActionBar(MiniMessage.miniMessage().deserialize("<green><b>Flugzeit Beendet."));
                }

                //Remove logic here
                LootConsumeEvents.remove(uuid);
            }
        }
    }

    private String getMessage(long time) {
        if (time / 1000 < 60) {
            time = time / 1000;
            return "<gray>Flugzeit <aqua>" + String.valueOf(time) + " <gray>Sekunde(n)";
        }
        if (time / 60 / 1000 < 60) {
            time = time / 60 / 1000;
            return "<gray>Flugzeit <aqua>" + String.valueOf(time) + " <gray>Minuten(n)";
        }
        return "";
    }
}
