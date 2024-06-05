package eu.goodyfx.mcraspisystem.utils;

import eu.goodyfx.goodysutilities.GoodysUtilities;
import eu.goodyfx.mcraspisystem.McRaspiSystem;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;

@SuppressWarnings("unused")
public class DiscordIntegration {

    private URL url;

    private final McRaspiSystem plugin;

    private boolean connected = false;
    private boolean connectedError = false;

    private static final String DISCORD_URL = "discord.url";
    private static final String DISCORD_ERROR_URL = "discord.errorUrl";
    private static final String ENABLED = "discord.enabled";


    public DiscordIntegration(McRaspiSystem goodysUtilities, boolean isDefault) {
        this.plugin = goodysUtilities;
        if (isDefault) {
            if (goodysUtilities.getConfig().contains(DISCORD_URL)) {
                try {
                    this.url = new URL(Objects.requireNonNull(goodysUtilities.getConfig().getString(DISCORD_URL)));
                    String log = String.format("Webhook Enabled! Connecting to: %1s", goodysUtilities.getConfig().getString(DISCORD_URL));
                    Bukkit.getLogger().info(log);
                    connected = true;
                } catch (MalformedURLException ignore) {
                    connected = false;
                }

            }
        } else {
            if (goodysUtilities.getConfig().contains(DISCORD_ERROR_URL)) {
                try {
                    this.url = new URL(Objects.requireNonNull(goodysUtilities.getConfig().getString(DISCORD_ERROR_URL)));
                    String log = String.format("Webhook Enabled! Connecting to: %1s", goodysUtilities.getConfig().getString(DISCORD_ERROR_URL));
                    Bukkit.getLogger().info(log);
                    connectedError = true;
                } catch (MalformedURLException ignore) {
                    connectedError = false;
                }

            }

        }
    }


    public void sendError(Class<?> currentClass, Exception e) {
        if (!connectedError) {
            return;
        }
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
        sendDiscord(builder.toString());
    }


    public void sendDiscord(String message) {
        if (!connected) {
            Bukkit.getLogger().severe("Discord Connection Failed. (505)");
            return;
        }
        if (url == null) {
            return;
        }
        if (plugin.getConfig().contains("discord.enabled")) {
            if (!plugin.getConfig().getBoolean("discord.enabled")) {
                return;
            }
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("content", message);
            try {
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.addRequestProperty("Content-Type", "application/json");
                connection.addRequestProperty("User-Agent", "Java-Discord_WEB_HOOK");
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                OutputStream stream = connection.getOutputStream();
                stream.write(jsonObject.toJSONString().getBytes());
                stream.flush();
                stream.close();
                connection.getInputStream().close();
                connection.disconnect();
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Discord Fail...", e);
            }
        }
    }

}
