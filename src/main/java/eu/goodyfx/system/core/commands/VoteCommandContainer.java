package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class VoteCommandContainer {

    private static final String VOTE_LINK_PATH = "Utilities.voteLinks";

    public static LiteralCommandNode<CommandSourceStack> voteCommand(McRaspiSystem plugin) {
        return Commands.literal("vote").requires(cont -> cont.getSender() instanceof Player).executes(context -> {
            Entity entity = context.getSource().getExecutor();
            if (!(entity instanceof Player player)) {
                return Command.SINGLE_SUCCESS;
            }
            RaspiPlayer raspiPlayer = plugin.getRaspiPlayer(player);

            if (plugin.getConfig().contains(VOTE_LINK_PATH)) {
                List<String> voteLinks = plugin.getConfig().getStringList(VOTE_LINK_PATH);
                raspiPlayer.sendMessage("<green>McRaspi braucht deine Unterstützung!", true);
                String linkMessage = "<dark_gray> - <blue>%s";

                for (String voteLink : voteLinks) {
                    if (voteLink.contains(" ")) {
                        String[] voteLinkComp = voteLink.split(" ");
                        raspiPlayer.sendMessage(String.format(linkMessage, raspiPlayer.convertLink(voteLinkComp[1], voteLinkComp[0])));
                        continue;
                    }
                    raspiPlayer.sendMessage(String.format(linkMessage, raspiPlayer.convertLink(voteLink)));
                }
            } else {
                raspiPlayer.sendMessage("<red>Momentan gibt es keine Vote Links.", true);
            }

            return Command.SINGLE_SUCCESS;
        }).build();
    }

}
