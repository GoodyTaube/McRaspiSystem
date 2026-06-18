package eu.goodyfx.mcraspi.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.core.utils.RaspiFormatting;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

public class PrefixCommandContainer {

    private final McRaspiSystem plugin;

    public PrefixCommandContainer(McRaspiSystem plugin) {
        this.plugin = plugin;
    }

    public LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("prefix").
                then(Commands.argument("prefix", StringArgumentType.greedyString()).executes(this::executes)).executes(this::removePrefix).build();
    }


    private final static String COMMAND_SUCCESS = "Dein Prefix ist nun <green>%s";
    private final static String COMMAND_FAIL_NO_PREFIX = "<red>Du hast noch kein Prefix";
    private final static String COMMAND_SUCCESS_REMOVED = "<green>Du hast deinen Prefix entfernt.";
    private final static String DEBUG_MESSAGE_SUCCESS = "Changed Prefix for %s to %s";
    private final static String COMMAND_FAIL_LENGTH = "<red>Dein Prefix hat die Länge mit %s/%s überschritten.";
    private final static String CONFIG_LENGTH_INTEGER = "prefix.length";


    private Integer removePrefix(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            context.getSource().getSender().sendRichMessage("PLAYER_COMMAND_ONLY");
            return 1;
        }

        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);

        if (raspiPlayer.getPrefix() == null) {
            raspiPlayer.sendMessage(COMMAND_FAIL_NO_PREFIX, true);
            return 1;
        }
        raspiPlayer.removePrefix();
        raspiPlayer.sendMessage(COMMAND_SUCCESS_REMOVED, true);
        return Command.SINGLE_SUCCESS;
    }

    private Integer executes(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            context.getSource().getSender().sendRichMessage("PLAYER_COMMAND_ONLY");
            return 1;
        }

        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        String prefix = context.getArgument("prefix", String.class);
        prefix = RaspiFormatting.formattingChatMessage(prefix) + "<reset>";
        String tbString = prefix.replace(" ", "@");

        if (!lengthCheck(prefix)) {
            raspiPlayer.sendMessage(String.format(COMMAND_FAIL_LENGTH, getPlainPrefix(prefix).replace(" ", "").length(), plugin.getConfig().getInt(CONFIG_LENGTH_INTEGER)), true);
            return 1;
        }
        raspiPlayer.setPrefix(tbString);
        raspiPlayer.sendMessage(String.format(COMMAND_SUCCESS, prefix), true);
        Raspi.debugger().debug(String.format(DEBUG_MESSAGE_SUCCESS, player.getName(), prefix));
        return Command.SINGLE_SUCCESS;
    }


    private String getPlainPrefix(String prefix) {
        Component cleaning = MiniMessage.miniMessage().deserialize(prefix);
        return PlainTextComponentSerializer.plainText().serialize(cleaning);
    }

    private boolean lengthCheck(String prefix) {

        return getPlainPrefix(prefix).replace(" ", "").length() <= 12 && getPlainPrefix(prefix).replace(" ", "").length() > 1;
    }


}
