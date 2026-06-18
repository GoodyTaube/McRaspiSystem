package eu.goodyfx.mcraspi.core.security;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import org.bukkit.entity.Player;

public class PreventPacketTeleport {

    public PreventPacketTeleport(McRaspiSystem plugin) {
        ProtocolManager manager = plugin.getHookManager().getProtocolManager();
        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.POSITION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (player == null) {
                    return;
                }
                RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
                if (raspiPlayer.userData().getAllowed()) {
                    return;
                }
                //PLAYER NOT FTP
                double maxDistance = 15.0D;
                var spawn = player.getWorld().getSpawnLocation();
                double x = event.getPacket().getDoubles().read(0);
                double z = event.getPacket().getDoubles().read(2);

                double distanceX = Math.abs(x - spawn.getX());
                double distanceZ = Math.abs(z - spawn.getZ());

                if (distanceX > maxDistance || distanceZ > maxDistance) {
                    event.setCancelled(true);
                    //TODO send Discord NOTIFY

                    player.getScheduler().run(plugin, (task) -> {
                        player.teleport(spawn);
                    }, null);

                }

                super.onPacketReceiving(event);

            }
        });

    }

}
