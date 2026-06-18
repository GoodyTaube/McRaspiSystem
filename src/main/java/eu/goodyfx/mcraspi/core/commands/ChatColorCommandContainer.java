package eu.goodyfx.mcraspi.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.core.utils.RaspiFormatting;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class ChatColorCommandContainer extends RaspiCommand {


    @Override
    public String getName() {
        return "chatcolor";
    }

    @Override
    public String getDescription() {
        return "Command to Color PlayerName";
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return command();
    }

    @Override
    public String[] getAliases() {
        return new String[]{"cc"};
    }

    public ChatColorCommandContainer(McRaspiSystem plugin) {
        super(plugin);
    }


    public LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal(getName()).then(Commands.argument("farbe", StringArgumentType.greedyString()).suggests((this::listSuggestions)).executes(this::perform)).build();
    }


    private <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (RaspiFormatting color : RaspiFormatting.chatAllowed()) {
            if (color == RaspiFormatting.HEX) {
                continue;
            }
            if (color.getValue() != null && !color.getValue().isEmpty()) {
                builder.suggest(color.getValue());
            }
            for (String alias : color.getAliases()) {
                if (alias != null && !alias.equals(color.getValue())) {
                    builder.suggest(alias);
                }
            }
            if (color.getLegacyValue() != null && !color.getLegacyValue().isEmpty()) {
                builder.suggest(color.getLegacyValue());
            }
        }
        // Optional: Ein Hinweis für Hex-Farben
        builder.suggest("#HEX");
        return builder.buildFuture();
    }

    private int perform(CommandContext<CommandSourceStack> context) {

        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer((Player) context.getSource().getSender());
        String input = context.getArgument("farbe", String.class).split(" ")[0];
        String color = input; //norm

        if (!color.startsWith("<") && !color.startsWith("&")) {
            color = String.format("<%s>", color);
        }
        if (color.equalsIgnoreCase("<random>")) {
            raspiPlayer.nameController.setPlayerColor(color);
            raspiPlayer.nameController.updateRandom();
            raspiPlayer.sendMessage(String.format("<gray>Deine Neue Chat Farbe ist jetzt: <gray>▆▇ %s <gray>▇▆", raspiPlayer.getColorName()), true);
            return 1;
        }
        String formatted = RaspiFormatting.formattingChatMessage(color);
        Component colorOnly = RaspiFormatting.COLOR_ONLY_MESSAGE.deserialize(formatted);
        String cleaned = RaspiFormatting.COLOR_ONLY_MESSAGE.serialize(colorOnly);
        boolean isValid = Arrays.stream(RaspiFormatting.values()).anyMatch(raspiFormatting -> raspiFormatting.getAliases().contains(cleaned) || raspiFormatting.getValue().contains(cleaned));
        boolean isHex = input.matches("<?#([A-Fa-f0-9]{6})>?");
        if (!isValid && !isHex) {
            raspiPlayer.sendMessage(String.format("<red>%s ist keine gültige Farbe.", color), true);
            return 1;
        }
        raspiPlayer.nameController.setPlayerColor(cleaned);
        raspiPlayer.sendMessage(String.format("<gray>Deine Neue Chat Farbe ist jetzt: %s▆▇ %s %s▇▆", raspiPlayer.getColor(), raspiPlayer.getColorName(), raspiPlayer.getColor()), true);

        return Command.SINGLE_SUCCESS;
    }


}
