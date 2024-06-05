package eu.goodyfx.mcraspisystem.commands.subcommands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import eu.goodyfx.goodysutilities.GoodysUtilities;
import eu.goodyfx.goodysutilities.commands.SubCommand;
import eu.goodyfx.goodysutilities.utils.RaspiPlayer;

public class AdminChatSubCommand extends SubCommand {

    private final GoodysUtilities plugin;

    public AdminChatSubCommand(GoodysUtilities plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getLabel() {
        return "chat";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        PacketContainer container = new PacketContainer(PacketType.Play.Client.CHAT);
        container.getMessageSignatures().writeDefaults();
        container.getStrings().write(0, "dwhd");
        plugin.getProtocolManager().sendServerPacket(player.getPlayer(), container);
        return true;
    }
}
