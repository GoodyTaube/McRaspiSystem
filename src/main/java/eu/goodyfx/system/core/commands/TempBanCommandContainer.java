package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.core.database.RaspiSuggestions;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class TempBanCommandContainer {

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("tempban").executes(TempBanCommandContainer::helpCommand)
                .then(Commands.argument("player", StringArgumentType.string())
                        .suggests((context, builder) -> RaspiSuggestions.suggestOfflinePlayer(builder))
                        .executes(TempBanCommandContainer::tempBanPlayer))
                .build();
    }


    private static Integer helpCommand(CommandContext<CommandSourceStack> context) {

        return Command.SINGLE_SUCCESS;
    }

    private static Integer tempBanPlayer(CommandContext<CommandSourceStack> context) {

        return 1;
    }

}
