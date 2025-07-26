package eu.goodyfx.system.core.commandsOLD;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.PlayerNameController;
import eu.goodyfx.system.core.utils.RaspiMessages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MessageCommand implements CommandExecutor, TabCompleter {

    private final RaspiMessages data;
    private final PlayerNameController playerNameController;
    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    public MessageCommand(McRaspiSystem plugin) {
        this.data = plugin.getModule().getRaspiMessages();
        this.playerNameController = plugin.getModule().getPlayerNameController();
        plugin.setCommand("message", this, this);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (command.getName().equalsIgnoreCase("message")) {
            if (args.length == 1) {
                return null;
            }
        }
        return List.of();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0 || args.length == 1) {
                return false;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                StringBuilder messageBuilder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    messageBuilder.append(args[i]).append(" ");
                }

                String finalMessage = messageBuilder.toString();


                finalMessage = finalMessage.replace("<hover:", "");
                finalMessage = finalMessage.replace("<click:", "");
                finalMessage = finalMessage.replace("<br>", "");
                finalMessage = finalMessage.replace("<obf>", "");
                finalMessage = finalMessage.replace("<obfuscated>", "");
                finalMessage = finalMessage.replace("<rainbow>", "");

                player.sendRichMessage("<gray><i>Du flüsterst " + getName(target) + " <gray><i>zu: " + finalMessage);
                target.sendRichMessage(getName(player) + " <gray><i>flüstert dir zu: " + finalMessage);
            } else {
                player.sendRichMessage(data.playerNotOnline(args[0]));
            }

            return true;
        } else {
            if (args.length == 0 || args.length == 1) {
                return false;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                StringBuilder messageBuilder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    messageBuilder.append(args[i]).append(" ");
                }

                String finalMessage = messageBuilder.toString();


                finalMessage = finalMessage.replace("<hover:", "");
                finalMessage = finalMessage.replace("<click:", "");
                finalMessage = finalMessage.replace("<br>", "");
                finalMessage = finalMessage.replace("<obf>", "");
                finalMessage = finalMessage.replace("<obfuscated>", "");
                finalMessage = finalMessage.replace("<rainbow>", "");
                sender.sendRichMessage("<gray><i>Du flüsterst " + getName(target) + " <gray><i>zu: " + finalMessage);
                target.sendRichMessage("<rainbow:1>[SERVER] " + " <gray><i>flüstert dir zu: " + finalMessage);
            } else {
                sender.sendRichMessage(data.playerNotOnline(args[0]));
            }

            return true;
        }
    }

    /**
     * Get Clickable Target Name
     *
     * @param target The Target
     * @return ClickAble Target Name
     */
    private String getName(Player target) {
        return "<click:suggest_command:'/msg " + target.getName() + " '><hover:show_text:'<green>Antworten'>" + playerNameController.getName(target) + "<reset>";
    }

}
