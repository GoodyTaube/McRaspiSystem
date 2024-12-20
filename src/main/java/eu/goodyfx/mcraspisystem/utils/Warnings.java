package eu.goodyfx.mcraspisystem.utils;

public enum Warnings {

    CRITICAL("<red>Kritische Warnung <white>|"),
    DEFAULT("<red>Warnung <white>|"),
    HIGH("<red>Hohe Warnung<white> |");

    private final String prefix;

    Warnings(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return this.prefix;
    }

}
