package eu.goodyfx.mcraspisystem;

import eu.goodyfx.mcraspisystem.commands.container.ItemConverterCommandContainer;
import eu.goodyfx.mcraspisystem.commands.container.RaspiGiveCommandContainer;
import eu.goodyfx.mcraspisystem.commands.container.VoteCommandContainer;
import eu.goodyfx.mcraspisystem.craftings.CanabolaCraftging;
import eu.goodyfx.mcraspisystem.craftings.EntityGranadeCrafting;
import eu.goodyfx.mcraspisystem.managers.RaspiHookManager;
import eu.goodyfx.mcraspisystem.managers.RaspiModuleManager;
import eu.goodyfx.mcraspisystem.tasks.*;
import eu.goodyfx.mcraspisystem.utils.*;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
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
    private Item mapItem = null;

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
        //recipes();
        this.raspiItemsRunner = new RaspiItemsTimer(this).runTaskTimerAsynchronously(this, 0L, 20L);
        this.idleTask = new IdleTask(this, this);
        this.weeklyTimer = new WeeklyTimer(this);
        this.animation = new AnimationBlockDisplay(this);
        this.lootChestTimer = new LootChestTimer(this);
        this.restoreInv = new InventoryBackup(this);
        this.dailyCommand = new CommandResetTask(this);
        this.inHeadTask = new InHeadTask();
        moduleManager.getMotdManager().set();
        new InHeadSpectator();
    }

    private void recipes() {
        new CanabolaCraftging(this);
        new EntityGranadeCrafting();
    }


    private void setupConfigs() {
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
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.raspiItemsRunner.cancel();
        this.idleTask.cancel();
        this.weeklyTimer.cancel();
        this.animation.cancel();
        this.restoreInv.cancel();
        lootChestTimer.cancel();
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

    public Item getMapItem() throws NullPointerException {
        if (mapItem != null) {
            return this.mapItem;
        }
        throw new NullPointerException("Item is Null!");
    }


}
