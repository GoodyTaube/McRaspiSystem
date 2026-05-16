package eu.goodyfx.system.core.utils;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.database.RaspiPlayer;
import eu.goodyfx.system.core.database.RaspiUser;
import eu.goodyfx.system.core.managers.RequestManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
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

public class PlayerJoinTasks {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    private final NamespacedKey joinErrorKey = plugin.getNameSpaced("joinError");
    private final RequestManager requestManager = plugin.getModule().getRequestManager();

    private final List<UUID> unverifiedPlayers = new ArrayList<>();

    private static final String REQUEST_MESSSAGE = """
            
            <red>⚠ %2$s <gray>ist noch nicht freigeschaltet.
            
            <green>➤ <click:run_command:'/request accept %1$s'><hover:show_text:'<gray>Freischaltung bestätigen <gray><i>(1 Click)'><green>Freischalten</hover></click>
            
            <red>➤ <click:suggest_command:'/request deny %1$s '><hover:show_text:'<gray>Anfrage ablehnen <gray><i>(Grund angeben)'><red>Ablehnen</hover></click>
            """;

    private static final String REQUEST_MESSSAGE_DENIED = """
            
            <red>⚠ %2$s <gray>wurde bereits Abgelehnt.
            
            <green>➤ <click:run_command:'/request accept %1$s'><hover:show_text:'<gray>%1$s Freischalten <gray><i>(1 Click)'><green>Doch Freischalten</hover></click>
            
            <red>➤ <click:run_command:'/request kick %1$s'><hover:show_text:'<gray>%1$s Kicken<gray><i> (1 Click)'><red>Spieler Kicken</hover></click>
            
            <gold>➤ <click:suggest_command:'/request ban %1$s '><hover:show_text:'<gray>%1$s 3h Sperren<gray><i> (Grund Angeben)'>Spieler Sperren</hover></click>
            """;


    private static final String NO_TEAM_MESSAGE = "Schön, dass du da bist!<br> Momentan ist kein Teammitglied verfügbar. Wir bemühen uns, deine Anfrage zeitnah zu bearbeiten.";

    private static final String BAN_MESSAGE = """
            <dark_red><b>Du wurdest auf mcraspi.com gesperrt!<reset>
            
            <gray>Grund: <red>' %1$s '
            
            <gray>Entsperrung: <red>%2$s
            
            <gray><i>Bitte beachte, dass diese Entscheidung <underlined>nicht</underlined> angefochten werden kann.
            
            """;


    public boolean perform(RaspiPlayer raspiPlayer, Map<UUID, PlayerTime> container) {

        Player player = raspiPlayer.getPlayer();


        if (raspiPlayer.userManagement().isBanned()) {
            String reason = raspiPlayer.userManagement().getBan_message();
            reason = reason.replace("@", " ");
            long expire = raspiPlayer.userManagement().getBan_expire();

            if (System.currentTimeMillis() >= expire) {
                raspiPlayer.userManagement().performUnban();
            } else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
                player.kick(MiniMessage.miniMessage().deserialize(String.format(BAN_MESSAGE, reason, simpleDateFormat.format(expire))));
                return false;
            }
        }


        if ((raspiPlayer.userData().getAllowed() == null) || (player.getPersistentDataContainer().has(joinErrorKey))) {
            spielerNeu(player);
        }
        raspiRequest(raspiPlayer);
        container.put(player.getUniqueId(), new PlayerTime(player));
        welcomeMessage(raspiPlayer);
        Bukkit.getOnlinePlayers().forEach(onlinePLayer -> {
            onlinePLayer.sendRichMessage(RaspiFormatting.formattingChatMessage(plugin.getModule().getJoinMessageManager().get(raspiPlayer)));
        });

        if ((!player.isPermissionSet("system.bypass") && (raspiPlayer.hasTimePlayed(100)))) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " permission set system.bypass");
        }
        return true;
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
            Raspi.playerLifeCycleService().getRaspiTeamPlayers().forEach(player1 -> player1.sendMessage(String.format("<red><i>%s hat kein Willkommensbuch bekommen!<reset> <yellow>FEHLER:[404]:: %s NOT FOUND!", player.getName(), fileName), true));

            container.set(joinErrorKey, PersistentDataType.INTEGER, 1);
            return;
        } else {
            if (container.has(joinErrorKey)) {
                Raspi.playerLifeCycleService().getRaspiTeamPlayers().forEach(team -> team.sendMessage(String.format("<gray><i>Versuche das Willkommensbuch für %s erneut zu erstellen.", player.getName()), true));
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
                Raspi.playerLifeCycleService().getRaspiTeamPlayers().forEach(team -> team.sendMessage(String.format("<green><i>Willkommensbuch für %s erfolgreich übermittelt!", player.getName()), true));
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
                    line = line.replace("{player}", player.getColorName());
                    player.sendMessage(line);
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "willkommen.txt konnte nicht gefunden werden.");
            }
        }
    }

    /**
     * Handle RaspiPlayer join
     *
     * @param raspiPlayer Player who Joined
     */
    private void raspiRequest(RaspiPlayer raspiPlayer) {
        //raspiPlayer.sendMessage(String.format(REQUEST_MESSSAGE_DENIED, raspiPlayer.getPlayer().getName(), raspiPlayer.getColorName()));

        //MOD Join Handling
        if (isTeam(raspiPlayer)) {
            if (unverifiedPlayers.isEmpty()) {
                return;
            }
            for (UUID uuid : unverifiedPlayers) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    unverifiedPlayers.remove(uuid);
                    continue;
                }
                raspiPlayer.sendMessage(String.format(REQUEST_MESSSAGE, player.getName(), player.getName()));
            }
            return;
        }

        //Checkt ob der Spieler bereits angenommen wurde
        handleNewbie(raspiPlayer);
        //Was passiert mit einem Spieler, welcher abgelehnt wurde
        handleBlocked(raspiPlayer);
    }


    /**
     * Handle a RaspiPlayer if he is Blocked.
     * Send Message to Mod if blocked user.
     *
     * @param raspiPlayer The Tested Player
     */
    private void handleBlocked(RaspiPlayer raspiPlayer) {
        Player player = raspiPlayer.getPlayer();
        RaspiUser user = raspiPlayer.userData();
        if (requestManager.isBlocked(user)) {
            List<RaspiPlayer> mods = getModPlayers();
            mods.forEach(mod -> {
                mod.sendMessage(String.format(REQUEST_MESSSAGE_DENIED, player.getName(), raspiPlayer.getColorName()));
            });
        }
    }

    /**
     * Check if Player is new and Send Info to MODS if so
     *
     * @param player The Player to Check
     */
    private void handleNewbie(RaspiPlayer player) {
        if (player.userData().getAllowed() == null) {
            if (player.hasPermission("group.spieler")) {
                //Spieler bereits freigeschaltet interne Verarbeitung, freischaltung setzten
                RaspiUser targetPlayer = player.userData();
                targetPlayer.setAllowed(true);
                targetPlayer.setAllowed_since(new SimpleDateFormat("dd/MM/yyyy").format(new Date(System.currentTimeMillis())));
                targetPlayer.setAllowed_by("SYSTEM");
                targetPlayer.setDeny_reason(null);
                targetPlayer.setDenied_by(null);
                plugin.getLogger().info("[PLAYER ALLOW_STATE RECOVER]:: ALLOWED :: " + targetPlayer.getUsername() + " BY SYSTEM");
                return;
            }

            List<RaspiPlayer> mods = getModPlayers();

            if (!mods.isEmpty()) {
                //Information an alle Mods mit klickbaren Message OPTIONEN
                mods.forEach(moderator -> {
                    moderator.sendMessage(String.format(REQUEST_MESSSAGE, player.getPlayer().getName(), player.getColorName()));
                });
                return;
            }

            unverifiedPlayers.add(player.getUUID());
            player.sendMessage(NO_TEAM_MESSAGE, true);
        }
    }

    public boolean isTeam(RaspiPlayer raspiPlayer) {
        return getModPlayers().contains(raspiPlayer);
    }

    public List<RaspiPlayer> getModPlayers() {
        return Raspi.playerLifeCycleService().getRaspiPlayersWHP(RaspiPermission.MOD);
    }

}
