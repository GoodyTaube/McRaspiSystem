package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.database.RaspiPlayer;
import eu.goodyfx.system.core.database.RaspiSuggestions;
import eu.goodyfx.system.core.managers.ExtraInfos;
import eu.goodyfx.system.core.utils.PlayerInfo;
import eu.goodyfx.system.core.utils.RaspiMessages;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

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
                        .then(Commands.literal("addMod").then(Commands.argument("extrainfo", StringArgumentType.string()).executes(PlayerInfoCommandContainer::playerInfoAddModInfo)))

                        .then(Commands.literal("remove").then(Commands.argument("extrainfo", StringArgumentType.string()).executes(PlayerInfoCommandContainer::playerInfoRemoveInfo)))
                ).build();
    }

    private static int playerInfoAddInfo(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            return Command.SINGLE_SUCCESS;
        }

        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        String info = StringArgumentType.getString(context, "extrainfo");
        info = info.replace(" ", "@");
        ExtraInfos extraInfos = new ExtraInfos(player);
        extraInfos.add(Bukkit.getOfflinePlayer(StringArgumentType.getString(context, "player")), info, false);
        raspiPlayer.sendMessage(String.format("<gray>Die %s<aqua><u>info</hover> <gray>wurde gespeichert.", String.format("<hover:show_text:'<green>Extra Info: <gray>%s'>", info.replace("@", " "))), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int playerInfoAddModInfo(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            return Command.SINGLE_SUCCESS;
        }
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);

        String info = StringArgumentType.getString(context, "extrainfo");
        info = info.replace(" ", "@");
        ExtraInfos extraInfos = new ExtraInfos(player);
        extraInfos.add(Bukkit.getOfflinePlayer(StringArgumentType.getString(context, "player")), info, true);
        raspiPlayer.sendMessage(String.format("<gray>Die Mod %s<aqua><u>info</hover> <gray>wurde gespeichert.", String.format("<hover:show_text:'<green>Extra Info: <gray>%s'>", info.replace("@", " "))), true);
        return Command.SINGLE_SUCCESS;
    }


    private static int playerInfoRemoveInfo(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            return Command.SINGLE_SUCCESS;
        }

        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player);
        String extraInfoID = StringArgumentType.getString(context, "extrainfo");
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(StringArgumentType.getString(context, "player"));
        ExtraInfos extraInfos = new ExtraInfos(player);
        extraInfos.remove(offlinePlayer, extraInfoID, false);
        raspiPlayer.sendMessage(String.format("<gray>Du hast die %s<aqua><u>info</hover> <gray>entfernt", String.format("<hover:show_text:'<aqua>ID: <gray>%s'>", extraInfoID)), true);
        return Command.SINGLE_SUCCESS;
    }


    private static int targetPlayerInfo(CommandContext<CommandSourceStack> context) {
        McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
        if (!(context.getSource().getSender() instanceof Player player)) {
            return 1;
        }
        RaspiPlayer raspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(player); //CommandSender

        String targetName = context.getArgument("player", String.class);
        UUID targetUUID = Bukkit.getPlayerUniqueId(targetName);

        if (targetUUID == null) {
            raspiPlayer.sendMessage(String.format(RaspiMessages.PLAYER_NOT_FOUND_NAME, targetName));
            return 0;
        }
        Raspi.playerLifeCycleService().getRaspiAccount(targetUUID, false).thenAcceptAsync(raspiAccount -> {
            if (raspiAccount == null) {
                raspiPlayer.sendMessage(String.format(RaspiMessages.PLAYER_NOT_FOUND_NAME, targetName), true);
                return;
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendRichMessage(new PlayerInfo(Bukkit.getOfflinePlayer(targetUUID), raspiAccount).buildPlayerInfos());
                    ExtraInfos extraInfos = new ExtraInfos(player);
                    extraInfos.getExtraInfos(player);
                }
            }.runTask(plugin);
        });

        return Command.SINGLE_SUCCESS;
    }

}
