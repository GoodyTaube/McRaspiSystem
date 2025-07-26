package eu.goodyfx.mcraspisystem.managers;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.utils.PlayerNameController;
import eu.goodyfx.mcraspisystem.utils.RaspiMessages;
import lombok.Getter;

@Getter
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
    private final TimeDBManager timeDBManager;
    private final LootChestManager lootChestManager;
    private final MOTDManager motdManager;
    private final TraderDB traderDB = new TraderDB();
    private final CommandManager commandManager = new CommandManager();
    private final ItemConverterManager itemConverterManager = new ItemConverterManager();
    private final RaspiGiveManager raspiGiveManager = new RaspiGiveManager();
    
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
        this.timeDBManager = new TimeDBManager(system);
        this.lootChestManager = new LootChestManager(system);
        this.motdManager = new MOTDManager(system);

    }
}
