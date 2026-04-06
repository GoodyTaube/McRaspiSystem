package eu.goodyfx.system.core.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.database.RaspiPlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class InHeadSpectator {

    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
    private static final Map<UUID, WrappedGameProfile> fakePlayerCache = new HashMap<>();

    /**
     * Erstellt ein Fake Player für ein Target.
     * Der observer wird dabei geklont und erneut in die Tablist geladen.
     *
     * @param plugin   Das Hauptsystem
     * @param observer Der Beobachter
     * @param target   Das Target
     */
    public static void sendFakePlayer(McRaspiSystem plugin, RaspiPlayer observer, Player target) {
        ProtocolManager manager = plugin.getHookManager().getProtocolManager();
        PacketContainer container = manager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        EnumSet<EnumWrappers.PlayerInfoAction> actions = EnumSet.of(
                EnumWrappers.PlayerInfoAction.ADD_PLAYER,
                EnumWrappers.PlayerInfoAction.UPDATE_LISTED,
                EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME
        );
        //Die benötigten Packets in das Packet schreben.
        container.getPlayerInfoActions().write(0, actions);

        //PlayerData für die Tab zusammensetzten
        PlayerInfoData data = new PlayerInfoData(
                generateFakeProfile(observer, target).getUUID(),
                21,
                true,
                EnumWrappers.NativeGameMode.SURVIVAL,
                generateFakeProfile(observer, target),
                WrappedChatComponent.fromText(getPlayerDisplayName(observer))
        );
        container.getPlayerInfoDataLists().write(1, Collections.singletonList(data));
        manager.sendServerPacket(target, container);
    }



    /**
     * Erstellt ein Fake Player für ein Target.
     * Der observer wird dabei geklont und erneut in die Tablist geladen.
     *
     * @param plugin   Das Hauptsystem
     * @param target   Das Target
     */
    public static void updatePlayerList(McRaspiSystem plugin,Player target) {
        ProtocolManager manager = plugin.getHookManager().getProtocolManager();
        PacketContainer container = manager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        EnumSet<EnumWrappers.PlayerInfoAction> actions = EnumSet.of(
                EnumWrappers.PlayerInfoAction.UPDATE_LISTED,
                EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME
        );
        //Die benötigten Packets in das Packet schreben.
        container.getPlayerInfoActions().write(0, actions);
        manager.sendServerPacket(target, container);
    }


    /**
     * Get or Generate a Wrapped Game Profile and Store it
     *
     * @param raspiPlayer The Original Player
     * @return The Fake Player Profile with random UUID
     */
    private static WrappedGameProfile generateFakeProfile(RaspiPlayer raspiPlayer, Player target) {
        UUID key = target.getUniqueId(); // 🔥 EINHEITLICH

        if (fakePlayerCache.containsKey(key)) {
            return fakePlayerCache.get(key);
        }

        WrappedGameProfile original = WrappedGameProfile.fromPlayer(raspiPlayer.getPlayer());
        WrappedGameProfile fake = new WrappedGameProfile(UUID.randomUUID(), original.getName());

        fake.getProperties().putAll(original.getProperties());

        fakePlayerCache.put(key, fake);
        return fake;
    }


    /**
     * Get the Displayed Displayname of a RaspiPlayer
     * Includes color prefix and suffix
     *
     * @param raspiPlayer The RaspiPlayer
     * @return The Legacy Minecraft DisplayName
     */
    private static String getPlayerDisplayName(RaspiPlayer raspiPlayer) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(MiniMessage.miniMessage().deserialize(raspiPlayer.getDisplayName())).replace("&", "§");
    }

    /**
     * Löscht den FakeSpieler wieder für das Target
     *
     * @param plugin McRaspiSystem
     * @param target Das Target welches den Fake Spieler sieht.
     */
    public static void removeFakePlayer(McRaspiSystem plugin, Player target) {
        ProtocolManager manager = plugin.getHookManager().getProtocolManager();
        PacketContainer container = manager.createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);

        container.getUUIDLists().write(0, Collections.singletonList(fakePlayerCache.get(target.getUniqueId()).getUUID()));
        manager.sendServerPacket(target, container);
    }

    public static void removeFakePlayerG(McRaspiSystem plugin, Player target) {
        ProtocolManager manager = plugin.getHookManager().getProtocolManager();

        WrappedGameProfile profile = fakePlayerCache.remove(target.getUniqueId());
        if (profile == null) return;

        PacketContainer remove = manager.createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);
        remove.getUUIDLists().write(0, Collections.singletonList(profile.getUUID()));

        manager.sendServerPacket(target, remove);
    }

}
