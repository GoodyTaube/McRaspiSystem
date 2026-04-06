package eu.goodyfx.system.core.events;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.database.RaspiPlayer;
import eu.goodyfx.system.core.database.RaspiUser;
import eu.goodyfx.system.core.managers.LocationManager;
import eu.goodyfx.system.core.managers.RequestManager;
import eu.goodyfx.system.core.managers.WarteschlangenManager;
import eu.goodyfx.system.core.utils.PlayerTime;
import eu.goodyfx.system.core.utils.RaspiFormatting;
import eu.goodyfx.system.core.utils.RaspiMessages;
import eu.goodyfx.system.core.utils.RaspiPermission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

public class RaspiPlayerConnectionEvents implements Listener {

    //TODO HANDLE NEWBIE UND CO SPÄTER

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    private final RaspiMessages data = plugin.getModule().getRaspiMessages();
    private final WarteschlangenManager warteschlange = plugin.getModule().getWarteschlangenManager();
    private final NamespacedKey joinErrorKey = plugin.getNameSpaced("joinError");



    public RaspiPlayerConnectionEvents() {
        plugin.setListeners(this);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoined(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        LocationManager manager = plugin.getModule().getLocationManager();
        checkSystemLocationsExists(player, manager);

        //Check if joining Player joins in Active World
        warteschlange.join(raspiPlayer);
        warteschlange.setHeader();
        //REQUEST
        playerJoinEvent.joinMessage(null);

        plugin.getHookManager().getDiscordIntegration().send(String.format("`[System] <%s> ist zurückgekehrt.`", player.getName()));
        plugin.getModule().getItemConverterManager().convert(player.getInventory());

    }


    private void checkSystemLocationsExists(Player player, LocationManager manager) {
        //Check if Locations Exist
        if (player.isOp()) {
            if (!manager.exist("warteraum")) {
                player.sendRichMessage(data.noSpawnPoint(2));
            }
            if (!manager.exist("spawn")) {
                player.sendRichMessage(data.noSpawnPoint(1));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent playerQuitEvent) {
        //Quit Player

    }


    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        event.motd(plugin.getModule().getMotdManager().getMessage());
    }

}
