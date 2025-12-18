package eu.goodyfx.system.core.utils;

import lombok.Getter;

@Getter
public enum RaspiSubSystems {

    LOOTING("raspiLoot"),
    EVENTS("raspiEvents"),
    REISE("raspiReise");

    private final String name;

    RaspiSubSystems(String name) {
        this.name = name;
    }



}
