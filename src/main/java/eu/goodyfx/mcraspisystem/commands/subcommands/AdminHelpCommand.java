package eu.goodyfx.mcraspisystem.commands.subcommands;

import eu.goodyfx.goodysutilities.commands.AdminCommand;
import eu.goodyfx.goodysutilities.commands.SubCommand;
import eu.goodyfx.goodysutilities.utils.Data;
import eu.goodyfx.goodysutilities.utils.RaspiPlayer;

public class AdminHelpCommand extends SubCommand {

    private final Data data;

    private final AdminCommand mainCommand;

    public AdminHelpCommand(Data data, AdminCommand mainCommand) {
        this.data = data;
        this.mainCommand = mainCommand;
    }

    @Override
    public String getLabel() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Command to get List of Help.";
    }

    @Override
    public String getSyntax() {
        return "/admin help";
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {

        if (args.length == 1) {
            StringBuilder builder = new StringBuilder("SubCommands:").append(" ");
            for (SubCommand subCommand : mainCommand.getSubCommands()) {
                builder.append(subCommand.getLabel()).append(",").append(" ");
            }

            builder.setLength(builder.length() - 2);
            player.sendMessage(data.getPrefix() + builder);
        }
        return true;
    }
}
