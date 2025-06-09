package eu.goodyfx.mcraspisystem.commands.subcommands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.SubCommand;
import eu.goodyfx.mcraspisystem.managers.CommandManager;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import org.bukkit.plugin.java.JavaPlugin;

public class AdminPLHideSubCommand extends SubCommand {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    @Override
    public String getLabel() {
        return "plhide";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return "/admin plhide [<action>]";
    }

    @Override
    public int length() {
        return 2;
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if (args.length == 1) {
            player.sendMessage("/admin plhide list", true);
        }
        list(player, args);
        getCommands(player, args);
        getTabs(player, args);
        getErben(player, args);
        reload(player, args);
        return true;
    }

    private void reload(RaspiPlayer player, String[] args) {
        if (args.length == 2 && args[1].equalsIgnoreCase("reload")) {
            plugin.getModuleManager().getCommandManager().reload(player);
        }
    }

    private void list(RaspiPlayer player, String[] args) {
        if (args.length == 2 && args[1].equalsIgnoreCase("list")) {
            StringBuilder builder = new StringBuilder("<green>Eine liste aller Gruppen:");
            plugin.getModule().getCommandManager().getAllGroups().forEach(group -> builder.append(group).append(","));
            builder.setLength(builder.length() - 1);
            player.sendMessage(builder.toString(), true);
        }
    }

    private void getCommands(RaspiPlayer player, String[] args) {
        if (args.length == 3 && args[1].equalsIgnoreCase("commands")) {
            StringBuilder builder = new StringBuilder("<green>Eine Liste der gegebenen Commands: ");
            CommandManager commandManager = plugin.getModule().getCommandManager();
            commandManager.reload();
            if (commandManager.groupExists(args[2])) {
                commandManager.getList(args[2], CommandManager.CommandManagerPaths.COMMANDS).forEach(arg -> builder.append(arg).append(","));
                builder.setLength(builder.length() - 1);
                player.sendMessage(builder.toString(), true);
            } else player.sendMessage("<red>Die Gruppe gibt es nicht.", true);

        }
    }

    private void getTabs(RaspiPlayer player, String[] args) {
        if (args.length == 3 && args[1].equalsIgnoreCase("tabs")) {
            StringBuilder builder = new StringBuilder("<green>Eine Liste der gegebenen Tab-Commands: ");
            CommandManager commandManager = plugin.getModule().getCommandManager();
            commandManager.reload();
            if (commandManager.groupExists(args[2])) {
                if (commandManager.get(args[2], CommandManager.CommandManagerPaths.TAB_COMPLETE_IMPLEMENT, Boolean.class)) {
                    commandManager.getList(args[2], CommandManager.CommandManagerPaths.COMMANDS).forEach(arg -> builder.append(arg).append(","));
                }
                commandManager.getList(args[2], CommandManager.CommandManagerPaths.TAB_COMPLETE_COMMANDS).forEach(arg -> builder.append(arg).append(","));
                builder.setLength(builder.length() - 1);
                player.sendMessage(builder.toString(), true);
            } else player.sendMessage("<red>Die Gruppe gibt es nicht.", true);

        }
    }

    private void getErben(RaspiPlayer player, String[] args) {
        if (args.length == 3 && args[1].equalsIgnoreCase("erben")) {
            StringBuilder builder = new StringBuilder("<green>Eine Liste der gegebenen erben: ");
            CommandManager commandManager = plugin.getModule().getCommandManager();
            commandManager.reload();
            if (commandManager.groupExists(args[2])) {
                commandManager.getList(args[2], CommandManager.CommandManagerPaths.ERBEN_AUS).forEach(arg -> builder.append(arg).append(","));
                builder.setLength(builder.length() - 1);
                player.sendMessage(builder.toString(), true);
            } else player.sendMessage("<red>Die Gruppe gibt es nicht.", true);

        }
    }
}
