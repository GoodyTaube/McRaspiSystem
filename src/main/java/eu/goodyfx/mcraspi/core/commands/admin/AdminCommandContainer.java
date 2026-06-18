package eu.goodyfx.mcraspi.core.commands.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.api.CommandUtils;
import eu.goodyfx.mcraspi.core.commands.RaspiCommand;
import eu.goodyfx.mcraspi.core.commands.RaspiSubCommand;
import eu.goodyfx.mcraspi.core.commands.admin.sub.CoinsSubCommand;
import eu.goodyfx.mcraspi.core.commands.admin.sub.MigrationSubCommand;
import eu.goodyfx.mcraspi.core.commands.admin.sub.SetupSubCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AdminCommandContainer extends RaspiCommand {
    private final Set<RaspiSubCommand> subCommands = ConcurrentHashMap.newKeySet();

    @Override
    public String getName() {
        return "radmin";
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return command();
    }

    @Override
    public String getDescription() {
        return "Admin Command";

    }


    private void subCommands() {
        this.subCommands.add(new SetupSubCommand(getPlugin()));
        this.subCommands.add(new MigrationSubCommand());
        this.subCommands.add(new CoinsSubCommand());
    }

    public AdminCommandContainer(McRaspiSystem plugin) {
        super(plugin);
        subCommands();
    }

    public LiteralCommandNode<CommandSourceStack> command() {
        LiteralArgumentBuilder<CommandSourceStack> adminRoot = Commands.literal(getName()).requires(CommandUtils.PLAYER_ONLY);
        for (RaspiSubCommand subCommand : subCommands) {
            adminRoot.then(subCommand.build());
        }
        return adminRoot.build();
    }


}
