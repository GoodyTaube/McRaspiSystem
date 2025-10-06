package eu.goodyfx.system.core.managers;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.RaspiMessages;
import eu.goodyfx.system.lootchest.managers.LootChestManager;
import eu.goodyfx.system.lootchest.managers.LootManager;
import eu.goodyfx.system.trader.managers.TraderDB;
import lombok.Getter;

@Getter
public class RaspiModuleManager {

    private final JoinMessageManager joinMessageManager;
    private final LocationManager locationManager;
    private final MessageManager messageManager;
    private final RequestManager requestManager;
    private final WarteschlangenManager warteschlangenManager;
    private final RaspiMessages raspiMessages;
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
        this.messageManager = new MessageManager(system);
        this.raspiMessages = new RaspiMessages(this);
        this.joinMessageManager = new JoinMessageManager(this);
        this.locationManager = new LocationManager(system);
        this.requestManager = new RequestManager(this);
        this.warteschlangenManager = new WarteschlangenManager(this);
        this.lootManager = new LootManager(system);
        this.timeDBManager = new TimeDBManager(system);
        this.lootChestManager = new LootChestManager(system);
        this.motdManager = new MOTDManager(system);

    }
}
