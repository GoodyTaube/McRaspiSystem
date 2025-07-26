package eu.goodyfx.system.core.utils;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface RaspiTimes {


    String getLabel();

    Long getTime();

    enum MilliSeconds implements RaspiTimes {

        SECOND("Sekunde(n)", 1000L),
        MINUTE("Minute(n)", 1000L * 60),
        HOUR("Stunde(n)", 1000L * 60 * 60),
        DAY("Tag(e)", 1000L * 60 * 60 * 24),
        WEEK("Woche(n)", 1000L * 60 * 60 * 24 * 7),
        MONTH("Monat(e)", 1000L * 60 * 60 * 24 * 7 * 4),
        YEAR("Jahr(e)", 1000L * 60 * 60 * 24 * 7 * 4 * 12);

        private final String label;
        private final Long time;

        MilliSeconds(String label, Long time) {
            this.label = label;
            this.time = time;
        }

        @Override
        public String getLabel() {
            return this.label;
        }

        @Override
        public Long getTime() {
            return this.time;
        }

        public static Integer getTimeValue(long val, MilliSeconds unit) {
            val = System.currentTimeMillis() - val;
            return Math.toIntExact((val / unit.getTime()));
        }

        public static String getTimeUnit(long val) {

            if (val / WEEK.getTime() >= 1) {
                return val / DAY.getTime() + " " + DAY.getLabel();
            }
            if (val / HOUR.getTime() >= 1) {
                return val / HOUR.getTime() + " " + HOUR.getLabel();
            }
            if (val / MINUTE.getTime() >= 1) {
                return val / MINUTE.getTime() + " " + MINUTE.getLabel();
            }
            return val / SECOND.getTime() + " " + SECOND.getLabel();
        }

        public String getTimeDisplay(@NotNull RaspiTimes.MilliSeconds time, int val) {
            return (time.getTime() * val) + " " + time.getLabel();
        }

    }

    enum Ticks implements RaspiTimes {
        SECOND("Sekunde(n)", 20L),
        MINUTE("Minute(n)", 20L * 60),
        HOUR("Stunde(n)", 20L * 60 * 60),
        DAY("Tag(e)", 20L * 60 * 60 * 24),
        WEEK("Woche(n)", 20L * 60 * 60 * 24 * 7),
        MONTH("Monat(e)", 20L * 60 * 60 * 24 * 7 * 4),
        YEAR("Jahr(e)", 20L * 60 * 60 * 24 * 7 * 4 * 12);

        private final String label;
        private final Long time;

        Ticks(String label, Long time) {
            this.label = label;
            this.time = time;
        }

        @Override
        public String getLabel() {
            return this.label;
        }

        @Override
        public Long getTime() {
            return this.time;
        }

        public static Integer getTimeValue(long val, Ticks unit) {
            return Math.toIntExact((val / unit.getTime()));
        }

        public String getTimeDisplay(@NotNull RaspiTimes.Ticks time, int val) {
            return (time.getTime() * val) + " " + time.getLabel();
        }

        public static String getTimeUnit(long val) {

            if (val / WEEK.getTime() >= 1) {
                return val / DAY.getTime() + " " + DAY.getLabel();
            }
            if (val / HOUR.getTime() >= 1) {
                return val / HOUR.getTime() + " " + HOUR.getLabel();
            }
            if (val / MINUTE.getTime() >= 1) {
                return val / MINUTE.getTime() + " " + MINUTE.getLabel();
            }
            return val / SECOND.getTime() + " " + SECOND.getLabel();
        }
    }


}
