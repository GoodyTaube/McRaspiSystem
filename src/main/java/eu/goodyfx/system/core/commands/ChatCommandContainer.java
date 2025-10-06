package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.chat.SignedMessage;
import org.bukkit.entity.Player;

public class ChatCommandContainer {

    public static LiteralCommandNode<CommandSourceStack> runCommand() {
        return Commands.literal("chat").then(Commands.argument("sign", StringArgumentType.string()).executes(context -> {

            if (!(context.getSource().getSender() instanceof Player player)) {
                return Command.SINGLE_SUCCESS;
            }

            String signature = context.getArgument("sign", String.class);
            player.deleteMessage(SignedMessage.signature(signature.getBytes()));
            return Command.SINGLE_SUCCESS;
        })).build();
    }

}
