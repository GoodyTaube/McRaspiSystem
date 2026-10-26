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
import eu.goodyfx.mcraspi.core.utils.RaspiSounds;
import eu.goodyfx.mcraspi.modules.trader.TraderSubSystem;
import eu.goodyfx.mcraspi.modules.trader.managers.TraderManager;
import eu.goodyfx.mcraspi.modules.trader.utils.Trader;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

public class TraderCommandContainer extends RaspiCommand {

    private final TraderSubSystem subSystem;
    private final TraderManager traderManager;


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
        super(subSystem.getPlugin());
        this.subSystem = subSystem;
        this.traderManager = subSystem.getTraderManager();

    }


    private static final String TRADER_NAME = "traderName";
    private static final String FAIL_NON_EXIST_TRADER = "<yellow>`%s` <red>existiert nicht in der DB.";

    /**
     * Gets the traderUID required Argument
     *
     * @param <T> String
     * @return traderUID
     */
    private <T> RequiredArgumentBuilder<CommandSourceStack, String> getArgument() {
        return CommandUtils.string(TRADER_NAME);
    }

    private static final Predicate<CommandSourceStack> PLAYER_ONLY = CommandUtils.PLAYER_ONLY;


    public LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("trader").requires(PLAYER_ONLY).executes(this::helpCommand)
                .then(Commands.literal("create").requires(PLAYER_ONLY).then(getArgument().executes(this::createTrader)))
                .then(Commands.literal("edit").requires(PLAYER_ONLY).then(getArgument().suggests(((context, builder) -> RaspiSuggestions.suggestStringList(builder, traderManager.getActiveNames()))).executes(this::editTrader)))
                .then(Commands.literal("remove").requires(PLAYER_ONLY).then(getArgument().suggests((context, builder) -> RaspiSuggestions.suggestStringList(builder, traderManager.getActiveNames())).executes(this::removeTrader)))
                .then(Commands.literal("spawn").requires(PLAYER_ONLY).then(getArgument().suggests(((context, builder) -> RaspiSuggestions.suggestStringList(builder, traderManager.getActiveNames()))).executes(this::spawnTrader))).build();
    }

    public Integer helpCommand(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().sendRichMessage("Trader Command Hilfe:" + "<br>/trader create <name> #Erzeugt einen Trader mit Namen." + "<br>/trader edit <name> #Öffnet das Menü vom Trader." + "<br>/trader remove <name> #Löscht den Trader aus der Datenbank." + "<br>/trader spawn <name> #Spawnt den Trader falls er gestorben ist.");
        return Command.SINGLE_SUCCESS;
    }

    public Integer createTrader(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        Location spawnLocation = raspiPlayer.getLocation();
        String name = context.getArgument(TRADER_NAME, String.class);

        if (traderManager.traderExists(name)) {
            raspiPlayer.sendMessage("<red>Der Trader existiert bereits in der DB.", true);
            return 1;
        }


        Trader trader = traderManager.createTrader(name);
        if (trader == null) {
            raspiPlayer.sendMessage("Ein Fehler ist aufgetreten.", true);
            return 1;
        }

        trader.spawnTraderDummy(subSystem.getPlugin(), spawnLocation);
        return Command.SINGLE_SUCCESS;
    }

    public Integer removeTrader(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        String name = context.getArgument(TRADER_NAME, String.class);

        if (!traderManager.traderExists(name)) {
            raspiPlayer.sendMessage(String.format(FAIL_NON_EXIST_TRADER, name), true);
            return 1;
        }

        Trader trader = traderManager.getTraderByName(name);
        if (traderManager.delete(trader.getUid())) {
            raspiPlayer.sendMessage(String.format("<green>Du hast `%s` aus der Datenbank entfernt.", name), true);
        } else {
            raspiPlayer.sendDebugMessage("ERROR WHILE DELETE TRADER!");
        }
        return Command.SINGLE_SUCCESS;
    }

    public Integer editTrader(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        String name = context.getArgument(TRADER_NAME, String.class);

        if (!traderManager.traderExists(name)) {
            raspiPlayer.sendMessage(String.format(FAIL_NON_EXIST_TRADER, name));
            return 1;
        }

        Trader trader = traderManager.getTraderByName(name);

        if (trader.getRecipes().isEmpty()) {
            raspiPlayer.openInventory(trader.recipeEditor(0));
        } else {
            trader.openTraderSettings(raspiPlayer);
        }
        return Command.SINGLE_SUCCESS;
    }

    public Integer spawnTrader(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        String name = context.getArgument(TRADER_NAME, String.class);
        if (!traderManager.traderExists(name)) {
            raspiPlayer.sendMessage(String.format(FAIL_NON_EXIST_TRADER, name), true, RaspiSounds.ERROR);
            return 1;
        }

        Trader trader = traderManager.getTraderByName(name);
        trader.spawnTraderDummy(subSystem.getPlugin(), player.getLocation());
        raspiPlayer.playSound(RaspiSounds.SUCCESS);
        return Command.SINGLE_SUCCESS;
    }


}
