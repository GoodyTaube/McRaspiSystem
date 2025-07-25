package eu.goodyfx.mcraspisystem.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.tree.CommandNode;
import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.subcommands.*;
import eu.goodyfx.mcraspisystem.utils.RaspiMessages;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import eu.goodyfx.mcraspisystem.utils.Settings;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

@Getter
public class AdminCommand implements CommandExecutor, TabCompleter {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    private final RaspiMessages data;

    private AdminDebugSubCommand debugSubCommand;

    private final List<SubCommand> subCommands = new ArrayList<>();

    public AdminCommand() {
        plugin.setCommand("admin", this, this);
        this.data = plugin.getModule().getRaspiMessages();
        addSubCommands();

    }

    public void addSubCommands() {
        debugSubCommand = new AdminDebugSubCommand(plugin);
        subCommands.add(new AdminHelpCommand(data, this));
        subCommands.add(new AdminSudoCommand(plugin));
        subCommands.add(debugSubCommand);
        subCommands.add(new AdminSkullSubCommand());
        subCommands.add(new AdminLootChestSubCommand(plugin));
        subCommands.add(new AdminCombineFileSubCommand(plugin));
        subCommands.add(new AdminRestoreAdminSubCommand(plugin));
        subCommands.add(new AdminResetDailyCommandSubCommand(plugin));
        subCommands.add(new AdminReloadSubCommand(plugin));
        subCommands.add(new AdminResetPlayerSubCommand());
        subCommands.add(new AdminPLHideSubCommand());
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (command.getName().equalsIgnoreCase("admin")) {
            List<String> results = new ArrayList<>();
            if (args.length == 1) {
                subCommands.forEach(val -> results.add(val.getLabel()));
                Collections.sort(results);
                return results;
            }
            if (args.length == 2&& args[0].equalsIgnoreCase("debug")) {
                return debugSubCommand.getActions().keySet().stream().toList();
            }
            if ((args.length > 2 && args.length < 6) && args[0].equalsIgnoreCase("skull")) {


                if (sender instanceof Player player) {
                    if (args.length == 3) {
                        results.add(String.valueOf(player.getLocation().getBlockX()));
                    }
                    if (args.length == 4) {
                        results.add(String.valueOf(player.getLocation().getBlockY()));
                    }
                    if (args.length == 5) {
                        results.add(String.valueOf(player.getLocation().getBlockZ()));
                    }

                }

                return results;
            }
        }
        return null;
    }

    /**
     * Handles the execution of the admin command from both players and the console.
     *
     * @param sender  The source of the command, which can be a player or the console.
     * @param command The command that was executed.
     * @param label   The alias of the command which was used.
     * @param args    The arguments passed to the command.
     * @return true if the command was successful, false otherwise.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (sender instanceof Player player) {
            //ARGS
            if (args.length > 0) {
                syntaxCheck(player, args);
                for (SubCommand subCommand : subCommands) {
                    if (args[0].equalsIgnoreCase(subCommand.getLabel())) {
                        return subCommand.commandPerform(new RaspiPlayer(player), args);
                    }
                }
            } else {
                player.sendRichMessage(data.getUsage("/admin help"));
            }
        } else {
            if (args.length == 2) {
                return teleportController(sender, args);
            }
        }
        return true;
    }


    private void syntaxCheck(Player player, String[] args) {
        for (SubCommand subCommand : subCommands) {

            if (args.length < subCommand.length() && args[0].equalsIgnoreCase(subCommand.getLabel())) {
                player.sendRichMessage("<gray><italic>" + subCommand.getDescription());
                player.sendRichMessage(plugin.getModule().getRaspiMessages().getUsage(subCommand.getSyntax()));
            }
        }
    }

    private boolean skull(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            PlayerProfile profile = Bukkit.createProfile("Voting");
            ItemStack stack = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) stack.getItemMeta();
            PlayerTextures textures = profile.getTextures();
            try {
                URL url = new URL("https://textures.minecraft.net/texture/" + args[1]);
                textures.setSkin(url);
                profile.setTextures(textures);
                meta.setPlayerProfile(profile);

                if (args[5] != null) {

                    StringBuilder builder = new StringBuilder();
                    for (int i = 5; i < args.length; i++) {
                        builder.append(args[i]).append(" ");
                    }
                    builder.setLength(builder.length() - 1);

                    meta.displayName(MiniMessage.miniMessage().deserialize(builder.toString()));
                }
                stack.setItemMeta(meta);
                if (isNumber(sender, args[2]) && isNumber(sender, args[3]) && isNumber(sender, args[4])) {
                    Location location = new Location(Bukkit.getWorld("world"), convertDouble(args[2]), convertDouble(args[3]), convertDouble(args[4]));
                    location.getWorld().dropItem(location, stack);
                }
                return true;
            } catch (MalformedURLException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while Getting HEAD!", e);
            }
        }
        return false;
    }

    private Double convertDouble(String arg) {
        return Double.parseDouble(arg);
    }

    private boolean isNumber(CommandSender player, String arg) {
        try {
            Integer.valueOf(arg);
            return true;
        } catch (NumberFormatException e) {
            player.sendMessage("Du Kek, Du musst eine Nummer eingeben!");
            return false;
        }
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

        RaspiPlayer raspiPlayer = new RaspiPlayer(player);

        plugin.getRaspiPlayers().forEach(all -> {
            if (all.hasSetting(Settings.MESSAGES)) {
                String output = String.format("%s%s hat Teleport %s benutzt.", data.getPrefix(), raspiPlayer.getName(), location);
                all.sendMessage(output);
            }
        });
        String log = String.format("Raspi-Teleport: %s hat Teleport %s benutzt", player.getName(), location);
        plugin.getLogger().info(log);
        return true;
    }

}
