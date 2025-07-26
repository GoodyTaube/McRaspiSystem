package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class BackCommandContainer {

    public static LiteralCommandNode<CommandSourceStack> backCommand() {
        return Commands.literal("back")
                .executes(context -> {

                    return Command.SINGLE_SUCCESS;
                }).build();
    }
}
