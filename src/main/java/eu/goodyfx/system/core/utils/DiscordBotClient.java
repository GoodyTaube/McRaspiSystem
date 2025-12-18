package eu.goodyfx.system.core.utils;

import eu.goodyfx.system.McRaspiSystem;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.logging.Level;

@Getter
public class DiscordBotClient extends WebSocketClient {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    public boolean enabled = true;

    public DiscordBotClient(URI server) {
        super(server);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        plugin.getLogger().info("Verbunden mit RaspiBot!");
    }

    @Override
    public void onMessage(String s) {
        plugin.getLogger().info("Nachricht empfangen! " + s);
        for (RaspiPlayer raspiPlayer : Raspi.players().getRaspiPlayers()) {
            raspiPlayer.sendMessage("<white>[<aqua>Discord<white>] <gray>" + s);
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        plugin.getLogger().info("Verbindung zum RaspiBot getrennt! [" + s + "]");
    }

    @Override
    public void onError(Exception e) {
        enabled = false;
        plugin.getLogger().log(Level.SEVERE, "Error From DC_BOT", e);
    }
}
