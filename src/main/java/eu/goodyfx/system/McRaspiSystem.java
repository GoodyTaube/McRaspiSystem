package eu.goodyfx.system;

import eu.goodyfx.system.core.SystemStartUp;
import eu.goodyfx.system.core.commands.ItemConverterCommandContainer;
import eu.goodyfx.system.core.commands.RaspiGiveCommandContainer;
import eu.goodyfx.system.core.commands.VoteCommandContainer;
import eu.goodyfx.system.core.managers.RaspiHookManager;
import eu.goodyfx.system.core.managers.RaspiModuleManager;
import eu.goodyfx.system.core.tasks.*;
import eu.goodyfx.system.core.utils.*;
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
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public final class McRaspiSystem extends JavaPlugin {

    private RaspiModuleManager moduleManager;
    private RaspiHookManager hookManager;
    private RaspiDebugger debugger;

    private final Random random = new Random();


    private BukkitTask raspiItemsRunner;
    private BukkitRunnable idleTask;
    private BukkitRunnable weeklyTimer;
    private BukkitRunnable animation;
    private BukkitRunnable restoreInv;
    private BukkitRunnable dailyCommand;
    private BukkitRunnable inHeadTask;
    private LootChestTimer lootChestTimer;


    private final NamespacedKey raspiItemKey = new NamespacedKey(this, "raspiItem");

    private final List<RaspiSubSystem> raspiSubSystems = List.of(
            new RaspiEventsSystem(this),
            new LootChestSystem(this),
            new RaspiReiseSystem(this),
            new TraderSystem(this));

    @Override
    public void onEnable() {
        // Plugin startup logic
        init();
    }

    private void paperCommandsRegister() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            //commands.registrar().register(AdminWartungSubCommand.adminWartungCommand().build());
            //commands.registrar().register(SitCommandContainer.sitCommand());
            //commands.registrar().register(BackCommandContainer.backCommand());
            //commands.registrar().register(ChatColorCommandContainer.chatColorCommand());
            commands.registrar().register(VoteCommandContainer.voteCommand(this));
            commands.registrar().register(ItemConverterCommandContainer.command());
            commands.registrar().register(new RaspiGiveCommandContainer().command());
        });

    }

    private void init() {
        this.debugger = new RaspiDebugger(this);
        getLogger().info("Welcome to McRaspiSystem");
        hookManager = new RaspiHookManager(this, this);
        setupConfigs();
        moduleManager = new RaspiModuleManager(this);

        new SystemStartUp();

        paperCommandsRegister();
        systemsActivation();
        //recipes();
        this.idleTask = new IdleTask(this, this);
        this.weeklyTimer = new WeeklyTimer(this);
        this.restoreInv = new InventoryBackup(this);
        this.dailyCommand = new CommandResetTask(this);
        this.inHeadTask = new InHeadTask();
        moduleManager.getMotdManager().set();
        new InHeadSpectator();
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
        this.idleTask.cancel();
        this.weeklyTimer.cancel();
        this.restoreInv.cancel();
        this.dailyCommand.cancel();
        this.inHeadTask.cancel();
    }

    /**
     * Get List of RaspiPlayers by {@link Bukkit#getOnlinePlayers()}
     *
     * @return A list of RaspiPlayers
     */
    public Set<RaspiPlayer> getRaspiPlayers() {
        Set<RaspiPlayer> playerSet = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerSet.add(new RaspiPlayer(player));
        }
        return playerSet;
    }

    public Set<RaspiPlayer> getRaspiTeamPlayers() {
        return getRaspiPlayers().stream()
                .filter(raspiPlayer -> raspiPlayer.hasPermission(RaspiPermission.TEAM))
                .collect(Collectors.toSet());
    }

    public Set<RaspiPlayer> getRaspiPlayersPerPermission(RaspiPermission permission) {
        return getRaspiPlayers().stream()
                .filter(raspiPlayer -> raspiPlayer.hasPermission(permission))
                .collect(Collectors.toSet());
    }

    /**
     * Convert normal Player to RaspiPlayer
     *
     * @param player The Bukkit Player
     * @return Converted Raspi PLayer
     */
    public RaspiPlayer getRaspiPlayer(Player player) {
        return new RaspiPlayer(player);
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
            getConfig().addDefault("raspi.systems.raspiEvents", false);
            getConfig().addDefault("raspi.systems.raspiReise", true);
        }
    }

}
