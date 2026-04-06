package eu.goodyfx.system.core.utils;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.database.RaspiPlayer;
import eu.goodyfx.system.core.database.RaspiUser;
import eu.goodyfx.system.core.managers.RequestManager;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerJoinTasks {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    private final NamespacedKey joinErrorKey = plugin.getNameSpaced("joinError");
    private final RequestManager requestManager = plugin.getModule().getRequestManager();


    public void perform(RaspiPlayer raspiPlayer, Map<UUID, PlayerTime> container) {

        Player player = raspiPlayer.getPlayer();

        if (!(player.hasPlayedBefore()) || (player.getPersistentDataContainer().has(joinErrorKey))) {
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
     * @param raspiPlayer Player to Handle
     */
    private void raspiRequest(RaspiPlayer raspiPlayer) {
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
            Raspi.playerLifeCycleService().getRaspiPlayersWHP(RaspiPermission.MOD).forEach(team -> {
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

    /**
     * Check if Player is new and Send Info to MODS if so
     *
     * @param player The Player to Check
     */
    private void handleNewbie(RaspiPlayer player) {
        if (player.userData().getState() == null) {
            if (player.hasPermission("group.spieler")) {
                //Spieler bereits freigeschaltet interne Verarbeitung, freischaltung setzten
                RaspiUser targetPlayer = player.userData();
                targetPlayer.setState(true);
                targetPlayer.setAllowed_since(new SimpleDateFormat("dd/MM/yyyy").format(new Date(System.currentTimeMillis())));
                targetPlayer.setAllowed_by("SYSTEM");
                targetPlayer.setDeny_reason(null);
                targetPlayer.setDenied_by(null);
                plugin.getLogger().info("[PLAYER ALLOW_STATE RECOVER]:: ALLOWED :: " + targetPlayer.getUsername() + " BY SYSTEM");
                return;
            }
            List<RaspiPlayer> teams = Raspi.playerLifeCycleService().getRaspiPlayersWHP(RaspiPermission.MOD);


            if (!teams.isEmpty()) {
                teams.forEach(moderator -> {
                    moderator.sendMessage(String.format("<gray><italic>%s ist noch nicht Registriert!", player.getPlayer().getName()), true);
                    moderator.sendMessage(String.format("<white>[<green>%s<white>] [<red>%s<white>]", String.format("<click:run_command:'/request accept %s'>Annehmen<reset>", player.getPlayer().getName()), String.format("<click:run_command:'request deny %1$2s -request_start'>Ablehnen <gray>(<green><click:suggest_command:'/request deny %1$2s'>+<reset><gray>)", player.getPlayer().getName())), true);
                });
            }
        }
    }
}
