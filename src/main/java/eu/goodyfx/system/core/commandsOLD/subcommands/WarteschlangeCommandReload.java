package eu.goodyfx.system.core.commandsOLD.subcommands;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.SubCommand;
import eu.goodyfx.system.core.utils.RaspiPlayer;

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
    public int length() {
        return 0;
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
