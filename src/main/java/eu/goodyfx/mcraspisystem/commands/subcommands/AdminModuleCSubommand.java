package eu.goodyfx.mcraspisystem.commands.subcommands;

import eu.goodyfx.goodysutilities.GoodysUtilities;
import eu.goodyfx.goodysutilities.commands.SubCommand;
import eu.goodyfx.goodysutilities.managers.ModuleManager;
import eu.goodyfx.goodysutilities.utils.RaspiPlayer;

public class AdminModuleCSubommand extends SubCommand {

    private final GoodysUtilities plugin;

    public AdminModuleCSubommand(GoodysUtilities plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getLabel() {
        return "module";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if (args.length == 3 && (args[2].equalsIgnoreCase("reload"))) {

            ModuleManager manager = plugin.getModuleManager();

            manager.disable(args[1]);
            player.sendMessage(plugin.getData().getPrefix() + args[1] + " wurde Deaktiviert.");
        }
        return false;
    }
}
