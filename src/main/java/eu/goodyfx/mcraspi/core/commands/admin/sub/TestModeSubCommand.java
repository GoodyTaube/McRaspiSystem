package eu.goodyfx.mcraspi.core.commands.admin.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.commands.RaspiSubCommand;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class TestModeSubCommand implements RaspiSubCommand {
    private final McRaspiSystem plugin;
    private static final String PATH = "testMode";

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> build() {
        return Commands.literal("testmode").executes(this::executes);
    }

    public TestModeSubCommand(McRaspiSystem plugin) {
        FileConfiguration config = plugin.getConfig();
        if (!config.contains(PATH)) {
            config.addDefault(PATH, false);
            plugin.saveConfig();
        }
        this.plugin = plugin;
    }


    public int executes(CommandContext<CommandSourceStack> context) {
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer((Player) context.getSource().getSender());
        FileConfiguration config = plugin.getConfig();
        boolean state = config.getBoolean(PATH);
        config.set(PATH, !state);
        plugin.saveConfig();
        String msg = String.format("Du hast den TestModus auf %s gestellt.", !state);
        raspiPlayer.sendMessage(msg);
        return Command.SINGLE_SUCCESS;
    }

}
