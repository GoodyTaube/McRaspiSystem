package eu.goodyfx.system.core.utils;

import lombok.Getter;

@Getter
public enum RaspiSubSystems {

    LOOTING("raspiLoot", true),
    RANDO_LOOT_CHEST("randomLootChest", false),
    PVP("raspiPvP", true),
    EVENTS("raspiEvents", false),
    TRADER("raspiTrader", true),
    REISE("raspiReise", true);

    private final String name;
    private final Boolean default_enabled;

    RaspiSubSystems(String name, boolean default_enabled) {
        this.name = name;
        this.default_enabled = default_enabled;
    }


}
