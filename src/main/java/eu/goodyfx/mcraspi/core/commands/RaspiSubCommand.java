package eu.goodyfx.mcraspi.core.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import eu.goodyfx.mcraspi.core.database.RaspiSuggestions;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.List;

public interface RaspiSubCommand {
    ArgumentBuilder<CommandSourceStack, ?> build();

    default SuggestionProvider<CommandSourceStack> suggestOffline() {
        return (context, builder) -> RaspiSuggestions.suggestOfflinePlayer(builder);
    }

    default SuggestionProvider<CommandSourceStack> suggestOnline() {
        return (context, builder) -> RaspiSuggestions.suggestOnlinePlayers(builder);
    }

    default SuggestionProvider<CommandSourceStack> suggestStrings(String... strings) {
        return (context, builder) -> RaspiSuggestions.suggestStringList(builder, List.of(strings));
    }


}
