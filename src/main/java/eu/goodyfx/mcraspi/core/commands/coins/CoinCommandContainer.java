package eu.goodyfx.mcraspi.core.commands.coins;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.commands.RaspiCommand;
import eu.goodyfx.mcraspi.core.commands.RaspiSubCommand;
import eu.goodyfx.mcraspi.core.commands.coins.sub.SendSubCommand;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CoinCommandContainer extends RaspiCommand {

    private final Set<RaspiSubCommand> subCommands = ConcurrentHashMap.newKeySet();

    public CoinCommandContainer(McRaspiSystem plugin) {
        super(plugin);
        setSubCommands();
    }

    private void setSubCommands() {
        subCommands.add(new SendSubCommand());
    }

    @Override
    public String getName() {
        return "coins";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"money"};
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return command();
    }


    public LiteralCommandNode<CommandSourceStack> command() {

        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(getName()).executes(this::coinsCommand);

        for (RaspiSubCommand subCommand : subCommands) {
            root.then(subCommand.build());
        }

        return root.build();
    }


    private int coinsCommand(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            context.getSource().getSender().sendRichMessage("Dein Kontostand: ∞ VC (bitte nicht ausgeben)");
            return 1;
        }
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        raspiPlayer.sendMessage(String.format("<gray>Dein Kontostand: <aqua>%s <gray>VC", raspiPlayer.userData().getCoins()), true);
        return Command.SINGLE_SUCCESS;
    }


}
