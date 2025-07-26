package eu.goodyfx.system.core.tasks;

import eu.goodyfx.system.McRaspiSystem;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.logging.Level;

public class WeeklyTimer extends BukkitRunnable {

    private final McRaspiSystem plugin;

    public WeeklyTimer(McRaspiSystem system) {
        this.plugin = system;
        this.runTaskTimer(system, calcDelay(), 7 * 24 * 60 * 60 * 20L);
    }


    private static long calcDelay() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Berlin"));
        ZonedDateTime nextSunday = now.with(DayOfWeek.SUNDAY).withHour(0).withMinute(0).withSecond(0).withNano(0);

        if (now.compareTo(nextSunday) >= 0) {
            nextSunday = nextSunday.plusWeeks(1);
        }

        return Duration.between(now, nextSunday).toMillis();
    }

    @Override
    public void run() {
        delDB();
    }


    public void delDB() {
        File file = new File(plugin.getDataFolder(), "timesDB.yml");
        if (file.exists()) {
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Die Datei konnte nicht gelöscht werden", e);
            }
            plugin.getLogger().info("Reset der TimesDB veranlasst.");
        }
    }
}
