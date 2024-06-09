package eu.goodyfx.mcraspisystem.commands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.managers.UserManager;
import eu.goodyfx.mcraspisystem.utils.RaspiMessages;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MuteCommand implements CommandExecutor {

    private final RaspiMessages data;
    private final McRaspiSystem plugin;
    private final UserManager userManager;

    public MuteCommand(McRaspiSystem plugin) {
        this.plugin = plugin;
        this.userManager = this.plugin.getModule().getUserManager();
        plugin.setCommand("mute", this);
        this.data = plugin.getModule().getRaspiMessages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player && args.length >= 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            if (!userManager.userExist(player)) {
                player.sendRichMessage("<red>Der User existiert nicht.");
                return true;
            }

            //Nachtrag zu Bernds Wunsch
            if (!player.isPermissionSet("group.OP")) { //Bypass Check
                //ASYNC
                isDefault(target.getUniqueId()).thenAcceptAsync(result -> {
                    if (Boolean.FALSE.equals(result)) {
                        player.sendRichMessage(data.getPrefix() + "<red>Der Spieler ist nicht 'Neu' und kann nicht mehr Stumm geschaltet werden!");
                    } else {
                        if (args.length == 1) {
                            if (userManager.isMuted(target)) {
                                userManager.unMuteUser(target);
                                Bukkit.getOnlinePlayers().forEach(all -> all.sendRichMessage(broadcastChatUnMute(player, target)));
                                return;
                            }
                            player.sendRichMessage(errorMessageReason(target));
                            return;
                        }
                        if (userManager.isMuted(target)) {
                            player.sendRichMessage(errorMessageAllReadyMuted(target));
                            return;
                        }

                        //Perform Actual Mute
                        StringBuilder reason = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            reason.append(args[i]).append("@"); //Build Reason for Database
                        }
                        reason.setLength(reason.length() - 1); //Remove last @ char
                        userManager.muteUser(target, player, reason.toString());
                        Bukkit.getOnlinePlayers().forEach(all -> all.sendRichMessage(broadcastChatMute(target, player, reason)));
                    }
                });
            } else {
                //Nachtrag ENDE
                userManager.reloadFile(); //Reload User Database
                if (args.length == 1) {
                    if (userManager.isMuted(target)) {
                        userManager.unMuteUser(target);
                        Bukkit.getOnlinePlayers().forEach(all -> all.sendRichMessage(broadcastChatUnMute(player, target)));
                        return true;
                    }
                    //Please the player to define Reason
                    player.sendRichMessage(errorMessageReason(target));
                    return true;
                }


                if (userManager.isMuted(target)) {
                    player.sendRichMessage(errorMessageAllReadyMuted(target));
                    return true;
                }

                //Perform Actual Mute

                StringBuilder reason = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    reason.append(args[i]).append("@"); //Build Reason for Database
                }
                reason.setLength(reason.length() - 1); //Remove last @ char
                userManager.muteUser(target, player, reason.toString());
                Bukkit.getOnlinePlayers().forEach(all -> all.sendRichMessage(broadcastChatMute(target, player, reason)));
            }
            return true;
        }
        return false;
    }


    public CompletableFuture<Boolean> isDefault(UUID userUID) {
        return plugin.getHookManager().getLuckPerms().getUserManager().loadUser(userUID)
                .thenApplyAsync(user -> {
                    Collection<Group> inheritedGroups = user.getInheritedGroups(user.getQueryOptions());
                    return inheritedGroups.stream().anyMatch(group -> group.getName().equalsIgnoreCase("default"));
                });
    }


    private String errorMessageReason(OfflinePlayer target) {

        String muteCommand = String.format("/mute %s", target.getName());

        return String.format("<red>Bitte vergib einen Grund!! %1$s <white>[%2$s<red>Beleidigung<white>] %3$s<white>[%4$s<red>Werbung<white>] %5$s[%6$s<red>+]",
                hoverText("<gray>Der Spieler hat im Chat Beleidigt."),
                runCommand(muteCommand + " Beleidigung"),
                hoverText("<gray>Der Spieler hat Werbung gemacht."),
                runCommand(muteCommand + " Werbung"),
                hoverText("<rainbow>Eigener Grund."),
                sugCommand(muteCommand + " "));
    }

    private String errorMessageAllReadyMuted(OfflinePlayer target) {
        return String.format("%1s<red>Der Spieler ist bereits Stumm geschaltet!<br><gray><italic>Nutze /mute %2s um den Spieler freizugeben!", data.getPrefix(), target.getName());

    }

    private String broadcastChatUnMute(Player player, OfflinePlayer target) {
        return String.format("%1s <red> %2s <gray>wurde von <yellow>%3s <gray>im Chat Freigegeben.", data.getPrefix(), target.getName(), player.getName());
    }

    private String broadcastChatMute(OfflinePlayer target, Player player, StringBuilder reason) {
        return String.format("%1s<red>%2s <gray>wurde von <red>%3s <gray>für: %4s <gray>Stumm geschaltet.", data.getPrefix(), target.getName(), player.getName(), reason.toString().replace("@", " "));
    }

    /**
     * Create Hover Message String with papers miniText API
     *
     * @param message The text to Show
     * @return Hover Message String
     */
    private String hoverText(String message) {
        return "<hover:show_text:'" + message + "'>";
    }

    /**
     * Create Message String with papers miniText API
     *
     * @param command The Command to Suggest
     * @return Chat Suggest Command String
     */
    private String sugCommand(String command) {
        return "<click:suggest_command:'" + command + "'>";
    }

    /**
     * Create Message String with papers miniText API
     *
     * @param command The Command to Run
     * @return Chat run Command String
     */
    private String runCommand(String command) {
        return "<click:run_command:'" + command + "'>";
    }


}
