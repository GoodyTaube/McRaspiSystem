package eu.goodyfx.system.core.commandsOLD;


import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.OldColors;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class PrefixCommand implements CommandExecutor {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    public PrefixCommand() {
        plugin.setCommand("prefix", this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (sender instanceof Player dummy) {
            RaspiPlayer player = Raspi.players().get(dummy);
            if (args.length == 0) {
                if (player.getPrefix() != null) {
                    removePrefix(player);
                } else {
                    player.sendMessage("Du hast bisher keinen Prefix." + String.format("<white>[ <click:suggest_command:'/prefix '><hover:show_text:'Klicke um deinen Prefix zu setzten.'><green>%s <white>]<reset>", "Prefix Setzen"), true);
                }
                return true;
            }
            //Check if String is Valid
            if (stringCheck(args)) {
                String db_String = getDBString(args); //Convert args to DB_Sting (...@...@...)
                player.setPrefix(db_String);
                player.sendMessage(String.format("<gray>Dein Prefix ist nun <green>%s", db_String.replace("@", " ")), true);
            } else {
                player.sendMessage(String.format("<red>Dein Prefix ist zu lang! <yellow>%s<red>/<yellow>%s", prefixLength(args), plugin.getConfig().getInt("prefix.length")), true);
            }

        }
        return false;
    }

    private boolean stringCheck(@NotNull String[] args) {
        return prefixLength(args) <= plugin.getConfig().getInt("prefix.length");
    }

    private Integer prefixLength(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String arg : args) {
            stringBuilder.append(arg);
        }
        return OldColors.getRawString(stringBuilder.toString()).length();
    }

    private String getDBString(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String arg : args) {
            stringBuilder.append(arg).append("@");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);

        String message = stringBuilder.toString();
        message = OldColors.convert(message);
        message = message.replace("<obf", "");
        return message;
    }


    private void removePrefix(RaspiPlayer player) {
        //TODO update Tablist
        String prefix = player.getPrefix();
        player.removePrefix();
        player.sendMessage(String.format("<gray>Dein Prefix wurde entfernt. [%s]", prefix), true);

    }
}
