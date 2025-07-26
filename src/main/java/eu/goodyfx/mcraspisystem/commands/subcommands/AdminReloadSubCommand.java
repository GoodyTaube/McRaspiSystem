package eu.goodyfx.mcraspisystem.commands.subcommands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.SubCommand;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;

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
            plugin.getModule().getTraderDB().reload();
            player.sendMessage("Die RaspiConfigs wurden neu Geladen.", true);
            plugin.getDebugger().info(plugin.getConfig().getString("Utilities.vote"));
            plugin.getRaspiPlayers().forEach(player1 -> player.getPlayer().updateCommands());

        }
        return true;
    }
}
