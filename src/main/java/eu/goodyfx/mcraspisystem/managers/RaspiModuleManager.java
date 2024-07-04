package eu.goodyfx.mcraspisystem.managers;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.utils.PlayerNameController;
import eu.goodyfx.mcraspisystem.utils.RaspiMessages;

public class RaspiModuleManager {

    private final JoinMessageManager joinMessageManager;
    private final LocationManager locationManager;
    private final MessageManager messageManager;
    private final PlayerBanManager playerBanManager;
    private final PlayerNameController playerNameController;
    private final PlayerSettingsManager playerSettingsManager;
    private final RequestManager requestManager;
    private final UserManager userManager;
    private final WarteschlangenManager warteschlangenManager;
    private final RaspiMessages raspiMessages;
    private final PrefixManager prefixManager;
    private final McRaspiSystem plugin;
    private final LootManager lootManager;


    public RaspiModuleManager(McRaspiSystem system) {
        this.plugin = system;
        system.getLogger().info("Initialize Modules...");
        this.userManager = new UserManager(system);
        this.prefixManager = new PrefixManager(this);
        this.playerNameController = new PlayerNameController(this);
        this.messageManager = new MessageManager(system);
        this.raspiMessages = new RaspiMessages(this);
        this.joinMessageManager = new JoinMessageManager(this);
        this.locationManager = new LocationManager(system);
        this.playerBanManager = new PlayerBanManager(this);
        this.playerSettingsManager = new PlayerSettingsManager(this);
        this.requestManager = new RequestManager(this);
        this.warteschlangenManager = new WarteschlangenManager(this);
        this.lootManager = new LootManager(system);

    }

    public JoinMessageManager getJoinMessageManager() {
        return joinMessageManager;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public PlayerBanManager getPlayerBanManager() {
        return playerBanManager;
    }

    public PlayerSettingsManager getPlayerSettingsManager() {
        return playerSettingsManager;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public RaspiMessages getRaspiMessages() {
        return raspiMessages;
    }

    public WarteschlangenManager getWarteschlangenManager() {
        return warteschlangenManager;
    }

    public PlayerNameController getPlayerNameController() {
        return playerNameController;
    }

    public PrefixManager getPrefixManager() {
        return prefixManager;
    }

    public McRaspiSystem getPlugin() {
        return this.plugin;
    }

    public LootManager getLootManager() {
        return lootManager;
    }
}
