package eu.goodyfx.mcraspisystem.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import eu.goodyfx.goodysutilities.GoodysUtilities;
import eu.goodyfx.mcraspisystem.McRaspiSystem;
import org.bukkit.entity.Player;

import java.util.List;

public record SpectatorName(McRaspiSystem plugin) {


    public void specName() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                PacketContainer packet = event.getPacket();

                if (player.isPermissionSet("group.default")) {
                    return;
                }

                EnumWrappers.NativeGameMode gameMode = EnumWrappers.NativeGameMode.SURVIVAL;
                List<PlayerInfoData> data = packet.getPlayerInfoDataLists().read(1);
                for (int i = 0; i < data.size(); i++) {
                    PlayerInfoData data1 = data.get(i);

                    if (data1.getGameMode() == EnumWrappers.NativeGameMode.SPECTATOR && !event.getPlayer().getUniqueId().equals(data1.getProfile().getUUID())) {
                        data.set(i, new PlayerInfoData(data1.getProfile(), data1.getLatency(), gameMode, data1.getDisplayName()));
                    }
                }
                packet.getPlayerInfoDataLists().write(1, data);
            }
        });

    }

}
