package eu.goodyfx.system.core.utils;

import eu.goodyfx.system.McRaspiSystem;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class RaspiDebugger extends Logger {

    private String pluginName;

    /**
     * Creates a new PluginLogger that extracts the name from a plugin.
     *
     * @param context A reference to the plugin
     */
    public RaspiDebugger(@NotNull Plugin context) {
        super(context.getClass().getSimpleName(), null);

        String prefix = context.getDescription().getPrefix();
        pluginName = prefix != null ? new StringBuilder().append("[").append(prefix).append("] ").toString() : "[" + context.getDescription().getName() + "] ";
        setParent(context.getServer().getLogger());
        setLevel(Level.ALL);
    }

    @Override
    public void log(@NotNull LogRecord logRecord) {
        if (JavaPlugin.getPlugin(McRaspiSystem.class).getConfig().contains("debug")) {
            logRecord.setMessage("DEBUGGER //" + logRecord.getMessage());
            super.log(logRecord);
        }
    }

}
