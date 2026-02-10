package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.database.RaspiSuggestions;
import eu.goodyfx.system.core.managers.ExtraInfos;
import eu.goodyfx.system.core.utils.PlayerInfo;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiPermission;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class PlayerInfoCommandContainer {

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("playerinfo")
                .executes(context -> {
                    if (!(context.getSource().getSender() instanceof Player player)) {
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    }
                    Bukkit.dispatchCommand(player, "playerinfo " + player.getName());
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("player", StringArgumentType.string())
                        .suggests(((context, builder) -> RaspiSuggestions.suggestOfflinePlayer(builder)))
                        .executes(PlayerInfoCommandContainer::targetPlayerInfo)
                        .then(Commands.literal("add").then(Commands.argument("extrainfo", StringArgumentType.string()).executes(PlayerInfoCommandContainer::playerInfoAddInfo)))
                        .then(Commands.literal("addMod").then(Commands.argument("extrainfo", StringArgumentType.string()).requires(sender -> Objects.requireNonNull(sender.getExecutor()).hasPermission(RaspiPermission.TEAM.getPermissionValue())).executes(PlayerInfoCommandContainer::playerInfoAddModInfo)))

                        .then(Commands.literal("remove").then(Commands.argument("extrainfo", StringArgumentType.string()).executes(PlayerInfoCommandContainer::playerInfoRemoveInfo)))
                ).build();
    }

    private static int playerInfoAddInfo(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            return Command.SINGLE_SUCCESS;
        }

        Raspi.players().withOnlinePlayer(player, raspiPlayer -> {
            String info = StringArgumentType.getString(context, "extrainfo");
            info = info.replace(" ", "@");
            ExtraInfos extraInfos = new ExtraInfos(player);
            extraInfos.add(Bukkit.getOfflinePlayer(StringArgumentType.getString(context, "player")), info, false);
            raspiPlayer.sendMessage(String.format("<gray>Die %s<aqua><u>info</hover> <gray>wurde gespeichert.", String.format("<hover:show_text:'<green>Extra Info: <gray>%s'>", info.replace("@", " "))), true);

        });

        return Command.SINGLE_SUCCESS;
    }

    private static int playerInfoAddModInfo(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            return Command.SINGLE_SUCCESS;
        }
        Raspi.players().withOnlinePlayer(player, raspiOffPlayer -> {
            String info = StringArgumentType.getString(context, "extrainfo");
            info = info.replace(" ", "@");
            ExtraInfos extraInfos = new ExtraInfos(player);
            extraInfos.add(Bukkit.getOfflinePlayer(StringArgumentType.getString(context, "player")), info, true);
            raspiOffPlayer.sendMessage(String.format("<gray>Die Mod %s<aqua><u>info</hover> <gray>wurde gespeichert.", String.format("<hover:show_text:'<green>Extra Info: <gray>%s'>", info.replace("@", " "))), true);
        });
        return Command.SINGLE_SUCCESS;
    }


    private static int playerInfoRemoveInfo(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            return Command.SINGLE_SUCCESS;
        }
        Raspi.players().withOnlinePlayer(player, raspiPlayer -> {
            String extraInfoID = StringArgumentType.getString(context, "extrainfo");
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(StringArgumentType.getString(context, "player"));

            ExtraInfos extraInfos = new ExtraInfos(player);
            extraInfos.remove(offlinePlayer, extraInfoID, false);
            raspiPlayer.sendMessage(String.format("<gray>Du hast die %s<aqua><u>info</hover> <gray>entfernt", String.format("<hover:show_text:'<aqua>ID: <gray>%s'>", extraInfoID)), true);
        });
        return Command.SINGLE_SUCCESS;
    }


    private static int targetPlayerInfo(CommandContext<CommandSourceStack> context) {
        McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
        if (!(context.getSource().getSender() instanceof Player player)) {
            return Command.SINGLE_SUCCESS;
        }
        Raspi.players().withOnlinePlayer(player, raspiPlayer -> {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(StringArgumentType.getString(context, "player"));

            if (!offlinePlayer.hasPlayedBefore()) {
                raspiPlayer.sendMessage("<red>❌ Spieler nicht gefunden", true);
                return;
            }


            player.showDialog(new PlayerInfo(offlinePlayer).buildPlayerInfosDialog(offlinePlayer, player));
        });
        return Command.SINGLE_SUCCESS;
    }

}
