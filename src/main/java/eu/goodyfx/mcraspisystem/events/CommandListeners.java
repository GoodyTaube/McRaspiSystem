package eu.goodyfx.mcraspisystem.events;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandListeners implements Listener {

    private final McRaspiSystem plugin;

    public CommandListeners(McRaspiSystem plugin) {
        this.plugin = plugin;
        plugin.setListeners(this);
    }

    private final Map<UUID, Integer> playerContainer = new HashMap<>();

    @EventHandler
    public void onCommandSend(PlayerCommandSendEvent playerCommandSendEvent) {
        Player player = playerCommandSendEvent.getPlayer();
        Collection<String> commandCollections = playerCommandSendEvent.getCommands();
        if (player.isPermissionSet("group.default") && !player.isPermissionSet("group.spieler")) {
            commandCollections.clear();
        }
    }

    @EventHandler
    public void onDefault(InventoryOpenEvent event) {
        if (event.getPlayer().isPermissionSet("group.default") && !event.getPlayer().isPermissionSet("group.spieler")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent commandEvent) {
        String[] command = commandEvent.getMessage().split(" ");
        RaspiPlayer player = new RaspiPlayer(plugin, commandEvent.getPlayer());
        if ((command[0].equalsIgnoreCase("/msg") || command[0].equalsIgnoreCase("/tell") || command[0].equalsIgnoreCase("/me")) && (plugin.getModule().getUserManager().isMuted(commandEvent.getPlayer()))) {
            commandEvent.getPlayer().sendRichMessage("<red><hover:show_text:'<red>Sei einfach Leise bitte.<gray> Kuss'>Du bist Stumm. Das geht so nicht..");
            commandEvent.setCancelled(true);

        }
        if (commandEvent.getPlayer().isOp() && command[0].equalsIgnoreCase("/rl") || command[0].equalsIgnoreCase("/reload") || command[0].startsWith("/bukkit")) {
            commandEvent.setCancelled(true);
            if (playerContainer.containsKey(player.getUUID())) {
                Integer amount = playerContainer.get(player.getUUID());
                if (amount == 3) {
                    player.sendMessage("[Server] <red>Reicht doch jetzt...<br><gray><italic>Schreib Goody an!");
                    return;

                } else {
                    amount = amount + 1;
                    playerContainer.put(player.getUUID(), amount);
                }
            } else {
                playerContainer.put(player.getUUID(), 1);
            }
            player.sendMessage("<red>Dieser Command wurde von einen System Administrator Blockiert.<br><gray><italic>Bitte Kontaktiere Goody für weitere Infos!");
        }
    }

}
