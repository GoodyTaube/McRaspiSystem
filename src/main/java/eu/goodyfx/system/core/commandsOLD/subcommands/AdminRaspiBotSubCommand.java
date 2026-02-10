package eu.goodyfx.system.core.commandsOLD.subcommands;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.database.RaspiPlayer;
import eu.goodyfx.system.core.utils.SubCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class AdminRaspiBotSubCommand extends SubCommand {
    @Override
    public String getLabel() {
        return "raspibot";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return "admin raspibot [reconnect]";
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if (args.length == 2 && args[1].equalsIgnoreCase("reconnect")) {
            player.sendActionBar("Reconnect Discord Bot!");
            JavaPlugin.getPlugin(McRaspiSystem.class).getDiscordBot().reconnect();
        }
        return false;

    }
}
