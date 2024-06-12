package eu.goodyfx.mcraspisystem.managers;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.utils.QueueContainer;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class RaspiQuee {

    private final McRaspiSystem plugin;

    private Map<UUID, QueueContainer> playerContainer = new HashMap<>();
    private Queue<UUID> queue = new LinkedList<>();
    private List<World> activeWorlds = new ArrayList<>();
    private Integer maxPlayers = 0;

    private final String MAX_PLAYER_VALUE_PATH = "warteschlange.maxPlayer";
    private final String ACTIVE_WORLDS_PATH = "warteschlange.activeWorlds";


    public RaspiQuee(McRaspiSystem plugin) {
        this.plugin = plugin;
        setUp();
    }

    /**
     * Method to setting up this class
     */
    private void setUp() {
        if (plugin.getConfig().contains(MAX_PLAYER_VALUE_PATH)) {
            this.maxPlayers = plugin.getConfig().getInt(MAX_PLAYER_VALUE_PATH);
        }
        if (plugin.getConfig().contains(ACTIVE_WORLDS_PATH)) {
            List<String> worlds = plugin.getConfig().getStringList(ACTIVE_WORLDS_PATH);
            for (String value : worlds) {
                activeWorlds.add(Bukkit.getWorld(value));
            }
        }
    }

    public void addToQue(RaspiPlayer player) {
        queue.add(player.getUUID());
        playerContainer.put(player.getUUID(), new QueueContainer(player, queue));
        sendQueuePosition(player);
    }

    public void sendQueuePosition(RaspiPlayer player) {
        UUID uuid = player.getUUID();
        player.sendMessage("Deine Position ist " + playerContainer.get(uuid).getQueuePosition());
    }

    public void queue() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(queue.poll());
        if (player.isOnline()) {
            player.getPlayer().teleport(playerContainer.get(player.getUniqueId()).getLocation());
            player.getPlayer().sendActionBar(MiniMessage.miniMessage().deserialize("Du kannst nun Spielen!"));
            playerContainer.remove(player.getUniqueId());
        }

        //Send Update Message
        for (UUID per : playerContainer.keySet()) {
            int oldPos = playerContainer.get(per).getQueuePosition();
            playerContainer.get(per).setPosition(queue);
            if(playerContainer.get(per).getQueuePosition() == oldPos){
                return;
            }
            sendQueuePosition(new RaspiPlayer(plugin, per));
        }
    }

    public List<Player> getAllowedPlayers(){
        List<Player> players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(all ->{
            if(!queue.contains(all.getUniqueId())){
                players.add(all);
            }
        });
        return players;
    }

    /**
     * Removes Player from Queue if needed
     *
     * @param uuid The user UUID
     */
    public void remove(UUID uuid) {
        queue.remove(uuid);
        playerContainer.remove(uuid);
        queue();
    }
}
