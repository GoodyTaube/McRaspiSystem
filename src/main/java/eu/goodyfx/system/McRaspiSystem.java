package eu.goodyfx.system;

import eu.goodyfx.system.core.SystemStartUp;
import eu.goodyfx.system.core.commands.*;
import eu.goodyfx.system.core.database.DatabaseManager;
import eu.goodyfx.system.core.database.RaspiPlayers;
import eu.goodyfx.system.core.events.PlayerLifecycleListener;
import eu.goodyfx.system.core.managers.RaspiHookManager;
import eu.goodyfx.system.core.managers.RaspiModuleManager;
import eu.goodyfx.system.core.tasks.*;
import eu.goodyfx.system.core.utils.InHeadSpectator;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiDebugger;
import eu.goodyfx.system.core.utils.RaspiSubSystem;
import eu.goodyfx.system.lootchest.LootChestSystem;
import eu.goodyfx.system.lootchest.tasks.LootChestTimer;
import eu.goodyfx.system.raspievents.RaspiEventsSystem;
import eu.goodyfx.system.reise.RaspiReiseSystem;
import eu.goodyfx.system.trader.TraderSystem;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public final class McRaspiSystem extends JavaPlugin {

    private RaspiModuleManager moduleManager;
    private RaspiHookManager hookManager;
    private RaspiDebugger debugger;
    private DatabaseManager databaseManager;

    private final Random random = new Random();


    private BukkitTask raspiItemsRunner;
    private BukkitRunnable idleTask;
    private BukkitRunnable weeklyTimer;
    private BukkitRunnable animation;
    private BukkitRunnable restoreInv;
    private BukkitRunnable dailyCommand;
    private BukkitRunnable inHeadTask;
    //private BukkitRunnable playTimeTask;
    private BukkitRunnable tabListTask;
    private final List<BukkitRunnable> tasks = new ArrayList<>();
    private LootChestTimer lootChestTimer;

    private final ExecutorService asyncExecutor = Executors.newFixedThreadPool(4);


    private final NamespacedKey raspiItemKey = new NamespacedKey(this, "raspiItem");

    private final List<RaspiSubSystem> raspiSubSystems = List.of(new RaspiEventsSystem(this), new LootChestSystem(this), new RaspiReiseSystem(this), new TraderSystem(this));

    @Override
    public void onEnable() {
        init();
        dataMigration();
    }

    private void playerInit() {
        RaspiPlayers players = new RaspiPlayers();
        Raspi.init(players, debugger);
        new PlayerLifecycleListener(players);
    }


    private void paperCommandsRegister() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(SitCommandContainer.sitCommand());
            commands.registrar().register(VoteCommandContainer.voteCommand(this));
            commands.registrar().register(ItemConverterCommandContainer.command());
            commands.registrar().register(RequestCommandContainer.command());
            commands.registrar().register(new RaspiGiveCommandContainer().command());
            commands.registrar().register(ChatCommandContainer.runCommand());
            commands.registrar().register(BackCommandContainer.backCommand());
            commands.registrar().register(PlayerInfoCommandContainer.command());
            commands.registrar().register(MuteCommandContainer.muteCommand());
        });

    }

    private void init() {
        this.databaseManager = new DatabaseManager();
        this.debugger = new RaspiDebugger(this);
        getLogger().info("Welcome to McRaspiSystem");
        hookManager = new RaspiHookManager(this, this);
        setupConfigs();
        moduleManager = new RaspiModuleManager(this);
        playerInit();

        new SystemStartUp();

        paperCommandsRegister();
        systemsActivation();
        tasks();
        moduleManager.getMotdManager().set();
        new InHeadSpectator();
    }

    private void tasks() {
        this.idleTask = new IdleTask();
        tasks.add(idleTask);
        this.weeklyTimer = new WeeklyTimer(this);
        tasks.add(weeklyTimer);
        this.restoreInv = new InventoryBackup(this);
        tasks.add(restoreInv);
        this.dailyCommand = new CommandResetTask(this);
        tasks.add(dailyCommand);
        this.inHeadTask = new InHeadTask();
        tasks.add(inHeadTask);
        //this.playTimeTask = new PlayTimeTask();
        //tasks.add(playTimeTask);
        this.tabListTask = new TablistAnimator();
        tasks.add(tabListTask);
    }

    private void dataMigration() {
        File file = new File(getDataFolder(), "UserDB.yml");
        if (file.exists()) {
            getServer().getWhitelistedPlayers().clear();
            getServer().setWhitelist(true);
            getConfig().set("Utilities.wartung", true);
            Raspi.players().checkOldContents();
        } else {
            getServer().setWhitelist(false);
            getDebugger().info("Keine Dateien zur Migration gefunden // SKIP TASK");
        }
    }


    private void setupConfigs() {
        checkDefaults();
        //ALLE Config bezogenen sachen
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    /**
     * Setup Command Class and load.
     *
     * @param commandLabel    The Command Name / Alias
     * @param commandExecutor The Executor Class
     */
    public void setCommand(String commandLabel, CommandExecutor commandExecutor) {
        Objects.requireNonNull(getCommand(commandLabel)).setExecutor(commandExecutor);
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
    }

    /**
     * Get a NameSpacedKey for Different actions.     *
     *
     * @param key the Value
     * @return A NameSpacedKey out of GoodyUtilities
     */
    public NamespacedKey getNameSpaced(String key) {
        return new NamespacedKey(this, key);
    }

    public void systemsActivation() {
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

    private void checkDefaults() {
        if (!getConfig().contains("raspi")) {
            getConfig().addDefault("raspi.systems.raspiLoot", true);
            getConfig().addDefault("raspi.systems.raspiTrader", true);
            getConfig().addDefault("raspi.systems.raspiReise", true);
            getConfig().addDefault("raspi.systems.raspiVoting", false);
        }
    }

    public boolean subSystemExists(String key) {
        return getConfig().contains("raspi.systems." + key);
    }

}
