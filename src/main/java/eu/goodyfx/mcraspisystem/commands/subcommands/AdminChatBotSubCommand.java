package eu.goodyfx.mcraspisystem.commands.subcommands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.SubCommand;
import eu.goodyfx.mcraspisystem.utils.ChatBot;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;

import java.io.IOException;
import java.util.logging.Level;

public class AdminChatBotSubCommand extends SubCommand {

    private final McRaspiSystem plugin;

    public AdminChatBotSubCommand(McRaspiSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getLabel() {
        return "chat";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return "<red>/admin chat <gray><frage>";
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if (args.length == 1) {
            player.sendMessage(getDescription());
            return true;
        }
        startQuest(player, args);
        return false;
    }

    private void startQuest(RaspiPlayer player, String[] args) {
        if (args.length > 1) {
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                builder.append(args[i]).append(" ");
            }
            builder.setLength(builder.length() - 1);
            try {
                ChatBot chatBot = new ChatBot();
                String resp = chatBot.querySerge(builder.toString());
                player.sendMessage(resp);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while Handle AI Chat.", e);
                player.sendMessage("<red>Server: Beim SprachBot ist ein Fehler aufgetreten.");
            }
        }
    }
}
