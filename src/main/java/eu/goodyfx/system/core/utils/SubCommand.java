package eu.goodyfx.system.core.utils;

import eu.goodyfx.system.McRaspiSystem;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Abstract class representing a sub-command that can be executed by a player.
 * <p>
 * Subclasses should implement methods to define the command's label,
 * description, syntax, and execution logic.
 */
@Getter
public abstract class SubCommand {

    private final Map<String, BiConsumer<RaspiPlayer, String[]>> actions = new HashMap<>();

    protected void register(String name, BiConsumer<RaspiPlayer, String[]> method) {
        actions.put(name, method);
    }

    public abstract String getLabel();

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract int length();

    public abstract boolean commandPerform(RaspiPlayer player, String[] args);

    public void invoke(String name, RaspiPlayer player, String[] args) {
        BiConsumer<RaspiPlayer, String[]> action = actions.get(name);
        if (action != null) {
            action.accept(player, args);
        } else {
            JavaPlugin.getPlugin(McRaspiSystem.class).getLogger().info("No Action to Process.");
        }
    }

}
