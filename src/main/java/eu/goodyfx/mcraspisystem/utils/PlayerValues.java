package eu.goodyfx.mcraspisystem.utils;

import org.bukkit.persistence.PersistentDataType;

public enum PlayerValues {

    DEATH("death", PersistentDataType.LONG),
    COLOR("nameColor", PersistentDataType.STRING),
    MUTE("muted", PersistentDataType.INTEGER),
    PREFIX("prefix", PersistentDataType.STRING),
    AFK("isAfk", PersistentDataType.INTEGER);

    private final String label;
    private final PersistentDataType<?, ?> persistentDataType;

    PlayerValues(String label, PersistentDataType<?, ?> type) {
        this.label = label;
        this.persistentDataType = type;
    }

    public String getLabel() {
        return label;
    }

    public PersistentDataType getPersistentDataType() {
        return persistentDataType;
    }
}
