package eu.goodyfx.mcraspi.modules.reise.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.commands.RaspiCommand;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.core.database.RaspiSuggestions;
import eu.goodyfx.mcraspi.modules.reise.RaspiReiseSystem;
import eu.goodyfx.mcraspi.modules.reise.managers.ReiseLocationManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class RBSucheCommandContainer extends RaspiCommand {
    @Override
    public String getName() {
        return "rbsuche";
    }

    @Override
    public String getDescription() {
        return "Finde einen Spieler port.";
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return command();
    }


    public RBSucheCommandContainer(RaspiReiseSystem system) {
        super(system.getPlugin());
    }

    public LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal(getName()).then(Commands.argument("spieler", StringArgumentType.string()).suggests(((context, builder) -> RaspiSuggestions.suggestOfflinePlayer(builder))).executes(this::execute)).build();
    }


    private int execute(CommandContext<CommandSourceStack> context) {

        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer((Player) context.getSource().getSender());
        String spielerName = context.getArgument("spieler", String.class);

        ReiseLocationManager.checkUser(raspiPlayer, spielerName, getPlugin());
        return Command.SINGLE_SUCCESS;
    }

}
