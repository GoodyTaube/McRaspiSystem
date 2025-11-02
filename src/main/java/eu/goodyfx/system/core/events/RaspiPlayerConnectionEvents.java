package eu.goodyfx.system.core.events;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.database.RaspiUser;
import eu.goodyfx.system.core.managers.LocationManager;
import eu.goodyfx.system.core.managers.RequestManager;
import eu.goodyfx.system.core.managers.WarteschlangenManager;
import eu.goodyfx.system.core.utils.*;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class RaspiPlayerConnectionEvents implements Listener {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    private final RaspiMessages data = plugin.getModule().getRaspiMessages();
    private final WarteschlangenManager settings = plugin.getModule().getWarteschlangenManager();
    private final RequestManager requestManager = plugin.getModule().getRequestManager();
    private final NamespacedKey joinErrorKey = plugin.getNameSpaced("joinError");

    private final Map<UUID, PlayerTime> timeContainer = new HashMap<>();

    public RaspiPlayerConnectionEvents() {
        plugin.setListeners(this);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoined(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        RaspiPlayer raspiPlayer = Raspi.players().get(player);
        LocationManager manager = plugin.getModule().getLocationManager();
        checkSystemLocationsExists(player, manager);

        //Check if joining Player joins in Active World
        settings.join(raspiPlayer);
        settings.setHeader();
        //REQUEST

        if (!(player.hasPlayedBefore()) || (player.getPersistentDataContainer().has(joinErrorKey))) {
            spielerNeu(player);
        }

        raspiRequest(raspiPlayer);
        playerJoinEvent.joinMessage(null);
        Bukkit.getOnlinePlayers().forEach(all -> all.sendRichMessage(RaspiFormatting.formattingChatMessage(plugin.getModule().getJoinMessageManager().get(player))));

        if ((!player.isPermissionSet("system.bypass") && (raspiPlayer.hasTimePlayed(100)))) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " permission set system.bypass");
        }

        plugin.getHookManager().getDiscordIntegration().send(String.format("`[System] <%s> ist zurückgekehrt.`", player.getName()));

        timeContainer.put(player.getUniqueId(), new PlayerTime(player));

        welcomeMessage(Raspi.players().get(player));
        plugin.getModule().getItemConverterManager().convert(player.getInventory());

    }

    private void spielerNeu(Player player) {
        //TODO SPIER NEU LOGIC
        String path = "Utilities.firstJoinCommands.file";
        PersistentDataContainer container = player.getPersistentDataContainer();

        if (!plugin.getConfig().contains(path)) {
            return;
        }
        String fileName = plugin.getConfig().getString(path);
        assert fileName != null;
        if (!new File(plugin.getDataFolder(), fileName).exists()) {
            Raspi.players().getRaspiTeamPlayers().forEach(player1 -> player1.sendMessage(String.format("<red><i>%s hat kein Willkommensbuch bekommen!<reset> <yellow>FEHLER:[404]:: %s NOT FOUND!", player.getName(), fileName), true));

            container.set(joinErrorKey, PersistentDataType.INTEGER, 1);
            return;
        } else {
            if (container.has(joinErrorKey)) {
                Raspi.players().getRaspiTeamPlayers().forEach(team -> team.sendMessage(String.format("<gray><i>Versuche das Willkommensbuch für %s erneut zu erstellen.", player.getName()), true));
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(plugin.getDataFolder(), fileName), StandardCharsets.UTF_8))) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                line = line.replace("%player%", player.getName());
                if (!line.isEmpty()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), line);
                }
            }

            if (container.has(joinErrorKey) && player.getInventory().contains(new ItemStack(Material.WRITTEN_BOOK).getType())) {
                Raspi.players().getRaspiTeamPlayers().forEach(team -> team.sendMessage(String.format("<green><i>Willkommensbuch für %s erfolgreich übermittelt!", player.getName()), true));
                container.remove(joinErrorKey);
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while Handling commands.txt", e);
        }
    }

    private void welcomeMessage(RaspiPlayer player) {
        if (plugin.getConfig().contains("Utilities.welcome") && plugin.getConfig().getBoolean("Utilities.welcome")) {
            try (BufferedReader reader = new BufferedReader(new FileReader(new File(plugin.getDataFolder(), "willkommen.txt"), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.replace("{player}", player.getPlayer().getName());
                    player.sendMessage(line);
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "willkommen.txt konnte nicht gefunden werden.");
            }
        }
    }

    private void raspiRequest(RaspiPlayer raspiPlayer) {
        //Checkt ob der Spieler bereits angenommen wurde
        handleNewbie(raspiPlayer);
        //Was passiert mit einem Spieler, welcher abgelehnt wurde
        handleBlocked(raspiPlayer);
    }

    private void handleBlocked(RaspiPlayer raspiPlayer) {
        Player player = raspiPlayer.getPlayer();
        RaspiUser user = Raspi.players().get(player).getUser();
        if (requestManager.isBlocked(user)) {
            Raspi.players().getRaspiModPlayers().forEach(team -> {
                team.sendMessage(String.format("<gray><italic>%s wurde bereits von %s <gray><italic>Abgelehnt!", player.getName(), requestManager.getDeny(user)), true);
                if (requestManager.isBlocked(user)) {
                    team.sendMessage(String.format("<gray><italic>Grund: <yellow>%s", requestManager.getReason(user).replace("@", " ")), true);
                }
                String message = String.format("<white>[<red>%s<white>] [<green>%s<white>]", String.format("<click:run_command:'/request kick %s'>Kicken<reset>", player.getName()), String.format("<click:run_command:'/request accept %s'>Erlauben<reset>", player.getName()));
                String banMessage = String.format("// <white>[<gold><click:run_command:'/tempban %s RSP:6723 Überdenk Dein Leben --MOD'>Ban<reset><white>]", player.getName());
                //TODO SEND BAN BUTTON

                team.sendMessage(message, true);

            });
        }
    }

    private void handleNewbie(RaspiPlayer player) {
        if (player.getUser().getState() == null) {
            player.sendDebugMessage("State is null.");
            List<RaspiPlayer> teams = Raspi.players().getRaspiModPlayers();
            if (!teams.isEmpty()) {
                teams.forEach(moderator -> {
                    moderator.sendMessage(String.format("<gray><italic>%s ist noch nicht Registriert!", player.getPlayer().getName()), true);
                    moderator.sendMessage(String.format("<white>[<green>%s<white>] [<red>%s<white>]", String.format("<click:run_command:'/request accept %s'>Annehmen<reset>", player.getPlayer().getName()), String.format("<click:run_command:'request deny %1$2s -request_start'>Ablehnen <gray>(<green><click:suggest_command:'/request deny %1$2s'>+<reset><gray>)", player.getPlayer().getName())), true);
                    //TODO Check if MOD is AFK to trigger autoFreischaltung
                });
            } else {
                //   triggerAutoFreischalten(player);
            }

        }
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
        Player player = playerQuitEvent.getPlayer();
        if (timeContainer.containsKey(player.getUniqueId())) {
            PlayerTime playerTime = timeContainer.get(player.getUniqueId());
            playerTime.end(plugin.getModule().getTimeDBManager());
        }
    }


    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        event.motd(plugin.getModule().getMotdManager().getMessage());

    }

}
