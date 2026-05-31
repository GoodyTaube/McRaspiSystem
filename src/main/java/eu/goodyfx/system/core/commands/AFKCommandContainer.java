package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.database.RaspiPlayer;
import eu.goodyfx.system.core.events.PlayerAFKEvent;
import eu.goodyfx.system.core.managers.LocationManager;
import eu.goodyfx.system.core.managers.WarteschlangenManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AFKCommandContainer {

    private final LocationManager locationManager;
    private final WarteschlangenManager warteschlangenManager;

    @Getter
    private static final Map<UUID, Integer> playerIDLE = new ConcurrentHashMap<>();

    public AFKCommandContainer(McRaspiSystem plugin) {
        this.locationManager = plugin.getModule().getLocationManager();
        this.warteschlangenManager = plugin.getModule().getWarteschlangenManager();
    }

    public LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("afk").executes(this::afk).build();
    }

    private int afk(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player dummy)) {
            context.getSource().getSender().sendRichMessage("Dieser Command kann nur von Spielern genutzt werden.");
            return 1;
        }
        RaspiPlayer player = Raspi.playerLifeCycleService().getRaspiPlayer(dummy);
        Location playerLocation = dummy.getLocation();
        if (player.isAfk()) {
            player.setAfk(false);
            warteschlangenManager.setHeader();
            dummy.sendActionBar(MiniMessage.miniMessage().deserialize("<red>Du bist nicht mehr AFK"));
            player.nameController.setPlayerList();
            dummy.setSleepingIgnored(false);
            Raspi.debugger().info(String.format("AFK Disabled for %s SLEEPING_IGNORED:FALSE", dummy.getName()));
            return 1;
        }
        player.setAfk(true);
        if (!playerLocation.getWorld().getName().equalsIgnoreCase(locationManager.getWorldName("waiting"))
                && warteschlangenManager.queueSize() > 0 && !warteschlangenManager.playersQueue.contains(dummy.getUniqueId())) {
            warteschlangenManager.queue();
            warteschlangenManager.addToQueue(dummy.getUniqueId(), playerLocation);
        }
        warteschlangenManager.setHeader();
        dummy.sendActionBar(MiniMessage.miniMessage().deserialize("<yellow>Du bist nun AFK"));
        player.nameController.setPlayerList();

        PlayerAFKEvent afkEvent = new PlayerAFKEvent(player);
        Bukkit.getPluginManager().callEvent(afkEvent);
        dummy.setSleepingIgnored(true);
        Raspi.debugger().info(String.format("AFK Disabled for %s SLEEPING_IGNORED:TRUE", dummy.getName()));
        return Command.SINGLE_SUCCESS;
    }

}
