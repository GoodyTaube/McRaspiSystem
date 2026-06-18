package eu.goodyfx.mcraspi.modules.reise.commands.reise.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.commands.RaspiSubCommand;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.modules.reise.managers.ReiseLocationManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import java.util.Map;

public class ListSubCommand implements RaspiSubCommand {
    @Override
    public ArgumentBuilder<CommandSourceStack, ?> build() {
        return Commands.literal("list")
                .then(Commands.argument("from", IntegerArgumentType.integer(1, 99999)).then(Commands.argument("to", IntegerArgumentType.integer(1, 999999)).executes(this::execute)));
    }


    private int execute(CommandContext<CommandSourceStack> context) {
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer((Player) context.getSource().getSender());

        int from = context.getArgument("from", Integer.class);
        int to = context.getArgument("to", Integer.class);

        raspiPlayer.sendMessage(getIDsByRange(from, to));
        return Command.SINGLE_SUCCESS;
    }


    public String getIDsByRange(int min, int max) {
        int times = 0;
        int val = 0;
        if (min == 0) {
            min++;
        }
        StringBuilder builder = new StringBuilder("<gold>Liste von ID's:<br>");
        Map<Integer, String> ids = ReiseLocationManager.getPOS();

        if (max > ids.size()) {
            max = ids.size();
        }

        for (int i = min; i <= max; i++) {
            builder.append(ids.get(i)).append("<br>");
            times++;
            if (times == 10) {
                builder.setLength(builder.length() - 4);
                builder.append(getStringNext(i, i + 10));
                break;
            }


        }

        builder.setLength(builder.length() - 4);

        return builder.toString();
    }

    public String getStringNext(int from, int to) {
        from++;
        return String.format("<br><gold>>>>>>>>> <click:run_command:'/reise list %s %s'>%s - %s <<<<<<<<<br>", from, to, from, to);
    }


}
