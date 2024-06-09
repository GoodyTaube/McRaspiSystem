package eu.goodyfx.mcraspisystem.commands.subcommands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.SubCommand;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;

public class WarteschlangeCommandReload extends SubCommand {

    private final McRaspiSystem system;

    public WarteschlangeCommandReload(McRaspiSystem system) {
        this.system = system;
    }

    @Override
    public String getLabel() {
        return "reload";
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
        if (args.length == 1) {
            system.reloadConfig();
            system.getModule().getWarteschlangenManager().setHeader();
            player.getPlayer().sendRichMessage(system.getModule().getRaspiMessages().getPrefix() + "<green>Die Config Datei wurde neu Geladen!");
            return true;
        }
        return false;
    }
}
