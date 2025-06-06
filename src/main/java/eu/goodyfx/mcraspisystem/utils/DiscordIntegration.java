package eu.goodyfx.mcraspisystem.utils;


import eu.goodyfx.mcraspisystem.McRaspiSystem;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@SuppressWarnings("unused")
public class DiscordIntegration {

    private DiscordHandler defaultHandler;
    private DiscordHandler errorHandler;
    private static final String DISCORD_URL = "discord.default.url";
    private static final String DISCORD_ERROR_URL = "discord.error.url";
    private static final String ENABLED_DEFAULT = "discord.default.enabled";
    private static final String ENABLED_ERROR = "discord.error.enabled";

    public DiscordIntegration() {
        McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
        FileConfiguration config = plugin.getConfig();
        if (config.contains("discord") && config.getBoolean(ENABLED_DEFAULT) && Objects.requireNonNull(config.getString(DISCORD_URL)).length() >= 6) {
            this.defaultHandler = new DiscordHandler(config.getString(DISCORD_URL));
            plugin.getLogger().info("ENABLED DISCORD HOOK");
            this.defaultHandler.send(String.format("```Welcome to RaspiChat // Protocol version:%s```", plugin.getRandom().nextInt(100000)));
        }
        if (config.contains("discord") && config.getBoolean(ENABLED_ERROR) && Objects.requireNonNull(config.getString(DISCORD_ERROR_URL)).length() >= 6) {
            this.errorHandler = new DiscordHandler(config.getString(DISCORD_ERROR_URL));
            plugin.getLogger().info("ENABLED DISCORD ERROR HOOK");

        }

    }


    public void sendError(Class<?> currentClass, Exception e) {
        StringBuilder builder = new StringBuilder("```Error in class: ").append(currentClass.getSimpleName());
        builder.append("\n").append(e.toString()).append("\n");
        for (StackTraceElement element : e.getStackTrace()) {
            if (element.toString().startsWith("Goodys")) {
                builder.append("\nat:").append(" ").append(element.toString().replaceAll("GoodysUtilities.jar//", ""));
            }
        }

        if (e.getCause() != null) {
            builder.append("Caused by:\n").append(e.getCause());
        }
        builder.append("```");
        errorHandler.send(builder.toString());
    }

    public void send(String message) {
        if (defaultHandler != null) {
            defaultHandler.send(message);
        }
    }

}
