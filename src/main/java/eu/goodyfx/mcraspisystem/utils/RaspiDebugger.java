package eu.goodyfx.mcraspisystem.utils;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.MissingResourceException;
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
        super(context.getClass().getCanonicalName(), null);
        String prefix = context.getDescription().getPrefix();
        pluginName = prefix != null ? new StringBuilder().append("[").append(prefix).append("] ").toString() : "[" + context.getDescription().getName() + "] ";
        setParent(context.getServer().getLogger());
        setLevel(Level.ALL);
    }

    @Override
    public void log(@NotNull LogRecord logRecord) {
        logRecord.setMessage(pluginName + " // DEBUGG //" + logRecord.getMessage());
        super.log(logRecord);
    }

}
