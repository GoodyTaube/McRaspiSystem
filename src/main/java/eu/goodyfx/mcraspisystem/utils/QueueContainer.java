package eu.goodyfx.mcraspisystem.utils;

import org.bukkit.Location;

import java.util.Queue;
import java.util.UUID;

public class QueueContainer {

    private final RaspiPlayer player;
    private final Location location;
    private Integer queuePosition;

    public QueueContainer(RaspiPlayer player, final Queue<UUID> queue) {
        this.player = player;
        this.location = player.getPlayer().getLocation();
        this.queuePosition = getQueuePosition(queue);
    }

    public void setPosition(Queue<UUID> current){
        this.queuePosition = getQueuePosition(current);
    }

    public Integer getQueuePosition(){
        return queuePosition;
    }

    private Integer getQueuePosition(Queue<UUID> current){
        int id = 0;
        for(UUID users : current){
            id++;
            if(users.equals(player.getUUID())){
                return id;
            }
        }
        return 0;
    }

    /**
     * Get The {@link RaspiPlayer} involved in this Queue
     *
     * @return The Queued Player
     */
    public RaspiPlayer getPlayer() {
        return player;
    }

    /**
     * Get the orin Location
     *
     * @return Saved Location
     */
    public Location getLocation() {
        return location;
    }
}
