package eu.goodyfx.mcraspisystem.commands;


import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.managers.PrefixManager;
import eu.goodyfx.mcraspisystem.utils.OldColors;
import eu.goodyfx.mcraspisystem.utils.PlayerNameController;
import eu.goodyfx.mcraspisystem.utils.RaspiMessages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PrefixCommand implements CommandExecutor {

    private final McRaspiSystem plugin;
    private final PlayerNameController playerNameController;
    private final PrefixManager prefixManager;
    private final RaspiMessages data;

    public PrefixCommand(McRaspiSystem plugin) {
        this.data = plugin.getModule().getRaspiMessages();
        this.plugin = plugin;
        this.playerNameController = plugin.getModule().getPlayerNameController();
        this.prefixManager = plugin.getModule().getPrefixManager();
        plugin.setCommand("prefix", this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player) {
            if (args.length == 0) {
                //remove prefix
                if (prefixManager.exist(player)) {
                    prefixManager.remove(player);
                    player.sendRichMessage(data.getPrefix() + "<gray>Dein Prefix wurde entfernt.");
                } else {
                    player.sendRichMessage(data.getPrefix() + "<red>Du hast bisher kein Prefix! " + "<white>[<green><click:suggest_command:'/prefix '>" + "<hover:show_text:'<green>Klicke um deinen Prefix zu setzen.'>+<white>]<reset>");
                }
                playerNameController.setPlayerList(player);
                return true;
            }

            StringBuilder message = new StringBuilder();
            for (String arg : args) {
                message.append(arg).append("@");
            }

            message.setLength(message.length() - 1);

            if (OldColors.getRawString(message.toString()).length() > plugin.getConfig().getInt("prefix.length")) {
                player.sendRichMessage(data.getPrefix() + "<red>Dein Prefix überschreitet die Maximal eingestellte länge. (<red>" + message.length() + "<gray>/<green>" + plugin.getConfig().getInt("prefix.length") + "<red>)");
                player.sendRichMessage(data.getPrefix() + "<gray><i>Versuche es bitte erneut. <click:suggest_command:'/prefix '><underlined><aqua>Klick Mich<reset>");

                return true;
            }


            String messageConverted = message.toString();
            messageConverted = messageConverted.replace("<obf", "");
            messageConverted = OldColors.convert(messageConverted);
            prefixManager.set(player, messageConverted);
            playerNameController.setPlayerList(player);
            player.sendRichMessage(data.getPrefix() + "<green>Dein Prefix wurde gesetzt. (" + OldColors.getRawString(messageConverted).length() + "/" + plugin.getConfig().getInt("prefix.length") + ")");
            return true;
        }
        return true;
    }
}
