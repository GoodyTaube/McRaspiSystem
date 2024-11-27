package eu.goodyfx.mcraspisystem;

import eu.goodyfx.mcraspisystem.managers.RaspiHookManager;
import eu.goodyfx.mcraspisystem.managers.RaspiModuleManager;
import eu.goodyfx.mcraspisystem.tasks.*;
import eu.goodyfx.mcraspisystem.utils.Item;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import eu.goodyfx.mcraspisystem.utils.SystemStartUp;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import java.util.Set;

public final class McRaspiSystem extends JavaPlugin {

    private RaspiModuleManager moduleManager;
    private RaspiHookManager hookManager;

    private BukkitTask raspiItemsRunner;
    private BukkitRunnable idleTask;
    private BukkitRunnable weeklyTimer;
    private BukkitRunnable animation;
    private LootChestTimer lootChestTimer;

    private final NamespacedKey raspiItemKey = new NamespacedKey(this, "raspiItem");
    private Item mapItem = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        init();
    }

    private void init() {
        getLogger().info("Welcome to McRaspiSystem");
        hookManager = new RaspiHookManager(this, this);
        setupConfigs();
        moduleManager = new RaspiModuleManager(this);
        new SystemStartUp(this);

        this.raspiItemsRunner = new RaspiItemsTimer(this).runTaskTimerAsynchronously(this, 0L, 20L);
        this.idleTask = new IdleTask(this, this);
        this.weeklyTimer = new WeeklyTimer(this);
        this.animation = new AnimationBlockDisplay(this);
        this.lootChestTimer = new LootChestTimer(this);
        moduleManager.getMotdManager().set();
    }


    private void setupConfigs() {
        //ALLE Config bezogenen sachen
        getConfig().options().copyDefaults(true);
        saveConfig();

        if (getConfig().contains("items.mapLabel")) {
            try {
                Material material = Material.valueOf(getConfig().getString("items.mapLabel.type"));
                mapItem = new Item(material, getConfig().getInt("items.mapLabel.id"));
            } catch (EnumConstantNotPresentException e) {
                String log = String.format("Das Item %s existiert nicht.", getConfig().getString("items.mapLabel.type"));
                getLogger().info(log);
            }
        }
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
     * Get all third Party Plugins to Run System
     *
     * @return A Manager with all NEEDED API'S
     */
    public RaspiHookManager getHookManager() {
        return hookManager;
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
        lootChestTimer.cancel();
    }

    /**
     * Get List of RaspiPlayers by {@link Bukkit#getOnlinePlayers()}
     *
     * @return A list of RaspiPlayers
     */
    public Set<RaspiPlayer> getRaspiPlayers() {
        Set<RaspiPlayer> playerSet = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerSet.add(new RaspiPlayer(this, player));
        }
        return playerSet;
    }

    /**
     * Convert normal Player to RaspiPlayer
     *
     * @param player The Bukkit Player
     * @return Converted Raspi PLayer
     */
    public RaspiPlayer getRaspiPlayer(Player player) {
        return new RaspiPlayer(this, player);
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

    public NamespacedKey getRaspiItemKey() {
        return this.raspiItemKey;
    }

    public Item getMapItem() throws NullPointerException {
        if (mapItem != null) {
            return this.mapItem;
        }
        throw new NullPointerException("Item is Null!");
    }

    public LootChestTimer getLootChestTimer() {
        return this.lootChestTimer;
    }

}
