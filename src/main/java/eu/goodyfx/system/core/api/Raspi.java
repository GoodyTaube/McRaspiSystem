package eu.goodyfx.system.core.api;

import eu.goodyfx.system.core.utils.RaspiDebugger;

public class Raspi {

    private static RaspiDebugger DEBUGGER;
    private static RaspiAccountService ACCOUNTSERVICE;
    private static PlayerLifeCycleService PLAYERLIFECYCLESERVICE;

    public static void init(RaspiDebugger debugger, RaspiAccountService accountService, PlayerLifeCycleService playerLifeCycleService) {
        DEBUGGER = debugger;
        ACCOUNTSERVICE = accountService;
        PLAYERLIFECYCLESERVICE = playerLifeCycleService;
    }

    public static RaspiDebugger debugger() {
        return DEBUGGER;
    }

    public static RaspiAccountService accountService() {
        return ACCOUNTSERVICE;
    }

    public static PlayerLifeCycleService playerLifeCycleService() {
        return PLAYERLIFECYCLESERVICE;
    }

}
