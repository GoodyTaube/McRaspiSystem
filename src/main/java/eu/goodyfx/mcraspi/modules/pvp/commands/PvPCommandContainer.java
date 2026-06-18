package eu.goodyfx.mcraspi.modules.pvp.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.mcraspi.core.api.CommandUtils;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.commands.RaspiCommand;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.modules.pvp.PvPSubSystem;
import eu.goodyfx.mcraspi.modules.pvp.utils.PvPManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class PvPCommandContainer extends RaspiCommand {

    private final PvPManager manager;

    @Override
    public String getName() {
        return "pvptoggle";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return command();
    }

    public PvPCommandContainer(PvPSubSystem system) {
        super(system.getPlugin());
        this.manager = system.getPvPManager();
    }


    public LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal(getName()).requires(CommandUtils.PLAYER_ONLY).executes(this::execute).build();
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        manager.handleToggle(raspiPlayer);
        return Command.SINGLE_SUCCESS;
    }


}
