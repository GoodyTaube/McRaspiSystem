package eu.goodyfx.mcraspisystem.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.InHeadCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Level;

public class InHeadSpectator {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    public InHeadSpectator() {
        ProtocolManager manager = plugin.getHookManager().getProtocolManager();
        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();

                PacketContainer packet = event.getPacket();

                //Index define and Gamemode Definition
                int index = 1;
                EnumWrappers.NativeGameMode gameMode = EnumWrappers.NativeGameMode.SURVIVAL;


                try {
                    List<PlayerInfoData> data = packet.getPlayerInfoDataLists().read(index);
                    for (int i = 0; i < data.size(); i++) {
                        PlayerInfoData dataManu = data.get(i);
                        if (dataManu.getGameMode() == EnumWrappers.NativeGameMode.SPECTATOR && !player.getUniqueId().equals(dataManu.getProfile().getUUID()) && InHeadCommand.getInHeadContainer().containsKey(player.getUniqueId())) {
                            data.set(i, new PlayerInfoData(dataManu.getProfile(), dataManu.getLatency(), gameMode, dataManu.getDisplayName()));
                        }
                    }
                    packet.getPlayerInfoDataLists().write(index, data);
                } catch (NullPointerException e) {
                    plugin.getLogger().log(Level.SEVERE, "index falsch", e);
                    plugin.getLogger().info(packet.getPlayerInfoDataLists().toString());

                }
            }
        });
    }


}
