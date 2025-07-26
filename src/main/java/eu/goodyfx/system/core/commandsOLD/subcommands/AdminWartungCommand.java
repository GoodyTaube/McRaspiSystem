package eu.goodyfx.system.core.commandsOLD.subcommands;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.SubCommand;
import eu.goodyfx.system.core.utils.RaspiPlayer;

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
    public int length() {
        return 0;
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if(args.length == 1){

        }
        return false;

    }
}
