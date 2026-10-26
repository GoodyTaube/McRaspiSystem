package eu.goodyfx.mcraspi.core.commands.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.api.CommandUtils;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.commands.RaspiCommand;
import eu.goodyfx.mcraspi.core.commands.RaspiSubCommand;
import eu.goodyfx.mcraspi.core.commands.admin.sub.MigrationSubCommand;
import eu.goodyfx.mcraspi.core.commands.admin.sub.RestoreSubCommand;
import eu.goodyfx.mcraspi.core.commands.admin.sub.SetupSubCommand;
import eu.goodyfx.mcraspi.core.commands.admin.sub.TestModeSubCommand;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.core.utils.Settings;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AdminCommandContainer extends RaspiCommand {
    private final Set<RaspiSubCommand> subCommands = ConcurrentHashMap.newKeySet();
    private McRaspiSystem plugin;

    @Override
    public String getName() {
        return "admin";
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return command();
    }

    @Override
    public String getDescription() {
        return "Admin Command";

    }


    private void subCommands() {
        this.subCommands.add(new SetupSubCommand(getPlugin()));
        this.subCommands.add(new MigrationSubCommand());
        this.subCommands.add(new RestoreSubCommand());
        this.subCommands.add(new TestModeSubCommand(plugin));
    }

    public AdminCommandContainer(McRaspiSystem plugin) {
        super(plugin);
        this.plugin = plugin;
        subCommands();
    }

    public LiteralCommandNode<CommandSourceStack> command() {
        LiteralArgumentBuilder<CommandSourceStack> adminRoot = Commands.literal(getName()).requires(CommandUtils.PLAYER_ONLY);
        for (RaspiSubCommand subCommand : subCommands) {
            adminRoot.then(subCommand.build());
        }
        return adminRoot.build();
    }

    public boolean teleportController(CommandSender sender, String[] args) {
        if (sender instanceof BlockCommandSender commandSender) {
            CommandBlock block = (CommandBlock) commandSender.getBlock().getState();
            Location location = commandSender.getBlock().getLocation();
            Collection<Entity> entities = location.getNearbyEntities(3, 4, 3);
            String commandString = block.getCommand();


            for (Entity entity : entities) {
                if (entity instanceof Player player && commandString.contains("@p")) {
                    block.setSuccessCount(block.getSuccessCount() + 1);
                    block.update();
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandString.replace("@p", player.getName()));
                    break;
                }
            }


            return true;
        }


        Player player = Bukkit.getPlayer(args[0]); //Get Player by Name
        String location = args[1]; //Teleport Location

        if (player == null) {
            String log = String.format("[%s] is not a valid Player! See Command Usage for more Information", args[0]);
            plugin.getLogger().severe(log);
            return false;
        }
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);

        Raspi.playerLifeCycleService().getCachedRaspiPlayers().forEach(all -> {
            if (all.settings().get(Settings.MESSAGES)) {
                String output = String.format("%s hat Teleport %s benutzt.", raspiPlayer.getColorName(), location);
                all.sendMessage(output, true);
            }
        });
        String log = String.format("Raspi-Teleport: %s hat Teleport %s benutzt", player.getName(), location);
        plugin.getLogger().info(log);
        return true;
    }


}
