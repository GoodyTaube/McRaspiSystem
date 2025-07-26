package eu.goodyfx.system.core.utils;

public enum PlayerInfosValues {

    GROUPS("Spieler Gruppen"),

    REGISTRATION("Freischaltung"),
    FIRST_JOIN("Erste Dokumentation"),
    LAST_NAMES("AKA"),
    TIME_PLAYED("Bisher Gespielt (Spielzeit)"),
    TIME_PLAYED_WEEKLY("Diese Woche Gespielt (Realzeit)"),
    LAST_DEATH("Zuletzt Gestorben (Spielzeit)"),
    LAST_SEEN("Zuletzt Gesehen (Realzeit)"),
    BACK_AFTER("Zurückgekehrt nach (Realzeit)"),
    MUTED("<red>Der Spieler ist Stummgeschaltet für:"),

    BANNED("<red>Der Spieler ist gesperrt für"),

    DENYED("<red>Der Spieler wurde bereits abgelehnt!"),
    PLAYER_XP("Spieler Level");
    private final String label;

    PlayerInfosValues(String label) {
        this.label = label;
    }

    public String getLabel() {
        return String.format("<gray>%s:<aqua>", this.label);
    }

}
