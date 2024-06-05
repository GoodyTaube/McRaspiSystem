package eu.goodyfx.mcraspisystem;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class McRaspiSystem extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        init();
    }

    private void init(){
        getLogger().info("Welcome to McRaspiSystem");
        setupConfigs();
        registerCommands();
        registerEvents();
    }

    private void setupConfigs(){
        //ALLE Config bezogenen sachen
    }

    private void registerCommands(){
        //Alle Commands sortiert nach Wichtigkeit
    }

    private void registerEvents(){
        //Alle Events sortiert nach Wichtigkeit
    }

    /**
     * Setup Command Class and load.
     * @param commandLabel The Command Name / Alias
     * @param commandExecutor The Executor Class
     */
    public void setCommand(String commandLabel, CommandExecutor commandExecutor){
        Objects.requireNonNull(getCommand(commandLabel)).setExecutor(commandExecutor);
    }

    /**
     * Setup Command Class and load.
     * @param commandLabel The Command Name / Alias
     * @param commandExecutor The Executor Class
     */
    public void setCommand(String commandLabel, CommandExecutor commandExecutor, TabCompleter commandTabCompleter){
        Objects.requireNonNull(getCommand(commandLabel)).setExecutor(commandExecutor);
        Objects.requireNonNull(getCommand(commandLabel)).setTabCompleter(commandTabCompleter);
    }

    /**
     * Setup Listeners and load
     * @param listeners The Listeners Class
     */
    public void setListeners(Listener listeners){
        Bukkit.getPluginManager().registerEvents(listeners, this);
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
