package eu.goodyfx.system.core.managers;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.PlayerValues;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WarteschlangenManager {

    private final McRaspiSystem plugin;
    private final UserManager userManager;

    public WarteschlangenManager(RaspiModuleManager moduleManager) {
        this.userManager = moduleManager.getUserManager();
        this.plugin = moduleManager.getPlugin();
    }


    /**
     * Gets the amount of max online Players
     *
     * @return The max online Players
     */
    public int getMaxPlayers() {
        return plugin.getConfig().getInt("warteschlange.maxPlayer");
    }

    public List<String> activeWorlds() {
        return plugin.getConfig().getStringList("warteschlange.activeWorlds");
    }


    //START API

    private final Map<UUID, Location> locationHashMap = new HashMap<>();
    public final Queue<UUID> playersQueue = new LinkedList<>();
    private final Map<UUID, Integer> queuePosition = new HashMap<>();
    public final List<UUID> players = new ArrayList<>();

    /**
     * Gets the PlayerQue per Plugin reboot
     *
     * @return The PlayerQue
     */
    public Map<UUID, Location> getLocationHashMap() {
        return this.locationHashMap;
    }

    public List<UUID> getQueuedPlayers() {
        return new ArrayList<>(this.playersQueue);
    }

    public void join(Player player) {
        // If max player
        if (getAffectedPlayers().size() - this.playersQueue.size() > getMaxPlayers()) {
            //FINISH
            if (!this.playersQueue.isEmpty() && !userManager.hasPersistantValue(player, PlayerValues.AFK)) {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (userManager.hasPersistantValue(all, PlayerValues.AFK)) {
                        addToQueue(all.getUniqueId(), all.getLocation());
                        setHeader();
                        break;
                    }
                }
            }


            if (!isQueue(player)) {
                addToQueue(player.getUniqueId(), player.getLocation());
            }

        }

    }

    public boolean isInActiveWorld(Player player) {
        return activeWorlds().contains(player.getWorld().getName());
    }

    /**
     * Returns if player is in Que
     *
     * @param player The Testing Player
     * @return player is in Que or nott
     */
    public boolean isQueue(Player player) {
        UUID uuid = player.getUniqueId();
        return this.playersQueue.contains(uuid);
    }

    public int queueSize() {
        if (!this.playersQueue.isEmpty()) {
            return this.playersQueue.size();
        }
        return 0;
    }

    public List<String> getActiveWorlds() {
        return plugin.getConfig().getStringList("Warteschlange.ACTIVE_WORLDS");
    }

    public void addToQueue(UUID uuid, Location location) {
        String world = Objects.requireNonNull(location.getWorld()).getName();
        Location waiting = plugin.getModule().getLocationManager().get("waiting");
        if (!world.equalsIgnoreCase(Objects.requireNonNull(waiting.getWorld()).getName())) {
            locationHashMap.put(uuid, location);
        }
        if (!this.playersQueue.isEmpty() && userManager.hasPersistantValue(Objects.requireNonNull(Bukkit.getPlayer(playersQueue.peek())), PlayerValues.AFK)) {
            UUID afkUUID = playersQueue.peek();
            this.playersQueue.remove(afkUUID);
            this.playersQueue.add(uuid);
            this.playersQueue.add(afkUUID);
            Objects.requireNonNull(Bukkit.getPlayer(uuid)).teleport(waiting, PlayerTeleportEvent.TeleportCause.PLUGIN);
            sendQueuePosition(Bukkit.getPlayer(uuid));
            return;
        }
        this.playersQueue.add(uuid);
        Bukkit.getPlayer(uuid).teleport(waiting);
        sendQueuePosition(Bukkit.getPlayer(uuid));
    }

    public void removeFromQueue(UUID uuid) {
        this.playersQueue.remove(uuid);
        locationHashMap.remove(uuid);
        queuePosition.remove(uuid);
        sendQueuePosition();
    }

    public void setHeader() {

        AtomicInteger afk = new AtomicInteger();
        Bukkit.getOnlinePlayers().forEach(all -> {
            if (userManager.hasPersistantValue(all, PlayerValues.AFK)) {
                afk.getAndIncrement();
            }

            if (userManager.getAfkContainer().size() > 0) {
                all.sendPlayerListHeaderAndFooter(Component.text("Spieler Online: " + Bukkit.getOnlinePlayers().size()
                        + "/" + getMaxPlayers() + " wartende Spieler " + plugin.getModule().getWarteschlangenManager().queueSize()).append(Component.newline()).append(Component.text("Spieler Abwesend: "
                        + afk)), Component.empty());
            } else {
                all.sendPlayerListHeaderAndFooter(Component.text("Spieler Online: " + Bukkit.getOnlinePlayers().size()
                        + "/" + getMaxPlayers() + " wartende Spieler " + plugin.getModule().getWarteschlangenManager().queueSize()), Component.empty());
            }

        });

    }

    /**
     * Queues The next Player
     */
    public void queue() {
        if (!playersQueue.isEmpty()) {

            if (getAffectedPlayers().size() - playersQueue.size() - 1 < getMaxPlayers()) {

                UUID uuid = playersQueue.poll();

                if (uuid == null) {
                    return;
                }

                Player player = Bukkit.getPlayer(uuid);

                if (player != null) {
                    if (locationHashMap.containsKey(uuid)) {
                        player.teleport(locationHashMap.get(uuid));
                    } else {
                        player.teleport(plugin.getModule().getLocationManager().get("spawn"));
                    }


                    player.sendRichMessage(plugin.getModule().getRaspiMessages().endWaiting());
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
                    sendQueuePosition();
                }
            }
        }
        setHeader();
    }


    /**
     * Sends The current Queue position to all Queue Players.
     */
    public void sendQueuePosition() {
        int id = 0;
        if (playersQueue.isEmpty()) {
            return;
        }
        for (UUID uuid : playersQueue) {
            id++;
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                if (queuePosition.get(uuid) != id) {

                    player.sendRichMessage(plugin.getModule().getRaspiMessages().getPosition(id));

                    queuePosition.put(uuid, id);
                }
            }
        }

    }

    /**
     * Check {@link WarteschlangenManager#getActiveWorlds()} contains Online Players World
     *
     * @return a list of players that are Affected
     */
    public List<Player> getAffectedPlayers() {
        List<Player> affectedPlayers = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(online -> {
            if (activeWorlds().contains(online.getWorld().getName())) {
                affectedPlayers.add(online);
            }
        });
        return affectedPlayers;
    }


    /**
     * Sends Queue Position to Player
     *
     * @param player The Specific Player
     */
    public void sendQueuePosition(Player player) {
        int id = 0;
        if (playersQueue.isEmpty()) {
            return;
        }
        for (UUID uuid : playersQueue) {
            id++;
            if (uuid.equals(player.getUniqueId())) {
                queuePosition.put(uuid, id);
                player.sendRichMessage(plugin.getModule().getRaspiMessages().getPosition(id));
            }
        }

        Bukkit.getOnlinePlayers().forEach(all -> {
            if (!all.getName().equalsIgnoreCase(player.getName())) {
                all.sendRichMessage(plugin.getModule().getRaspiMessages().broadcastPlayerInsert(player.getName()));
            }
        });


    }

}
