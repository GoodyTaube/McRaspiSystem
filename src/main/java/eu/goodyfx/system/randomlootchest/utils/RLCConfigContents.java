package eu.goodyfx.system.randomlootchest.utils;

import lombok.Getter;

@Getter
public enum RLCConfigContents {

    MESSAGE_SPAWN("SpawnBroadcastMessage"),
    MESSAGE_KILL("BroadcastMessage"),
    MESSAGE_LOOT("MessageOnLoot");


    private final String path;

    RLCConfigContents(String path){
        this.path = path;
    }

}
