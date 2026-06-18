package eu.goodyfx.mcraspi.core.api;

import eu.goodyfx.mcraspi.core.utils.RaspiDebugger;

public class Raspi {

    private static RaspiDebugger DEBUGGER;
    private static RaspiAccountService ACCOUNTSERVICE;
    private static PlayerLifeCycleService PLAYERLIFECYCLESERVICE;
    private static PluginKeys PLUGIN_KEYS;

    public static void init(RaspiDebugger debugger, RaspiAccountService accountService, PlayerLifeCycleService playerLifeCycleService, PluginKeys keys) {
        DEBUGGER = debugger;
        ACCOUNTSERVICE = accountService;
        PLAYERLIFECYCLESERVICE = playerLifeCycleService;
        PLUGIN_KEYS = keys;
    }

    public static RaspiDebugger debugger() {
        return DEBUGGER;
    }

    public static RaspiAccountService accountService() {
        return ACCOUNTSERVICE;
    }

    public static PluginKeys pluginKeys() {
        return PLUGIN_KEYS;
    }

    public static PlayerLifeCycleService playerLifeCycleService() {
        return PLAYERLIFECYCLESERVICE;
    }


}
