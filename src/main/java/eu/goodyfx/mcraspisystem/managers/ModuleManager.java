package eu.goodyfx.mcraspisystem.managers;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class ModuleManager {

    private final List<UtilitieModule> modules = new ArrayList<>();

    private final McRaspiSystem system;

    public ModuleManager(McRaspiSystem plugin) {
        this.system = plugin;
    }

    private static PluginCommand getCommand(String name, Plugin plugin) {
        PluginCommand command = null;
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            command = constructor.newInstance(name, plugin);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | InstantiationException |
                 InvocationTargetException | NoSuchMethodException e) {
            Bukkit.getLogger().log(Level.SEVERE, "PluginCommand Error", e);
        }
        return command;
    }

    public void add(UtilitieModule module) {
        this.modules.add(module);
    }

    public void remove(UtilitieModule module) {
        this.modules.remove(module);
    }

    public void loadAllFromFile(@NotNull String dirName) {
        Bukkit.getLogger().info("GoodysUtilities: Loading ALl Modules!");
        String filePath = String.format("%s\\%s", system.getDataFolder(), dirName);
        File file = new File(filePath);
        if (file.exists() && file.isDirectory()) {
            Plugin[] plugins = Bukkit.getPluginManager().loadPlugins(file);
            for (Plugin plugin : plugins) {
                if (plugin != null && !plugin.isEnabled()) {
                    Bukkit.getPluginManager().enablePlugin(plugin);
                }
            }
        } else {
            if (file.mkdirs()) {
                Bukkit.getLogger().info("New File Created!");
            }
        }
    }

    public void disable(String moduleLabel) {
        modules.forEach(module -> {
            if (module.getLabel().equalsIgnoreCase(moduleLabel) && Boolean.TRUE.equals(module.getEnabled())) {
                Bukkit.getPluginManager().disablePlugin(module.getModule());
            }
        });
    }

    public List<UtilitieModule> getModules() {
        return this.modules;
    }

    public void registerCommand(CommandExecutor executor, String... aliases) {
        PluginCommand command = getCommand(aliases[0], system.getPlugin());
        command.setAliases(Arrays.asList(aliases));
        system.getServer().getCommandMap().register(system.getName(), command);
        command.setExecutor(executor);
    }


}
