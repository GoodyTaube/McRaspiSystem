package eu.goodyfx.system.core.utils;

import eu.goodyfx.system.McRaspiSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Level;

public class MojangPlayerWrapper {

    public static String getName(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer.getName() != null) {
            return offlinePlayer.getName();
        }
        String mojang = getNameFromUUID(uuid);
        if (mojang != null) {
            return mojang;
        }
        return "UNKNOWN";
    }

    public static String getNameFromUUID(UUID uuid) {
        McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
        try {

            String uuidStr = uuid.toString().replace("-", "");
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuidStr);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            if (httpURLConnection.getResponseCode() != 200) {
                return null; //Player not Found
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            //Raspi.debugger().info(response.toString());
            reader.close();
            String json = response.toString();

// Suche "name" flexibel
            int nameIndex = json.indexOf("\"name\"");
            if (nameIndex == -1) return null;

            int colonIndex = json.indexOf(":", nameIndex);
            int quoteStart = json.indexOf("\"", colonIndex);
            int quoteEnd = json.indexOf("\"", quoteStart + 1);

            return json.substring(quoteStart + 1, quoteEnd);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Failed to get name from %s", uuid), e);
            return null;
        }
    }

}
