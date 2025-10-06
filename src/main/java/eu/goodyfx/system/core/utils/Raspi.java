package eu.goodyfx.system.core.utils;

import eu.goodyfx.system.core.database.RaspiPlayers;

public class Raspi {

    private static RaspiPlayers PLAYERS;
    private static RaspiDebugger DEBUGGER;

    public static void init(RaspiPlayers players, RaspiDebugger debugger) {
        PLAYERS = players;
        DEBUGGER = debugger;
    }

    public static RaspiPlayers players() {
        return PLAYERS;
    }

    public static RaspiDebugger debugger() {
        return DEBUGGER;
    }


}
