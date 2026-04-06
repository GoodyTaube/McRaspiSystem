package eu.goodyfx.system.pvp.utils;

import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.database.RaspiPlayer;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import java.util.*;

@Getter
public class PvPManager {

    private final List<UUID> activePvPPlayersCache = new ArrayList<>();

    private final Map<BlockVector, UUID> lavaPlaced = new HashMap<>();
    private final Map<BlockVector, UUID> firePlaced = new HashMap<>();

    private final List<Material> badBlocks = new ArrayList<>();


    public PvPManager() {
        initBadBlocks();
    }

    private void initBadBlocks() {
        badBlocks.add(Material.LAVA);
        badBlocks.add(Material.END_CRYSTAL);
        badBlocks.add(Material.TNT);
        badBlocks.add(Material.FIRE);
    }

    /**
     * Abfragen ob der Spieler das Target angreifen kann.
     *
     * @param player Der Angreifer
     * @param target Der FEIND
     * @return True wenn beide PvP aktiv haben.
     */
    public boolean canPvP(Player player, Player target) {
        return activePvPPlayersCache.contains(player.getUniqueId()) && activePvPPlayersCache.contains(target.getUniqueId());
    }

    /**
     * Sendet Fake Damage zu einen Spieler
     *
     * @param player Der Spieler welcher PVP AUS hat.
     */
    public void playDamageAnimation(Player player, Player damager) {
        if (!activePvPPlayersCache.contains(player.getUniqueId())) {
            player.playHurtAnimation(0);
            player.setVelocity(damager.getLocation().getDirection().multiply(0.2d));
        }
    }

    public void sendCanceledDamageWarning(Player hurt, Player damager) {
        playDamageAnimation(hurt, damager);
        if (!hasPvPActive(damager)) {
            damager.sendActionBar(MiniMessage.miniMessage().deserialize("<red>Du hast kein PvP aktiviert."));
            return;
        }
        damager.sendActionBar(MiniMessage.miniMessage().deserialize(String.format("<red>%s <red>hat kein PvP aktiviert.", Raspi.playerLifeCycleService().getRaspiPlayer(hurt).getColorName())));
    }


    private boolean hasPvPActive(Player player) {
        return activePvPPlayersCache.contains(player.getUniqueId());
    }

    public void handleToggle(RaspiPlayer raspiPlayer) {
        UUID uuid = raspiPlayer.getUUID();
        if (activePvPPlayersCache.contains(uuid)) {
            activePvPPlayersCache.remove(uuid);
            raspiPlayer.sendMessage("<green>Du hast dein PvP deaktiviert.", true);
        } else {
            activePvPPlayersCache.add(uuid);
            raspiPlayer.sendMessage("<red>Du hast dein PvP aktiviert.", true);
        }
    }


}
