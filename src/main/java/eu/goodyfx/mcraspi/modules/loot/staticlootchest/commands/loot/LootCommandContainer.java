package eu.goodyfx.mcraspi.modules.loot.staticlootchest.commands.loot;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.mcraspi.core.api.CommandUtils;
import eu.goodyfx.mcraspi.core.commands.RaspiCommand;
import eu.goodyfx.mcraspi.core.commands.RaspiSubCommand;
import eu.goodyfx.mcraspi.modules.loot.staticlootchest.LootChestSystem;
import eu.goodyfx.mcraspi.modules.loot.staticlootchest.commands.loot.sub.GiveSubCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LootCommandContainer extends RaspiCommand {

    private final Set<RaspiSubCommand> subCommands = ConcurrentHashMap.newKeySet();

    @Override
    public String getName() {
        return "loot";
    }

    @Override
    public String getDescription() {
        return "Static LootChest (RB) Commands";
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return command();
    }

    public LootCommandContainer(LootChestSystem system) {
        super(system.getPlugin());
    }

    private void setSubCommands() {
        subCommands.add(new GiveSubCommand());
    }

    public LiteralCommandNode<CommandSourceStack> command() {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("loot").requires(CommandUtils.PLAYER_ONLY);

        for (RaspiSubCommand subCommand : subCommands) {
            root.then(subCommand.build());
        }

        return root.build();
    }

}
