package eu.goodyfx.mcraspi.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.core.database.RaspiSuggestions;
import eu.goodyfx.mcraspi.core.utils.InHeadSpectator;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A Moderation tool to watch new Players
 */
public class InHeadCommandContainer extends RaspiCommand {


    @Getter
    private static final Map<UUID, UUID> inHeadCache = new HashMap<>();
    private static final Map<UUID, Location> oldLocationCache = new HashMap<>();

    protected boolean blocked = true;

    public InHeadCommandContainer(McRaspiSystem plugin) {
        super(plugin);
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return command();
    }

    @Override
    public String getName() {
        return "inhead";
    }

    @Override
    public String getDescription() {
        return "";
    }

    public LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal(getName()).executes(this::executeDisable).then(Commands.argument("player", StringArgumentType.string()).suggests((context, builder) -> RaspiSuggestions.suggestOnlinePlayers(builder)).executes(this::executeInHead)).build();
    }


    private Integer executeDisable(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getExecutor() instanceof Player player)) {
            return 1;
        }

        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        if (inHeadCache.containsKey(raspiPlayer.getUUID())) {
            stopInHead(getPlugin(), raspiPlayer);
            return 1;
        } else {
            raspiPlayer.sendMessage("<red>Du bist derzeit noch nicht im InHead", true);
        }

        return Command.SINGLE_SUCCESS;
    }


    private Integer executeInHead(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getExecutor() instanceof Player player)) {
            return 1;
        }
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);

        String targetPlayerName = context.getArgument("player", String.class);

        if (player.getName().equals(targetPlayerName)) {
            raspiPlayer.sendMessage("<red>Bitte versuch nicht in dich selber zu schauen.<br><i><gray>Wir sind in keiner Therapiestunde....<br><red>Nimm bitte einen anderen Spieler.", true);
            return 1;
        }

        Player target = Bukkit.getPlayer(targetPlayerName);

        if (target == null) {
            raspiPlayer.sendMessage("<red>Der Spieler muss online sein.", true);
            return 1;
        }

        if (inHeadCache.containsKey(player.getUniqueId()) && inHeadCache.get(player.getUniqueId()).equals(target.getUniqueId())) {
            stopInHead(getPlugin(), raspiPlayer);
            return 1;
        }

        startInHead(raspiPlayer, target);
        return Command.SINGLE_SUCCESS;

    }

    private void startInHead(RaspiPlayer observer, Player target) {
        Player player = observer.getPlayer();
        oldLocationCache.put(player.getUniqueId(), player.getLocation());
        target.hidePlayer(getPlugin(), player);
        player.setGameMode(GameMode.SPECTATOR);
        player.setSpectatorTarget(target);
        InHeadSpectator.sendFakePlayer(getPlugin(), observer, target);
        inHeadCache.put(player.getUniqueId(), target.getUniqueId());
        observer.sendMessage(String.format("Du beobachtest nun %s.", target.getName()), true);
    }

    public static void stopInHead(McRaspiSystem plugin, RaspiPlayer raspiPlayer) {
        Player player = raspiPlayer.getPlayer();
        player.setSpectatorTarget(null);
        player.teleport(oldLocationCache.get(player.getUniqueId()));
        player.setGameMode(GameMode.SURVIVAL);
        raspiPlayer.sendMessage("InHead beendet.", true);

        UUID uuid = getInHeadCache().get(player.getUniqueId());
        Player target = Bukkit.getPlayer(uuid);
        if (target != null) {
            target.showPlayer(plugin, player);
            removeFakePlayer(plugin, target);
            player.sendRichMessage(String.format("removed:%s from list", target));
        }

        inHeadCache.remove(player.getUniqueId());
        oldLocationCache.remove(player.getUniqueId());
    }

    public static void removeFakePlayer(McRaspiSystem plugin, Player player) {
        InHeadSpectator.removeFakePlayerG(plugin, player);
        Raspi.debugger().debug("REMOVED FAKE_PLAYER");
    }

}
