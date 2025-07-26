package eu.goodyfx.system.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class to reduce much codeJunk and have stable Control over RaspiPlayers
 */
public class RaspiPlayerManager {

    /**
     * The RaspiPlayer Container by Bukkit#Player UUID
     */
    private final Map<UUID, RaspiPlayer> raspiPlayers = new ConcurrentHashMap<>();

    /**
     * Get the saved equal RaspiPlayer by Bukkit:Player
     * @param bukkitPlayer The plain Bukkit Player
     * @return The RaspiPlayer equal to Bukkit#Player
     */
    public RaspiPlayer get(Player bukkitPlayer) {
        return raspiPlayers.computeIfAbsent(bukkitPlayer.getUniqueId(), uuid -> new RaspiPlayer(bukkitPlayer));
    }

    public Optional<RaspiPlayer> get(UUID uuid) {
        return Optional.ofNullable(raspiPlayers.get(uuid));
    }

    public void remove(Player bukkitPlayer) {
        raspiPlayers.remove(bukkitPlayer.getUniqueId());
    }

    public Collection<RaspiPlayer> getOnlineRaspiPlayers() {
        return Bukkit.getOnlinePlayers().stream().map(this::get).toList();
    }

    public void clear() {
        raspiPlayers.clear();
    }

}
