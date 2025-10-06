package eu.goodyfx.system.core.utils;

import java.time.Duration;
import java.time.Instant;

public class RaspiTimeUtils {

    /**
     * Get the Duration value between two TimeStamps
     *
     * @param timeStamp the "to" calc timestamp
     * @return Duration between timeStamp and today
     */
    public static Duration getBetween(long timeStamp) {
        Instant instant = Instant.ofEpochMilli(timeStamp);
        return Duration.between(instant, Instant.now());
    }

    public static String formatDuration(Duration duration) {
        long days = duration.toDays();
        duration = duration.minusDays(days);

        long hours = duration.toHours();
        duration = duration.minusHours(hours);

        long minutes = duration.toMinutes();
        duration = duration.minusHours(minutes);

        long seconds = duration.getSeconds();
        return (days > 0 ? days + " Tag(e) " : "")
                + (hours > 0 ? hours + " Stunde(n) " : "")
                + (minutes > 0 ? minutes + " Minute(n)" : "")
                + (seconds > 0 ? seconds + " Sekunde(n)" : "");

    }

}
