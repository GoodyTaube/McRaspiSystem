package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.database.RaspiUser;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class RequestCommandContainer {


    private static final Map<UUID, UUID> locationMap = new HashMap<>();

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("request")
                .executes(RequestCommandContainer::executeCommandHelp)
                .then(Commands.literal("kick").then(Commands.argument("player", StringArgumentType.string()).executes(RequestCommandContainer::executeKick)))
                .then(Commands.literal("accept").then(Commands.argument("player", StringArgumentType.string()).executes(RequestCommandContainer::executeRequestAccept)))
                .then(Commands.literal("deny").then(Commands.argument("player", StringArgumentType.string()).then(Commands.argument("reason", StringArgumentType.string()).suggests(RequestCommandContainer::getReasonSuggest).executes(RequestCommandContainer::executeRequestDeny))))
                .then(Commands.literal("tp").then(Commands.argument("player", StringArgumentType.string()).executes(RequestCommandContainer::executeTPRequest)))
                .then(Commands.literal("--confirm").executes(RequestCommandContainer::executeConfirm)).build();
    }

    private static CompletableFuture<Suggestions> getReasonSuggest(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        builder.suggest("\"Spieler ist zu Jung\"");
        builder.suggest("\"Spieler scheint ein Hacker zu sein\"");
        builder.suggest("\"Spieler beleidigt im Chat\"");
        builder.suggest("\"Spieler beleidigt im Discord\"");
        builder.suggest("\"Spieler will nur stress\"");
        builder.suggest("\"\"", MessageComponentSerializer.message().serialize(MiniMessage.miniMessage().deserialize("<rainbow><shadow:#000000>Eigener Grund in \"\" setzten")));
        return builder.buildFuture();
    }

    private static void playerAllow(SimpleDateFormat dateFormat, Player player, RaspiUser targetPlayer) {
        targetPlayer.setState(true);
        targetPlayer.setAllowed_since(dateFormat.format(new Date(System.currentTimeMillis())));
        targetPlayer.setAllowed_by(player.getName());
        targetPlayer.setDeny_reason(null);
        targetPlayer.setDenied_by(null);
        Raspi.debugger().info("ALLOWED PLAYER:: " + targetPlayer.getUsername());

    }

    private static void playerDeny(String denyReason, Player player, RaspiUser targetPlayer) {
        targetPlayer.setState(false);
        targetPlayer.setAllowed_by(null);
        targetPlayer.setAllowed_since(null);
        targetPlayer.setDenied_by(player.getName());
        targetPlayer.setDeny_reason(denyReason);
        Raspi.debugger().info("Denied PLAYER:: " + targetPlayer.getUsername());
    }

    private static int executeConfirm(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getExecutor() instanceof Player dummy)) {
            return Command.SINGLE_SUCCESS;
        }
        RaspiPlayer player = Raspi.players().get(dummy);
        if (!locationMap.containsKey(dummy.getUniqueId())) {
            player.sendMessage("<red>Fehlerhafte Anfrage.", true);
            return Command.SINGLE_SUCCESS;
        }
        Player targetDummy = Bukkit.getPlayer(locationMap.get(player.getUUID()));
        if (targetDummy != null) {
            RaspiPlayer target = Raspi.players().get(targetDummy);
            targetDummy.teleport(player.getLocation());
            target.sendMessage(String.format("Du wurdest zu %s teleportiert", player.getColorName()), true);
        } else {
            player.sendMessage("<red>Die Bearbeitung konnte nicht Abgeschlossen werden.", true);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int executeTPRequest(final CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getExecutor() instanceof Player dummy)) {
            return Command.SINGLE_SUCCESS;
        }


        RaspiPlayer player = Raspi.players().get(dummy);
        Player target = Bukkit.getPlayer(context.getArgument("player", String.class));
        if (target != null) {
            locationMap.put(target.getUniqueId(), dummy.getUniqueId());
            Raspi.players().get(target).sendMessage(String.format("%s würde sich gerne zu dir Teleportieren.<br>            %s | %s", player.getColorName(), "<hover:show_text:'Klicke hier um die Anfrage Anzunehmen'><click:run_command:'/request --confirm'><green>Annehmen</click></hover>", "<hover:show_text:'Klicke hier um die Anfrage Abzulehnen'><click:run_command:'/request --deny'><red>Ablehnen</click></hover>"), true);
        } else {
            player.sendMessage(String.format("%s ist nicht online!", context.getArgument("player", String.class)));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int executeRequestDeny(final CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getExecutor() instanceof Player dummy)) {
            return Command.SINGLE_SUCCESS;
        }
        RaspiPlayer player = Raspi.players().get(dummy);
        OfflinePlayer target = Bukkit.getOfflinePlayer(context.getArgument("player", String.class));
        String reason = context.getArgument("reason", String.class);
        AtomicReference<String> allowed = new AtomicReference<>("<gray>Du hast %s <green>Erfolgreich <gray>abgelehnt.");
        if (target.isOnline()) {
            RaspiPlayer targetPlayer = Raspi.players().get(target.getUniqueId());
            playerDeny(reason, dummy, targetPlayer.getUser());

            allowed.set(String.format(allowed.get(), targetPlayer.getColorName()));

        } else {
            Raspi.players().getRaspiOfflinePlayer(target).thenAcceptAsync(raspiOfflinePlayer -> {
                if (raspiOfflinePlayer == null) {
                    player.sendMessage("Der Spieler hat noch nicht gespielt.", true);
                    return;
                }
                playerDeny(reason, dummy, raspiOfflinePlayer.getRaspiUser());
                allowed.set(String.format(allowed.get(), raspiOfflinePlayer.getRaspiUser().getColor() + raspiOfflinePlayer.getPlayer().getName()));
            }, runnable -> Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(McRaspiSystem.class), runnable));
        }
        player.sendMessage(allowed.get(), true);
        return Command.SINGLE_SUCCESS;
    }


    private static int executeRequestAccept(final CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getExecutor() instanceof Player dummy)) {
            return Command.SINGLE_SUCCESS;
        }
        RaspiPlayer player = Raspi.players().get(dummy);
        OfflinePlayer target = Bukkit.getOfflinePlayer(context.getArgument("player", String.class));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        AtomicReference<String> allowedA = new AtomicReference<>("<gray>Du hast %s <green>Erfolgreich <gray>Freigeschaltet.");
        if (target.isOnline()) {
            RaspiPlayer targetPlayer = Raspi.players().get(target.getUniqueId());
            playerAllow(simpleDateFormat, player.getPlayer(), targetPlayer.getUser());
            allowedA.set(String.format(allowedA.get(), targetPlayer.getColorName()));
        } else {
            Raspi.players().getRaspiOfflinePlayer(target).thenAcceptAsync(raspiOfflinePlayer -> {
                if (raspiOfflinePlayer == null) {
                    player.sendMessage("Der Spieler hat noch nicht gespielt.", true);
                    return;
                }
                playerAllow(simpleDateFormat, player.getPlayer(), raspiOfflinePlayer.getRaspiUser());
                allowedA.set(String.format(allowedA.get(), raspiOfflinePlayer.getRaspiUser().getColor() + raspiOfflinePlayer.getPlayer().getName()));
            }, runnable -> Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(McRaspiSystem.class), runnable));
        }
        player.sendMessage(allowedA.get(), true);
        return Command.SINGLE_SUCCESS;
    }


    private static int executeCommandHelp(final CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        if (sender instanceof Player dummy) {
            RaspiPlayer player = Raspi.players().get(dummy);
            String builder =
                    """
                            <gray>Request Commands:<br>
                            <green>- accept <player> <gray><i>#Spieler erlauben.<br>
                            <green>- deny <player> <grund> <gray><i>#Spieler ablehnen<br>
                            <green>- kick <player> <gray><i>#Spieler Kicken<br>
                            <green>- tp <player> <gray><i>#TP-Anfrage senden.""";
            player.sendMessage(builder, true);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int executeKick(final CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getExecutor() instanceof Player dummy)) {
            return Command.SINGLE_SUCCESS;
        }
        RaspiPlayer player = Raspi.players().get(dummy);
        Player target = Bukkit.getPlayer(context.getArgument("player", String.class));
        if (target != null) {
            target.kick(MiniMessage.miniMessage().deserialize("Verpiss dich du schlingel"));
            player.sendMessage(String.format("<green>Du hast %s vom server geschubst.", target.getName()), true);
            return Command.SINGLE_SUCCESS;
        } else
            player.sendMessage(String.format("<red>%s ist doch nichtmal Online.", context.getArgument("player", String.class)), true);
        return Command.SINGLE_SUCCESS;
    }


}
