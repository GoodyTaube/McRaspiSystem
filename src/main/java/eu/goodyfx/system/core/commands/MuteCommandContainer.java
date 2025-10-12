package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class MuteCommandContainer {

    public static LiteralCommandNode<CommandSourceStack> muteCommand() {
        return Commands.literal("mute").executes(context -> {
            context.getSource().getSender().sendRichMessage("<gray>Bitte nutze: <yellow>/mute <player> <grund>");
            return Command.SINGLE_SUCCESS;
        }).then(Commands.argument("player", StringArgumentType.string()).then(Commands.argument("reason", StringArgumentType.string()).executes(MuteCommandContainer::execute))).build();
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        //TODO NEWBIE CHECK
        if (!(context.getSource().getSender() instanceof Player player)) {
            return Command.SINGLE_SUCCESS;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(StringArgumentType.getString(context, "player"));
        String reason = StringArgumentType.getString(context, "reason");
        String formatted = reason.replace(" ", "@");
        if (target.isOnline()) {
            RaspiPlayer targetOnline = Raspi.players().get(target.getPlayer());
            targetOnline.mute(player, formatted);
            //TODO MUTE USER
            return Command.SINGLE_SUCCESS;
        }
        Raspi.players().getRaspiOfflinePlayer(target).thenAcceptAsync(raspiOfflinePlayer -> {
            RaspiPlayer raspiPlayer = Raspi.players().get(player);
            if(raspiOfflinePlayer == null){
                player.sendRichMessage("<red>Der Spieler spielte nicht ;I ");
                return;
            }
            if(raspiOfflinePlayer.getManagement().isMuted()){
                raspiPlayer.sendMessage("<red>Der Spieler ist bereits stummgeschaltet!", true);
                return;
            }
            raspiOfflinePlayer.getManagement().setMuted(true);
            raspiOfflinePlayer.getManagement().setMute_owner(player.getName());
            raspiOfflinePlayer.getManagement().setMute_message(formatted);
            raspiPlayer.sendMessage(String.format("Du hast %s erfolgreich stummgeschaltet!", raspiOfflinePlayer.getRaspiUser().getColor() +raspiOfflinePlayer.getRaspiUser().getUsername()), true);
        });
        return Command.SINGLE_SUCCESS;
    }

}
