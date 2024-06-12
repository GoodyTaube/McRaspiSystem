package eu.goodyfx.mcraspisystem.utils;

import org.bukkit.Bukkit;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class DiscordHandler {

    private URL webHookURL = null;

    public DiscordHandler(String webHookURL) {
        try {
            this.webHookURL = new URL(webHookURL);
        } catch (MalformedURLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "URL cannot be converted! :: " + webHookURL, e);
        }
    }

    public void send(String message) {
        if (this.webHookURL != null) {
            try {
                final HttpsURLConnection connection = (HttpsURLConnection) webHookURL.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11");
                connection.setDoOutput(true);
                try (final OutputStream outputStream = connection.getOutputStream()) {
                    // Handle backslashes.
                    String preparedMessage = message.replaceAll("\\\\", "");
                    if (preparedMessage.endsWith(" *"))
                        preparedMessage = preparedMessage.substring(0, preparedMessage.length() - 2) + "*";

                    outputStream.write(("{\"content\":\"" + preparedMessage + "\"}").getBytes(StandardCharsets.UTF_8));
                }
                connection.getInputStream();
            } catch (final IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Error while Handle Discord WebHook", e);
            }
        }
    }

}
