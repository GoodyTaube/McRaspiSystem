package eu.goodyfx.mcraspi.core.commands.admin.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.commands.RaspiSubCommand;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.core.managers.LocationManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class SetupSubCommand implements RaspiSubCommand {

    private final McRaspiSystem plugin;

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> build() {
        return Commands.literal("setup").then(Commands.argument("location", StringArgumentType.word()).suggests(this::listSuggestions).executes(this::executeSpawn));
    }

    public SetupSubCommand(McRaspiSystem plugin) {
        this.plugin = plugin;
    }

    private static final String COMMAND_SUCCESS_SPAWN = "<green>Du hast den <namne> erfolgreich gesetzt.";


    private <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        builder.suggest("spawn");
        builder.suggest("warteraum");
        return builder.buildFuture();
    }

    public int executeSpawn(CommandContext<CommandSourceStack> context) {
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer((Player) context.getSource().getSender());
        String locationName = context.getArgument("location", String.class).toLowerCase();
        LocationManager manager = plugin.getModuleManager().getLocationManager();
        Location location = raspiPlayer.getLocation();
        manager.set(location, locationName);
        raspiPlayer.sendMessage(COMMAND_SUCCESS_SPAWN, true);
        return Command.SINGLE_SUCCESS;
    }


}
