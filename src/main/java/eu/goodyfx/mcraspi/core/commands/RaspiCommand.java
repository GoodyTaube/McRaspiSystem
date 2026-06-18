package eu.goodyfx.mcraspi.core.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.mcraspi.McRaspiSystem;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.Getter;

public abstract class RaspiCommand {

    @Getter
    private final McRaspiSystem plugin;

    public RaspiCommand(McRaspiSystem plugin) {
        this.plugin = plugin;

        plugin.registerCommand(this);

    }

    public abstract String getName();

    public abstract String getDescription();

    public abstract LiteralCommandNode<CommandSourceStack> getCommand();

    public String[] getAliases() {
        return new String[0];
    }

}
