package eu.goodyfx.mcraspisystem.events;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.utils.Warnings;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarnListeners implements Listener {


    private static Map<UUID, Map<EntityType, Integer>> mobKill = new HashMap<>();
    private static Map<UUID, Boolean> sendWarning = new HashMap<>();
    private final McRaspiSystem plugin;

    public WarnListeners(McRaspiSystem plugin) {
        this.plugin = plugin;
        plugin.setListeners(this);
    }


    @EventHandler
    public void onVillagerKill(EntityDeathEvent deathEvent) {
        villager(deathEvent);
    }

    private void villager(EntityDeathEvent deathEvent) {
        if (deathEvent.getEntity().getKiller() != null && deathEvent.getEntity() instanceof Villager villager) {
            Player player = deathEvent.getEntity().getKiller();
            if (mobKill.containsKey(player.getUniqueId())) {
                for (EntityType entity : mobKill.get(player.getUniqueId()).keySet()) {
                    if (entity.equals(villager.getType())) {
                        mobKill.get(player.getUniqueId()).compute(villager.getType(), (k, count) -> count + 1);
                    }
                }
            } else {
                Map<EntityType, Integer> map = new HashMap<>();
                map.put(EntityType.VILLAGER, 1);
                mobKill.put(player.getUniqueId(), map);
            }
            if (!sendWarning.containsKey(player.getUniqueId())) {
                sendWarning.put(player.getUniqueId(), false);
            }
            sendInfo(player);
        }
    }


    public void sendInfo(Player player) {
        if (!sendWarning.get(player.getUniqueId())) {
            sendWarning.put(player.getUniqueId(), true);
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> plugin.getRaspiPlayers().forEach(all -> {
                if (all.getPlayer().isPermissionSet("system.team")) {
                    all.sendMessage(Warnings.CRITICAL.getPrefix() + " der Spieler: " + player.getName() + " hat innerhalb 1 Min " + mobKill.get(player.getUniqueId()).get(EntityType.VILLAGER) + " Villager getötet!");
                }
            }), 20L * 60);


            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> plugin.getRaspiPlayers().forEach(all -> {
                all.sendMessage(Warnings.CRITICAL.getPrefix() + " <gray>Der Spieler: <red>" + player.getName() + " <gray>hat innerhalb 5 Min <yellow>" + mobKill.get(player.getUniqueId()).get(EntityType.VILLAGER) + " Villager <gray>getötet!");
                sendWarning.remove(player.getUniqueId());
                mobKill.get(player.getUniqueId()).remove(EntityType.VILLAGER);
            }), 20L * 60 * 5);

        }


    }


}
