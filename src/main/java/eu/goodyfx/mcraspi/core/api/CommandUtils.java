package eu.goodyfx.mcraspi.core.api;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

public class CommandUtils {

    public static final Predicate<CommandSourceStack> PLAYER_ONLY = commandSourceStack -> commandSourceStack.getSender() instanceof Player;

    public static <T> RequiredArgumentBuilder<CommandSourceStack, String> string(String name) {
        return Commands.argument(name, StringArgumentType.string());
    }

}
