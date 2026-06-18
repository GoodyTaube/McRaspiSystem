package eu.goodyfx.mcraspi.core.commandsOLD.subcommands;

import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.core.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AdminReloadSubCommand extends SubCommand {

    private final McRaspiSystem plugin;

    public AdminReloadSubCommand(McRaspiSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getLabel() {
        return "reloadConfig";
    }

    @Override
    public String getDescription() {
        return "Läd die config.yml neu.";
    }

    @Override
    public String getSyntax() {
        return "/admin reload";
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if (args.length == 1) {
            plugin.reloadConfig();
            plugin.getModule().getMotdManager().reload();
            plugin.getModule().getRaspiGiveManager().reload();
            player.sendMessage("Die RaspiConfigs wurden neu Geladen.", true);
            plugin.getDebugger().info(plugin.getConfig().getString("Utilities.vote"));
            Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        }
        return true;
    }
}
