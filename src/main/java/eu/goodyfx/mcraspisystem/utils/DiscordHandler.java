package eu.goodyfx.mcraspisystem.utils;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class DiscordHandler {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    private URL webHookURL = null;


    public DiscordHandler(String webHookURL) {
        try {
            this.webHookURL = new URI(webHookURL).toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            String message = String.format("URL cannot be converted! :: %s", webHookURL);
            plugin.getLogger().log(Level.SEVERE, message, e);
        }
    }

    public void send(String message) {
        if (this.webHookURL != null) {
            try {
                final HttpsURLConnection connection = (HttpsURLConnection) webHookURL.openConnection();

                if (connection.getResponseCode() == 522) {
                    plugin.getLogger().info("====================================");
                    plugin.getLogger().info("Discord hat Massive Server Probleme!");
                    String mess = String.format("Chat-Nachicht: %s konnte nicht Gesendet werden", message);
                    plugin.getLogger().info(mess);
                    plugin.getLogger().info(connection.getResponseMessage());
                    plugin.getLogger().info("====================================");
                    return;
                }


                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11");
                connection.setDoOutput(true);
                try (final OutputStream outputStream = connection.getOutputStream()) {
                    // Handle backslashes.
                    String preparedMessage = message.replace("\\\\", "");
                    if (preparedMessage.endsWith(" *"))
                        preparedMessage = preparedMessage.substring(0, preparedMessage.length() - 2) + "*";

                    outputStream.write(("{\"content\":\"" + preparedMessage + "\"}").getBytes(StandardCharsets.UTF_8));
                }
                connection.getInputStream();

            } catch (final IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while Handle Discord WebHook", e);
            }
        }
    }

}
