package eu.goodyfx.mcraspi.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.mcraspi.core.api.CommandUtils;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.core.utils.RaspiSounds;
import eu.goodyfx.mcraspi.core.utils.Settings;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class SettingsCommandContainer {

    private static final String COMMAND_FAIL_404 = "<red>Die Setting: %s wurde nicht gefunden.";
    private static final String COMMAND_SUCCESS = "<gray>Du hast <blue>%s <gray>nun %s";

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("settings").requires(CommandUtils.PLAYER_ONLY).then(Commands.argument("setting", StringArgumentType.string()).suggests(createSuggestions()).executes(SettingsCommandContainer::perform)).build();
    }

    /**
     * Helper method to create Suggestions
     *
     * @return Suggestions as Provider
     */
    private static SuggestionProvider<CommandSourceStack> createSuggestions() {
        return (context, builder) -> {
            String input = builder.getRemaining().toLowerCase();
            //Dynamic Suggestions by Settings.enum
            Arrays.stream(Settings.values()).map(Settings::getCommandKey).filter(key -> key.toLowerCase().startsWith(input)).forEach(builder::suggest);
            return builder.buildFuture();
        };
    }

    private static int perform(CommandContext<CommandSourceStack> context) {
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer((Player) context.getSource().getSender());
        String settingName = context.getArgument("setting", String.class);

        Settings setting = Settings.fromCommandInput(settingName);

        if (setting == null) {
            raspiPlayer.sendMessage(String.format(COMMAND_FAIL_404, settingName), true, RaspiSounds.ERROR);
            return 1;
        }

        boolean currentSettingStatus = raspiPlayer.settings().get(setting);
        boolean newSettingStatus = !currentSettingStatus;
        raspiPlayer.settings().set(setting, newSettingStatus);

        String statusMessage = newSettingStatus ? "<green>aktiviert" : "<red>deaktiviert";
        raspiPlayer.sendMessage(String.format(COMMAND_SUCCESS, setting.getDisplayName(), statusMessage), true, RaspiSounds.SUCCESS);
        return Command.SINGLE_SUCCESS;
    }

}
