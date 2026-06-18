package eu.goodyfx.mcraspi.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.api.CommandUtils;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.database.RaspiManagement;
import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.core.utils.RaspiMessages;
import eu.goodyfx.mcraspi.core.utils.RaspiPermission;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NonNull;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class TempBanCommandContainer {

    // Command Format  /tempban <user> <int> <s:m:h:d:w:M:y> <[grund...]>

    private final McRaspiSystem plugin;

    public TempBanCommandContainer(McRaspiSystem plugin) {
        this.plugin = plugin;
    }


    private static final String SUCCESS_BAN_MESSAGE = "<gray>Du hast <red>%s <gray>für: <red>'%s' bis zum: '%s' <green>gesperrt.";
    private static final String SUCCESS_BAN_MESSAGE_DISCORD = "> %s hat %s für: '%s' bis zum: '%s' vom server gesperrt.";
    private static final String BAN_MESSAGE_KICK = "<gold>mcraspi.com <gray>- <red><b>Moderation<reset><br><br><red>Du wurdest von mcraspi.com ausgeschlossen." + "<br><br><gray>Grund:<red> '%s'<br><gray>Deine Entsperrung: <red>'%s'<br><br> ";

    public LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("tempban").requires(CommandUtils.PLAYER_ONLY)
                .then(Commands.argument("spieler", StringArgumentType.string())
                        .then(Commands.argument("dauer", IntegerArgumentType.integer(1, 9999))
                                .then(Commands.argument("zeiteinheit", StringArgumentType.word()).suggests((context, builder) -> {
                                    builder.suggest("s");
                                    builder.suggest("m");
                                    builder.suggest("h");
                                    builder.suggest("d");
                                    builder.suggest("w");
                                    builder.suggest("M");
                                    builder.suggest("y");
                                    builder.suggest("y");
                                    return builder.buildFuture();
                                }).then(Commands.argument("grund", StringArgumentType.greedyString()).executes(this::tempBanPlayer))
                                .then(Commands.literal("--MOD")
                                        .then(Commands.argument("grund", StringArgumentType.greedyString()).executes(this::tempBanPlayer))))
                )
        ).build();
    }

    private Integer tempBanPlayer(@NonNull CommandContext<CommandSourceStack> context) {
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer((Player) context.getSource().getSender());
        String user = context.getArgument("spieler", String.class);
        int duration = context.getArgument("dauer", Integer.class);
        String unit = context.getArgument("zeiteinheit", String.class);
        String reason = context.getArgument("grund", String.class);
        //User Exists?
        UUID targetUUID = Bukkit.getPlayerUniqueId(user);
        if (targetUUID == null) {
            raspiPlayer.sendMessage(String.format(RaspiMessages.PLAYER_NOT_FOUND_NAME, user), true);
            return 1;
        }

        //TimeUnit bestimmen
        Instant now = Instant.now();
        Instant expire = switch (unit) {
            case "s" -> now.plus(duration, ChronoUnit.SECONDS);
            case "m" -> now.plus(duration, ChronoUnit.MINUTES);
            case "h" -> now.plus(duration, ChronoUnit.HOURS);
            case "d" -> now.plus(duration, ChronoUnit.DAYS);
            case "w" -> now.plus(duration * 7L, ChronoUnit.DAYS);
            case "M" -> now.plus(duration * 30L, ChronoUnit.DAYS);
            case "y" -> now.plus(duration * 365L, ChronoUnit.DAYS);
            default -> null;
        };

        if (expire == null) {
            raspiPlayer.sendMessage("<red>Bitte nutze ein gültiges Zeitformat.", true);
            return 1;
        }

        long banExpire = expire.toEpochMilli();

        //=============MOD USING FLAG ==================
        boolean isModAction = false;

        try {
            String modInteraction = context.getArgument("mod_interaction", String.class);
            if (modInteraction.equals("--MOD")) {
                isModAction = true;
            }
        } catch (IllegalArgumentException ignored) {

        }

        if (!isModAction && !raspiPlayer.hasPermission(RaspiPermission.ADMIN)) {
            long maxAllowedForMods = Instant.now().plus(3, ChronoUnit.HOURS).toEpochMilli();
            if (banExpire > maxAllowedForMods) {
                raspiPlayer.sendMessage("<red>Tut mir leid, Du darfst die Spieler nur 3h Sperren.", true);
                return 1;
            }
        }

        //Ban Perform
        Raspi.accountService().getRaspiAccount(targetUUID, false).thenAccept(targetAccount -> {
            //User Played?
            if (targetAccount == null) {
                raspiPlayer.sendMessage(RaspiMessages.PLAYER_NOT_FOUND, true);
                return;
            }
            //Okay, lets ban
            RaspiManagement management = targetAccount.getRaspiManagement();
            management.performTempBan(raspiPlayer.getPlayer(), reason, banExpire);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy | HH:mm");
            String banExpireFormatted = dateFormat.format(banExpire);
            new BukkitRunnable() {
                @Override
                public void run() {
                    Player targetPlayer = Bukkit.getPlayer(targetUUID);
                    if (targetPlayer != null) {
                        targetPlayer.kick(MiniMessage.miniMessage().deserialize(String.format(BAN_MESSAGE_KICK, reason, banExpireFormatted)));
                    }
                    raspiPlayer.sendMessage(String.format(SUCCESS_BAN_MESSAGE, targetAccount.getRaspiUser().getUsername(), reason, banExpireFormatted), true);
                    plugin.getHookManager().getDiscordIntegration().send(String.format(SUCCESS_BAN_MESSAGE_DISCORD, raspiPlayer.getPlayer().getName(), targetAccount.getRaspiUser().getUsername(), reason, banExpireFormatted));
                }
            }.runTask(plugin);

        });


        return Command.SINGLE_SUCCESS;
    }


}
