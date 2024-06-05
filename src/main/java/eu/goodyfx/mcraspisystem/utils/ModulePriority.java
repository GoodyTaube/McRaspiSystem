package eu.goodyfx.mcraspisystem.utils;

public enum ModulePriority {

    HIGH(2),
    MONITOR(1),
    LOW(0);

    private final int val;

    ModulePriority(int val) {
        this.val = val;
    }

    public int getVal() {
        return this.val;
    }

}
