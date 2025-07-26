package eu.goodyfx.system.core.managers;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class CommandManager {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    private final File file = new File(plugin.getDataFolder(), "pl-hide.yml");
    private FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    @Getter
    public enum CommandManagerPaths {
        TAB_COMPLETE_IMPLEMENT("group.%s.tabImplementCommands"), COMMANDS("group.%s.commands"), TAB_COMPLETE_COMMANDS("group.%s.tabComplete"), ERBEN_AUS("group.%s.erben-aus");
        private final String path;

        CommandManagerPaths(String path) {
            this.path = path;
        }
    }

    private static final String TAB_COMPLETE_IMPLEMENT = CommandManagerPaths.TAB_COMPLETE_IMPLEMENT.getPath();
    private static final String COMMANDS = CommandManagerPaths.COMMANDS.getPath();
    private static final String TAB_COMPLETE = CommandManagerPaths.TAB_COMPLETE_COMMANDS.getPath();
    private static final String ERBEN_AUS = CommandManagerPaths.ERBEN_AUS.getPath();


    public CommandManager() {
        plugin.getHookManager().getLuckPerms().getGroupManager().loadAllGroups().thenRunAsync(() -> plugin.getHookManager().getLuckPerms().getGroupManager().getLoadedGroups().forEach(group -> {
            String name = group.getName();
            config.addDefaults(addDefaults(name));
            config.options().copyDefaults(true);
            save();
        }));

    }

    /**
     * Build a Map to define Config defaults
     *
     * @param group The group to add default handled by LuckPerms
     * @return A Map of config defaults
     */
    private Map<String, Object> addDefaults(String group) {
        Map<String, Object> configDefaults = new HashMap<>();
        configDefaults.put(String.format(TAB_COMPLETE_IMPLEMENT, group), true);
        configDefaults.put(String.format(COMMANDS, group), Collections.EMPTY_LIST);
        configDefaults.put(String.format(TAB_COMPLETE, group), Collections.EMPTY_LIST);
        configDefaults.put(String.format(ERBEN_AUS, group), Collections.EMPTY_LIST);
        return configDefaults;
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "pl-hide.yml"));
    }

    public void reload(RaspiPlayer player) {
        plugin.getDebugger().info("Reloaded pl-hide.yml");
        reload();
        player.sendMessage("<green>Reload von pl-hide.yml Erfolgreich", true);
    }

    /**
     * Save a value to config
     *
     * @param group The group
     * @param path  The path to set
     * @param value the actual value to set List or Boolean
     */
    public void set(String group, CommandManagerPaths path, Object value) {
        if (pathIsList(path)) {
            List<String> commandList = config.getStringList(String.format(path.getPath(), group));
            commandList.add(String.valueOf(value));
            config.set(String.format(path.getPath(), group), commandList);
        } else {
            config.set(String.format(path.getPath(), group), value);
        }
        save();
    }

    /**
     * Check if the given Path of {@link CommandManagerPaths} is type of List
     *
     * @param path The requested Path
     * @return True if the {@link CommandManagerPaths} is List
     */
    private boolean pathIsList(CommandManagerPaths path) {
        return switch (path) {
            case COMMANDS, TAB_COMPLETE_COMMANDS, ERBEN_AUS -> true;
            case TAB_COMPLETE_IMPLEMENT -> false;
        };
    }

    /**
     * Get any value of the config
     *
     * @param group     The requested Group
     * @param path      The enum path to the request
     * @param classType the requested Type of the Request
     * @return A value from the plHide config
     */
    public <T> T get(String group, CommandManagerPaths path, Class<T> classType) {
        group = group.toLowerCase();
        return classType.cast(config.get(String.format(path.getPath(), group)));
    }

    public Set<String> getPlayerGroups(RaspiPlayer player){
        return getAllGroups().stream().filter(s -> player.hasPermission("group." +s)).collect(Collectors.toSet());
    }

    /**
     * Get the requested List and replaces the / to prevent errors
     *
     * @param group The requested Group
     * @param path  the path to the List
     * @return a Set of Commands replaced and available to process
     */
    public Set<String> getList(String group, CommandManagerPaths path) {

        return config.getStringList(String.format(path.getPath(), group.toLowerCase())).stream().map(str -> str.replace("/", "")).collect(Collectors.toSet());
    }

    /**
     * Check if the requested group String exist in config
     *
     * @param group The requested Group
     * @return True if config contains that Group
     */
    public boolean groupExists(String group) {
        group = group.toLowerCase();
        return config.contains(String.format("group.%s", group));
    }

    /**
     * Get a Set of all Groups
     *
     * @return A Set of groups
     */
    public Set<String> getAllGroups() {
        return new HashSet<>(Objects.requireNonNull(config.getConfigurationSection("group")).getKeys(false));
    }


    /**
     * Einfach nur config Speichern Bernd :)
     */
    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
