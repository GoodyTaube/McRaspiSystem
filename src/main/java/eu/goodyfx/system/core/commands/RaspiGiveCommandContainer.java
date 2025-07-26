package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.McRaspiSystem;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RaspiGiveCommandContainer {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    public LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("raspigive").then(Commands.argument("item", StringArgumentType.word()).then(Commands.argument("player", StringArgumentType.string()).executes(source -> {
            CommandSender executor = source.getSource().getSender();
            Player target = Bukkit.getPlayer(source.getArgument("player", String.class));
            if (target != null) {
                String item = source.getArgument("item", String.class);
                plugin.getModule().getRaspiGiveManager().addItem(target, item);
            } else executor.sendRichMessage("<red>Error while Handling Player DATA");
            return Command.SINGLE_SUCCESS;

        }))).build();
    }

}
