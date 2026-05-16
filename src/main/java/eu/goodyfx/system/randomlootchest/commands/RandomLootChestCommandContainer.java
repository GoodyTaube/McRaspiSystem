package eu.goodyfx.system.randomlootchest.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.core.api.CommandUtils;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.database.RaspiPlayer;
import eu.goodyfx.system.core.utils.RaspiSounds;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import java.util.function.Predicate;


public class RandomLootChestCommandContainer {

    private static final String helpString = """
            *-----<dark_red><b>RandomLootChest<reset><red>*-----*<br>
            <red>/rlc addItem <gray>#Open the item summation Gui<br>
            <red>/rlc killAll <gray>#Alle aktiven kisten vernichten<br>
            <red>/rlc toggleBreak <gray>#Eine kiste abbauen zum vernichten<br>
            <red>/rlc forceSpawn <gray>#Das neue Spawnen einer zusätzlichen Kiste erzwingen.<br>
            <red>*-----<dark_red><b>RandomLootChest<reset><red>*-----*
            """;

    private final Predicate<CommandSourceStack> PLAYER_ONLY = CommandUtils.PLAYER_ONLY;

    public LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("rlc").requires(PLAYER_ONLY).executes(this::helpCommand)
                .then(Commands.literal("addItem").requires(PLAYER_ONLY).executes(this::addItemCommand))
                .then(Commands.literal("killAll").requires(PLAYER_ONLY).executes(this::killAllChestCommand))
                .then(Commands.literal("toggleBreak").requires(PLAYER_ONLY).executes(this::toggleBreakCommand))
                .then(Commands.literal("forceSpawn").requires(PLAYER_ONLY).executes(this::forceSpawnChestCommand))
                .build();
    }

    private Integer helpCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        raspiPlayer.sendMessage(helpString);
        return Command.SINGLE_SUCCESS;
    }

    private Integer addItemCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);

        //TODO AddItem GUI
        //raspiPlayer.openInventory(openGUI);
        raspiPlayer.playSound(RaspiSounds.SUCCESS);
        return Command.SINGLE_SUCCESS;
    }

    private Integer toggleBreakCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);


        raspiPlayer.playSound(RaspiSounds.SUCCESS);
        return Command.SINGLE_SUCCESS;
    }

    private Integer killAllChestCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);


        raspiPlayer.playSound(RaspiSounds.SUCCESS);
        return Command.SINGLE_SUCCESS;
    }

    private Integer forceSpawnChestCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);


        raspiPlayer.playSound(RaspiSounds.SUCCESS);
        return Command.SINGLE_SUCCESS;
    }


}
