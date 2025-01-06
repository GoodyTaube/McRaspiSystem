package eu.goodyfx.mcraspisystem.commands.subcommands;

import eu.goodyfx.mcraspisystem.commands.AdminCommand;
import eu.goodyfx.mcraspisystem.commands.SubCommand;
import eu.goodyfx.mcraspisystem.utils.RaspiMessages;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;

public class AdminHelpCommand extends SubCommand {

    private final RaspiMessages data;

    private final AdminCommand mainCommand;

    public AdminHelpCommand(RaspiMessages data, AdminCommand mainCommand) {
        this.data = data;
        this.mainCommand = mainCommand;
    }

    @Override
    public String getLabel() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Ein Command um eine Liste von Allen Commands zu bekommen.";
    }

    @Override
    public String getSyntax() {
        return "/admin help";
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {

        if (args.length == 1) {
            player.sendMessage("<gray><italic>Um eine Detaillierte Beschreibung pro subCommand zu bekommen, nutze bitte: /admin <subCommand>");
            StringBuilder builder = new StringBuilder("Alle subCommands:").append(" ");
            for (SubCommand subCommand : mainCommand.getSubCommands()) {
                builder.append(subCommand.getLabel()).append(",").append(" ");
            }

            builder.setLength(builder.length() - 2);
            player.sendMessage(builder.toString());
        }
        return true;
    }
}
