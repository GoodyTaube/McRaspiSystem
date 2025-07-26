package eu.goodyfx.system.core.events;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.managers.CommandManager;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * The CommandListeners class serves as an event listener for various player command and inventory events.
 * Its primary purpose is to control and restrict certain player actions based on their permissions.
 */
public class CommandListeners implements Listener {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    private final Map<UUID, List<String>> commandContainer = new HashMap<>();

    public CommandListeners() {
        plugin.setListeners(this);
    }

    private final Map<UUID, Integer> playerContainer = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommandSend(PlayerCommandSendEvent event) {
        RaspiPlayer raspiPlayer = plugin.getRaspiPlayer(event.getPlayer());
        Collection<String> commandCollections = event.getCommands(); //die liste der commands auf dem server
        commandHide(event);
        if (raspiPlayer.hasPermission("group.default") && !raspiPlayer.hasPermission("group.spieler")) { // Checken ob der Spieler schon registriert ist
            plugin.getDebugger().info("CLEARING ALL COMMANDS");
            commandCollections.clear();
        }
    }


    private void commandHide(PlayerCommandSendEvent event) {
        RaspiPlayer player = plugin.getRaspiPlayer(event.getPlayer());
        if (player.hasPermission("*")) {
            return;
        }
        Set<String> command = new HashSet<>();
        CommandManager manager = plugin.getModuleManager().getCommandManager();
        plugin.getModule().getCommandManager().getAllGroups().forEach(group -> {
            if (player.hasPermission(String.format("group.%s", group))) {
                command.addAll(manager.getList(group, CommandManager.CommandManagerPaths.TAB_COMPLETE_COMMANDS));
                if (manager.get(group, CommandManager.CommandManagerPaths.TAB_COMPLETE_IMPLEMENT, Boolean.class)) {
                    plugin.getDebugger().info("IMPLEMENT ALL COMMANDS");
                    command.addAll(manager.getList(group, CommandManager.CommandManagerPaths.COMMANDS));
                }
            }
        });
        event.getCommands().removeIf(cmd -> !command.contains(cmd));


    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommand(PlayerCommandPreprocessEvent commandEvent) {
        String[] command = commandEvent.getMessage().split(" ");
        RaspiPlayer player = new RaspiPlayer(commandEvent.getPlayer());
        commandControlUnit(commandEvent);
        checkMuted(commandEvent, command); //Wichtig

    }


    public void commandControlUnit(PlayerCommandPreprocessEvent event) {
        RaspiPlayer player = plugin.getRaspiPlayer(event.getPlayer());
        if ((player.hasPermission("*")) || (player.getPlayer().isOp())) {
            return;
        }

        String[] command = event.getMessage().split(" ");
        String commandStarter = command[0].replace("/", "");
        CommandManager commandManager = plugin.getModule().getCommandManager();
        Set<String> availableCommands = new HashSet<>();
        commandManager.getPlayerGroups(player).forEach(group -> {
            availableCommands.addAll(commandManager.getList(group, CommandManager.CommandManagerPaths.COMMANDS));
        });

        if (!availableCommands.contains(commandStarter)) {
            event.setCancelled(true);
            player.sendMessage("Der Command Existiert nicht", true);
        }
    }


    /**
     * Check if PLayer is Muted and Tried to use forbidden Commands.
     */
    private void checkMuted(PlayerCommandPreprocessEvent commandEvent, String[] command) {
        if ((command[0].equalsIgnoreCase("/msg") || command[0].equalsIgnoreCase("/tell") || command[0].equalsIgnoreCase("/me")) && (plugin.getModule().getUserManager().isMuted(commandEvent.getPlayer()))) {
            commandEvent.getPlayer().sendRichMessage("<red><hover:show_text:'<red>Sei einfach Leise bitte.<gray> Kuss'>Du bist Stumm. Das geht so nicht..");
            commandEvent.setCancelled(true);
        }
    }
}
