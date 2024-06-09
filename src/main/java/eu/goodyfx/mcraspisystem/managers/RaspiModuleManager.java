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

    public RaspiModuleManager(McRaspiSystem system) {
        system.getLogger().info("Initialize Modules...");
        this.raspiMessages = new RaspiMessages(system);
        this.joinMessageManager = new JoinMessageManager(system);
        this.locationManager = new LocationManager(system);
        this.messageManager = new MessageManager(system);
        this.playerBanManager = new PlayerBanManager(system);
        this.playerSettingsManager = new PlayerSettingsManager(system);
        this.playerNameController = new PlayerNameController(system);
        this.requestManager = new RequestManager(system);
        this.userManager = new UserManager(system);
        this.warteschlangenManager = new WarteschlangenManager(system);
        this.prefixManager =new PrefixManager(system);
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
}
