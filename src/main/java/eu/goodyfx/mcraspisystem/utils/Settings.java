package eu.goodyfx.mcraspisystem.utils;

public enum Settings {

    AUTO_AFK("autoAFK", "AutoAFK"),
    ADVANCED_CHAT("optChat", "Optional Chat"),
    MESSAGES("messages", "Server-Nachrichten"),
    PVP("pvp", "PVP-Aktiviert");

    private final String label;
    private final String displayName;

    Settings(String label, String displayName) {
        this.label = label;
        this.displayName = displayName;
    }

    public String getLabel() {
        return this.label;
    }

    public String getDisplayName() {
        return this.displayName;
    }


}
