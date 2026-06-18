package eu.goodyfx.mcraspi.core.commands.admin.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.commands.RaspiSubCommand;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.core.utils.RaspiMessages;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CoinsSubCommand implements RaspiSubCommand {
    @Override
    public ArgumentBuilder<CommandSourceStack, ?> build() {
        return Commands.literal("coins").then(Commands.argument("spieler", StringArgumentType.string()).suggests(suggestOffline()).then(Commands.argument("wert", IntegerArgumentType.integer(1, 99999)).executes(this::execute)));
    }

    private Integer execute(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            context.getSource().getSender().sendRichMessage("ONLY PLAYER BISHER");
            return 1;
        }
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        String targetName = context.getArgument("spieler", String.class);
        int amount = context.getArgument("wert", Integer.class);

        UUID targetUUID = Bukkit.getPlayerUniqueId(targetName);
        if (targetUUID == null) {
            raspiPlayer.sendMessage(String.format(RaspiMessages.PLAYER_NOT_FOUND_NAME, targetName));
            return 1;
        }

        Raspi.playerLifeCycleService().getRaspiAccount(targetUUID, false).thenAccept(raspiAccount -> {
            raspiAccount.getRaspiUser().setCoins((long) amount);
            raspiAccount.save();
            raspiPlayer.sendMessage(String.format("%s hat nun %s VC!<gray> <italic>BEACHTE, dass die COINS die Wirtschaft beeinflussen!", targetName, amount), true);
        });
        return Command.SINGLE_SUCCESS;
    }

}
