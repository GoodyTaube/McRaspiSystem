package eu.goodyfx.system.core.utils;

public enum PlayerInfosValues {

    GROUPS("Spieler Raspi-Gruppen"),
    REGISTRATION("Freischaltung"),
    FIRST_JOIN("Erste Dokumentation"),
    LAST_NAMES("AKA"),
    TIME_PLAYED("Bisher Gespielt"),
    LAST_DEATH("Zuletzt Gestorben"),
    LAST_SEEN("Zuletzt Gesehen"),
    BACK_AFTER("Zurückgekehrt nach"),
    MUTED("<red>Der Spieler ist Stummgeschaltet für"),
    BANNED("<red>Der Spieler ist gesperrt für"),
    DENIED("<red>Der Spieler wurde bereits abgelehnt!"),
    PLAYER_XP("Aktuelle Spieler XP-Level");
    private final String label;

    PlayerInfosValues(String label) {
        this.label = label;
    }

    public String getLabel() {
        return String.format("<gray>%s: <aqua>", this.label);
    }

}
