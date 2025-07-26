package eu.goodyfx.mcraspisystem.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import eu.goodyfx.mcraspisystem.utils.RaspiFormatting;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class ChatColorCommandArgument implements CustomArgumentType.Converted<RaspiFormatting, String> {

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^(#|0x)?[a-fA-F0-9]{6}$");

    private static final DynamicCommandExceptionType ERROR_COLOR = new DynamicCommandExceptionType(color -> {
        return MessageComponentSerializer.message().serialize(MiniMessage.miniMessage().deserialize(String.format("<red>%s ist nicht Gültig!<br><red>Bitte verwende einen Gültigen code! (#000000) oder (Farbe)", color)));
    });


    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.greedyString();
    }

    @Override
    public RaspiFormatting convert(String nativeType) throws CommandSyntaxException {

        try {
            String input = nativeType.trim().toLowerCase();
            if (HEX_COLOR_PATTERN.matcher(input).matches()) {
                return RaspiFormatting.HEX;
            }

            for (RaspiFormatting formatting : RaspiFormatting.chatAllowed()) {
                // Match value (e.g., "<red>")
                if (formatting.getValue() != null && formatting.getValue().equalsIgnoreCase(input)) {
                    return formatting;
                }


            }
            return RaspiFormatting.valueOf(getNativeType().toString());
        } catch (IllegalArgumentException ignored) {
            throw ERROR_COLOR.create(nativeType);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (RaspiFormatting color : RaspiFormatting.chatAllowed()) {
            if (color.name().equalsIgnoreCase("HEX")) {
                builder.suggest(" ");
                continue;
            }
            if (color.getValue() != null && !color.getValue().isEmpty()) {
                builder.suggest(color.getValue()); // <red>
            }
        }
        return builder.buildFuture();
    }
}
