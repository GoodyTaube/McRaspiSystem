package eu.goodyfx.mcraspisystem.utils;

public enum Powers {

    HASTE(20 * 60 * 30, "haste", 1),
    NIGHT_VISION(20 * 60 * 30, "night_vision", 2),
    FLIGHT(30, "fly", 3);


    private long time;
    private int id;

    private final String label;

    Powers(long time, String powerLabel, int id) {
        this.time = time;
        this.id = id;
        this.label = powerLabel;
    }

    public int getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long minutes) {
        this.time = minutes;
    }

}
