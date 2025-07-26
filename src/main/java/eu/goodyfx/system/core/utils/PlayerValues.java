package eu.goodyfx.system.core.utils;

import lombok.Getter;
import org.bukkit.persistence.PersistentDataType;

@Getter
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
}
