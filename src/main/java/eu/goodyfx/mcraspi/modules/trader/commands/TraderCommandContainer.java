package eu.goodyfx.mcraspi.modules.trader.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.mcraspi.core.api.CommandUtils;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.commands.RaspiCommand;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.core.database.RaspiSuggestions;
import eu.goodyfx.mcraspi.core.exceptions.AllReadyExistException;
import eu.goodyfx.mcraspi.core.utils.RaspiSounds;
import eu.goodyfx.mcraspi.modules.trader.TraderSubSystem;
import eu.goodyfx.mcraspi.modules.trader.managers.TraderDB;
import eu.goodyfx.mcraspi.modules.trader.utils.TraderBuilder;
import eu.goodyfx.mcraspi.modules.trader.utils.TraderInventories;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Predicate;

public class TraderCommandContainer extends RaspiCommand {

    private final List<String> activeTraders;
    private final TraderSubSystem subSystem;
    private final TraderDB traderDB;

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return command();
    }

    @Override
    public String getName() {
        return "trader";
    }

    @Override
    public String getDescription() {
        return "";
    }

    public TraderCommandContainer(TraderSubSystem subSystem) {
        super(subSystem.getTraderDB().getPlugin());
        this.subSystem = subSystem;
        this.traderDB = subSystem.getTraderDB();
        this.activeTraders = subSystem.getTraderDB().getTraders();
    }

    private static final String TRADER_UID = "traderUID";
    private static final String FAIL_NON_EXIST_TRADER = "<yellow>`%s` <red>existiert nicht in der DB.";

    /**
     * Gets the traderUID required Argument
     *
     * @param <T> String
     * @return traderUID
     */
    private <T> RequiredArgumentBuilder<CommandSourceStack, String> getArgument() {
        return CommandUtils.string(TRADER_UID);
    }

    private static final Predicate<CommandSourceStack> PLAYER_ONLY = CommandUtils.PLAYER_ONLY;


    public LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("trader").requires(PLAYER_ONLY).executes(this::helpCommand)
                .then(Commands.literal("create").requires(PLAYER_ONLY).then(getArgument()
                        .executes(this::createTrader)))
                .then(Commands.literal("edit").requires(PLAYER_ONLY).then(getArgument()
                        .suggests(((context, builder) -> RaspiSuggestions.suggestStringList(builder, activeTraders)))
                        .executes(this::editTrader)))
                .then(Commands.literal("remove").requires(PLAYER_ONLY).then(getArgument()
                        .suggests((context, builder) -> RaspiSuggestions.suggestStringList(builder, activeTraders))
                        .executes(this::removeTrader)))
                .then(Commands.literal("spawn").requires(PLAYER_ONLY).then(getArgument()))
                .build();
    }

    public Integer helpCommand(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().sendRichMessage("Trader Command Hilfe:" +
                "<br>/trader create <name> #Erzeugt einen Trader mit Namen." +
                "<br>/trader edit <name> #Öffnet das Menü vom Trader." +
                "<br>/trader remove <name> #Löscht den Trader aus der Datenbank." +
                "<br>/trader spawn <name> #Spawnt den Trader falls er gestorben ist.");
        return Command.SINGLE_SUCCESS;
    }

    public Integer createTrader(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        Location spawnLocation = raspiPlayer.getLocation();
        String name = context.getArgument(TRADER_UID, String.class);
        try {
            new TraderBuilder(name, spawnLocation, subSystem).build();
            raspiPlayer.sendMessage(String.format("<green>%s wurde an deiner Position erzeugt!", name), true);
        } catch (AllReadyExistException e) {
            raspiPlayer.sendMessage(String.format("<yellow>`%s` <red>existiert bereits in der DB!", name), true, RaspiSounds.ERROR);
        }
        return Command.SINGLE_SUCCESS;
    }

    public Integer removeTrader(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        String name = context.getArgument(TRADER_UID, String.class);
        if (!traderDB.traderExist(name)) {
            raspiPlayer.sendMessage(String.format(FAIL_NON_EXIST_TRADER, name), true, RaspiSounds.ERROR);
            return 1;
        }
        traderDB.remove(name);
        raspiPlayer.sendMessage(String.format("<green>Du hast `%s` aus der Datenbank entfernt.", name), true);
        return Command.SINGLE_SUCCESS;
    }

    public Integer editTrader(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        String name = context.getArgument(TRADER_UID, String.class);

        if (!traderDB.traderExist(name)) {
            raspiPlayer.sendMessage(String.format(FAIL_NON_EXIST_TRADER, name), true, RaspiSounds.ERROR);
            return 1;
        }

        if (traderDB.shopExist(name)) {
            player.openInventory(TraderInventories.traderMenu(traderDB, name));
            return 1;
        }
        player.openInventory(TraderInventories.createRecipeInventory());
        return Command.SINGLE_SUCCESS;
    }
}
