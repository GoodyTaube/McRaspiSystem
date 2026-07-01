package eu.goodyfx.mcraspi;

import eu.goodyfx.mcraspi.core.SystemStartUp;
import eu.goodyfx.mcraspi.core.api.PlayerLifeCycleService;
import eu.goodyfx.mcraspi.core.api.PluginKeys;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.api.RaspiAccountService;
import eu.goodyfx.mcraspi.core.commands.RaspiCommand;
import eu.goodyfx.mcraspi.core.database.DatabaseManager;
import eu.goodyfx.mcraspi.core.events.PlayerLifecycleListener;
import eu.goodyfx.mcraspi.core.managers.RaspiHookManager;
import eu.goodyfx.mcraspi.core.managers.RaspiModuleManager;
import eu.goodyfx.mcraspi.core.security.BookBanFix;
import eu.goodyfx.mcraspi.core.tasks.*;
import eu.goodyfx.mcraspi.core.utils.DatabaseUpdate;
import eu.goodyfx.mcraspi.core.utils.RaspiDebugger;
import eu.goodyfx.mcraspi.core.utils.RaspiSubSystem;
import eu.goodyfx.mcraspi.core.utils.RaspiSubSystems;
import eu.goodyfx.mcraspi.modules.loot.staticlootchest.LootChestSystem;
import eu.goodyfx.mcraspi.modules.loot.worldlootchest.RandomLootChest;
import eu.goodyfx.mcraspi.modules.pvp.PvPSubSystem;
import eu.goodyfx.mcraspi.modules.raspievents.RaspiEventsSystem;
import eu.goodyfx.mcraspi.modules.reise.RaspiReiseSystem;
import eu.goodyfx.mcraspi.modules.trader.TraderSubSystem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public final class McRaspiSystem extends JavaPlugin {

    private RaspiModuleManager moduleManager;
    private RaspiHookManager hookManager;
    private RaspiDebugger debugger;
    private PluginKeys pluginKeys;
    private DatabaseManager databaseManager;
    private RaspiAccountService raspiAccountService;
    private PlayerLifeCycleService playerLifeCycleService;
    private SystemStartUp startUp;

    @Getter
    private final Set<RaspiCommand> commandCache = ConcurrentHashMap.newKeySet();

    private final NamespacedKey raspiItemKey = new NamespacedKey(this, "raspiItem");
    private final Random random = new Random();
    private BukkitTask raspiItemsRunner;
    private BukkitRunnable idleTask;
    private BukkitRunnable weeklyTimer;
    private BukkitRunnable animation;
    private BukkitRunnable restoreInv;
    private BukkitRunnable inHeadTask;
    private BukkitRunnable playTimeTask;
    private BukkitRunnable tabListTask;
    private BukkitRunnable transactionsTask;
    private final List<BukkitRunnable> tasks = new ArrayList<>();
    private final ExecutorService asyncExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final List<RaspiSubSystem> raspiSubSystems = List.of(new RandomLootChest(this), new PvPSubSystem(this), new RaspiEventsSystem(this), new LootChestSystem(this), new RaspiReiseSystem(this), new TraderSubSystem(this));

    @Override
    public void onEnable() {
        init();
    }

    private void playerInit() {
        Raspi.init(debugger, raspiAccountService, playerLifeCycleService, pluginKeys);
        new PlayerLifecycleListener();
    }


    public void registerCommand(RaspiCommand command) {
        commandCache.add(command);
    }

    private void init() {
        this.databaseManager = new DatabaseManager();
        new DatabaseUpdate(this); // Update Table if NEEDED!
        this.debugger = new RaspiDebugger(this);
        this.pluginKeys = new PluginKeys(this);
        getLogger().info("Welcome to McRaspiSystem");
        hookManager = new RaspiHookManager(this, this);
        setupConfigs();
        moduleManager = new RaspiModuleManager(this);
        services();
        playerInit();
        this.startUp = new SystemStartUp();
        subSystemActivation();
        tasks();
        moduleManager.getMotdManager().set();
        new BookBanFix(this);
    }

    private void services() {
        raspiAccountService = new RaspiAccountService(getAsyncExecutor());
        this.playerLifeCycleService = new PlayerLifeCycleService(this, raspiAccountService);
    }

    private void tasks() {
        this.idleTask = new IdleTask();
        tasks.add(idleTask);
        this.weeklyTimer = new WeeklyTimer(this);
        tasks.add(weeklyTimer);
        this.restoreInv = new InventoryBackup(this);
        tasks.add(restoreInv);
        //this.inHeadTask = new InHeadTask();
        tasks.add(inHeadTask);
        this.playTimeTask = new PlayTimeTask();
        tasks.add(playTimeTask);
        this.tabListTask = new TablistAnimator();
        tasks.add(tabListTask);

        this.transactionsTask = new OpenTransactionsTask(this);
        tasks.add(transactionsTask);
    }

    /**
     * OLD Database Migration method
     */
    private void dataMigration() {
        File file = new File(getDataFolder(), "UserDB.yml");
        if (file.exists()) {
            getServer().getWhitelistedPlayers().clear();
            getServer().setWhitelist(true);
            getConfig().set("Utilities.wartung", true);
        } else {
            getServer().setWhitelist(false);
            getLogger().info("Keine Dateien zur Migration gefunden // SKIP TASK");
        }
    }


    private void setupConfigs() {
        checkDefaults();
        //ALLE Config bezogenen sachen
        getConfig().options().copyDefaults(true);
        saveConfig();
    }


    /**
     * Get All Raspi "Modules" like Managers and Data Stuff
     *
     * @return a McRaspi Module
     */
    public RaspiModuleManager getModule() {
        return moduleManager;
    }


    /**
     * Setup Command Class and load.
     *
     * @param commandLabel    The Command Name / Alias
     * @param commandExecutor The Executor Class
     */
    public void setCommand(String commandLabel, CommandExecutor commandExecutor, TabCompleter commandTabCompleter) {
        Objects.requireNonNull(getCommand(commandLabel)).setExecutor(commandExecutor);
        Objects.requireNonNull(getCommand(commandLabel)).setTabCompleter(commandTabCompleter);
    }

    /**
     * Setup Listeners and load
     *
     * @param listeners The Listeners Class
     */
    public void setListeners(Listener listeners) {
        Bukkit.getPluginManager().registerEvents(listeners, this);
        getDebugger().info(String.format("Registered: %s", listeners.getClass().getSimpleName()));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
        for (BukkitRunnable task : tasks) {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
        }
        debugger.shutdown();
        raspiSubSystems.forEach(RaspiSubSystem::onDisable);
    }

    /**
     * Get a NameSpacedKey for Different actions.     *
     *
     * @param key the Value
     * @return A NameSpacedKey out of GoodyUtilities
     */
    @Contract("_ -> new")
    public @NotNull NamespacedKey getNameSpaced(String key) {
        return new NamespacedKey(this, key);
    }

    public void subSystemActivation() {
        String systemPath = "raspi.systems.%s";
        for (RaspiSubSystem subSystem : raspiSubSystems) {
            String key = String.format(systemPath, subSystem.systemKey());
            boolean enable = getConfig().getBoolean(key, false);
            if (enable) {
                subSystem.setEnabled(true);
                subSystem.onEnabled();
                getLogger().info(String.format("Subsystem: %s wird aktiviert.", subSystem.systemKey()));
            } else {
                getLogger().info(String.format("Subsystem: %s wurde per Config Deaktiviert.", subSystem.systemKey()));
            }
        }

    }

    /**
     * Method to set Configuration Defaults
     */
    private void checkDefaults() {
        String path = "raspi.systems.%s";
        List<RaspiSubSystems> systems = List.of(RaspiSubSystems.values());
        for (RaspiSubSystems subSystem : RaspiSubSystems.values()) {
            path = String.format(path, subSystem.getName());
            if (getConfig().getDefaults() != null && !getConfig().getDefaults().contains(path)) {
                getConfig().addDefault(path, subSystem.getDefault_enabled());
            }
        }
    }

    public boolean subSystemExists(String key) {
        return getConfig().contains("raspi.systems." + key);
    }

    public RaspiSubSystem getSubSystem(RaspiSubSystems system) throws NullPointerException {
        for (RaspiSubSystem all : raspiSubSystems) {
            if (system.getName().equalsIgnoreCase(all.systemKey())) {
                return all;
            }
        }
        return null;
    }

}
