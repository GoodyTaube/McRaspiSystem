package eu.goodyfx.mcraspi.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.core.database.RaspiSuggestions;
import eu.goodyfx.mcraspi.core.utils.RaspiFormatting;
import eu.goodyfx.mcraspi.core.utils.RaspiMessages;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MessageCommandContainer extends RaspiCommand {

    @Override
    public String getName() {
        return "message";
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return command();
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"msg", "tell", "reply"};
    }

    public MessageCommandContainer(McRaspiSystem plugin) {
        super(plugin);
    }

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("message").then(Commands.argument("spieler", StringArgumentType.string()).suggests(((context, builder) -> RaspiSuggestions.suggestOnlinePlayers(builder))).then(Commands.argument("nachricht", StringArgumentType.greedyString()).executes(MessageCommandContainer::perform))).build();
    }

    private static final String WHISPER_SENDER = "<gray><i>Du flüsterst %s <gray><i>zu: <message>";
    private static final String WHISPER_RECEIVER = "<hover:show_text:'<gray>Klicke zum antworten.'><click:suggest_command:'/msg %s '>%s <gray><i>flüstert dir zu: <message>";


    /**
     * Whisper command for McRaspi Players
     * Limited to color Only
     *
     * @param context Command Context
     * @return 1 = SUCCESS 0 = Command FAILED
     */
    private static int perform(CommandContext<CommandSourceStack> context) {
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer((Player) context.getSource().getSender());
        String targetName = context.getArgument("spieler", String.class);
        if (context.getArgument("nachricht", String.class) == null) {
            return 0;
        }
        String message = context.getArgument("nachricht", String.class);
        Component messageComponent = RaspiFormatting.COLOR_ONLY_MESSAGE.deserialize(message);
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            raspiPlayer.sendMessage(RaspiMessages.PLAYER_NOT_FOUND, true);
            return 1;
        }
        RaspiPlayer targetRaspi = Raspi.playerLifeCycleService().getRaspiPlayer(target);
        String whisper_sender_layout = String.format(WHISPER_SENDER, targetRaspi.getColorName());
        Component whisper_sender = MiniMessage.miniMessage().deserialize(whisper_sender_layout, Placeholder.component("message", messageComponent));
        String whisper_receiver_layout = String.format(WHISPER_RECEIVER, raspiPlayer.getPlayer().getName(), raspiPlayer.getColorName());
        Component whisper_receiver = MiniMessage.miniMessage().deserialize(whisper_receiver_layout, Placeholder.component("message", messageComponent));
        raspiPlayer.sendMessage(whisper_sender, true);
        targetRaspi.sendMessage(whisper_receiver, true);
        return Command.SINGLE_SUCCESS;
    }

}
