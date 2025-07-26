package eu.goodyfx.system.lootchest.utils;

import lombok.Getter;

@Getter
public enum Powers {

    HASTE(20 * 60 * 30L, "haste", 1),
    NIGHT_VISION(20 * 60 * 30L, "night_vision", 2),
    FLIGHT(30L, "fly", 3);


    private final long time;
    private final int id;

    private final String label;

    Powers(long time, String powerLabel, int id) {
        this.time = time;
        this.id = id;
        this.label = powerLabel;
    }
}
