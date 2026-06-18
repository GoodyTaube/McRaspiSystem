package eu.goodyfx.mcraspi.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.mcraspi.McRaspiSystem;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RaspiGiveCommandContainer extends RaspiCommand {

    @Override
    public String getName() {
        return "raspigive";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return command();
    }

    public RaspiGiveCommandContainer(McRaspiSystem plugin) {
        super(plugin);
    }


    public LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("raspigive").then(Commands.argument("item", StringArgumentType.word()).then(Commands.argument("player", StringArgumentType.string()).executes(source -> {
            CommandSender executor = source.getSource().getSender();
            Player target = Bukkit.getPlayer(source.getArgument("player", String.class));
            if (target != null) {
                String item = source.getArgument("item", String.class);
                getPlugin().getModule().getRaspiGiveManager().addItem(target, item);
            } else executor.sendRichMessage("<red>Error while Handling Player DATA");
            return Command.SINGLE_SUCCESS;

        }))).build();
    }

}
