package eu.goodyfx.mcraspi.modules.reise.commands.reise;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.mcraspi.core.commands.RaspiCommand;
import eu.goodyfx.mcraspi.core.commands.RaspiSubCommand;
import eu.goodyfx.mcraspi.modules.reise.RaspiReiseSystem;
import eu.goodyfx.mcraspi.modules.reise.commands.reise.sub.*;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ReiseCommandContainer extends RaspiCommand {

    private final Set<RaspiSubCommand> subCommands = ConcurrentHashMap.newKeySet();

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return command();
    }

    public ReiseCommandContainer(RaspiReiseSystem system) {
        super(system.getPlugin());
        setSubCommands();
    }

    private void setSubCommands() {
        subCommands.add(new SetupSubCommand());
        subCommands.add(new ResetSubCommand());
        subCommands.add(new RemoveSubCommand());
        subCommands.add(new ListSubCommand());
        subCommands.add(new TeleportSubCommand());
    }

    public LiteralCommandNode<CommandSourceStack> command() {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("reise");
        for (RaspiSubCommand subCommand : subCommands) {
            root.then(subCommand.build()).build();
        }
        return root.build();
    }

}

