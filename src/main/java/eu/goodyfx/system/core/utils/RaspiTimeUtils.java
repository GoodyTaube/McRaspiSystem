package eu.goodyfx.system.core.utils;

import java.time.Duration;
import java.time.Instant;

public class RaspiTimeUtils {

    public static Duration getBetween(long timeStamp) {
        Instant instant = Instant.ofEpochMilli(timeStamp);
        return Duration.between(instant, Instant.now());
    }

    public static String formatDuration(Duration duration) {
        long days = duration.toDays();

        if (days > 0) {
            return days + " Tag(e)";
        }

        long hours = duration.toHours();

        if (hours > 0) {
            return hours + " Stunde(n)";
        }

        long minutes = duration.toMinutes();

        if (minutes > 0) {
            return minutes + " Minute(n)";
        }

        long seconds = duration.getSeconds();

        return seconds + " Sekunde(n)";
    }

}
