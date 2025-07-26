package eu.goodyfx.system.core.utils;

import com.google.gson.JsonObject;
import eu.goodyfx.system.McRaspiSystem;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
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

    public void sendOLD(String message) {
        if (this.webHookURL != null) {
            try {
                HttpsURLConnection connection = (HttpsURLConnection) webHookURL.openConnection();
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
                InputStream inputStream = connection.getInputStream();
                inputStream.close();

            } catch (final IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while Handle Discord WebHook", e);
            }
        }
    }

    public void send(String message) {
        if (this.webHookURL != null) {
            try {
                HttpsURLConnection connection = (HttpsURLConnection) webHookURL.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "Java-DiscordWebhook");
                connection.setDoOutput(true);

                //JSON
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("content", message);
                try(OutputStream stream = connection.getOutputStream()){
                    stream.write(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
                }
                int response = connection.getResponseCode();
                if (response != 204) {
                    plugin.getLogger().warning("Webhook Antwortcode: " + response);
                    try (InputStream error = connection.getErrorStream()) {
                        if (error != null) {
                            String responseMessage = new String(error.readAllBytes(), StandardCharsets.UTF_8);
                            plugin.getLogger().warning("Fehlermeldung: " + responseMessage);
                        }
                    }
                }
            } catch (IOException e) {
                String exMessage = String.format("Error while sending %s", message);
                plugin.getLogger().log(Level.SEVERE, exMessage, e);
            }
        }
    }


}
