package eu.goodyfx.mcraspisystem.commands.subcommands;

import eu.goodyfx.goodysutilities.commands.SubCommand;
import eu.goodyfx.goodysutilities.utils.RaspiPlayer;

public class AdminSudoCommand extends SubCommand {


    @Override
    public String getLabel() {
        return "sudo";
    }

    @Override
    public String getDescription() {
        return "Command to perform Commands as Users";
    }

    @Override
    public String getSyntax() {
        return "/admin sudo <target:list> <command> [<args>]";
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {

        return true;
    }
}
