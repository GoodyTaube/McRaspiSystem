package eu.goodyfx.mcraspisystem.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import eu.goodyfx.mcraspisystem.McRaspiSystem;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.plugin.java.JavaPlugin;

public class AdminWartungSubCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> adminWartungCommand() {
        return Commands.literal("admin").then(Commands.literal("wartung").then(Commands.argument("wartung", BoolArgumentType.bool()).executes(AdminWartungSubCommand::logic)));
    }

    public static int logic(CommandContext<CommandSourceStack> context) {
        boolean value = BoolArgumentType.getBool(context, "wartung");
        JavaPlugin plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
        plugin.getConfig().set("Utilities.wartung", value);
        context.getSource().getSender().sendRichMessage(String.format("<green>Changed Wartung:%s", value));
        return Command.SINGLE_SUCCESS;
    }


}
