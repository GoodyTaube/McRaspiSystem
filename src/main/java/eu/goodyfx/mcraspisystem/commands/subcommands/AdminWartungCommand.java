package eu.goodyfx.mcraspisystem.commands.subcommands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.SubCommand;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;

public class AdminWartungCommand extends SubCommand {
    private final McRaspiSystem plugin;

    public AdminWartungCommand(McRaspiSystem plugin){
        this.plugin = plugin;
    }

    @Override
    public String getLabel() {
        return "wartung";
    }

    @Override
    public String getDescription() {
        return "Stop the server save";
    }

    @Override
    public String getSyntax() {
        return "/admin wartung";
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if(args.length == 1){

        }
        return false;

    }
}
