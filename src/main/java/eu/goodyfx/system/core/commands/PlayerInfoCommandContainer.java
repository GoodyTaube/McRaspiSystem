package eu.goodyfx.system.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.database.DatabaseTables;
import eu.goodyfx.system.core.database.RaspiSuggestions;
import eu.goodyfx.system.core.managers.ExtraInfos;
import eu.goodyfx.system.core.utils.PlayerInfo;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerInfoCommandContainer {

    public static LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("playerinfo")
                .executes(context -> {
                    if (!(context.getSource().getSender() instanceof Player player)) {
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    }
                    RaspiPlayer raspiPlayer = Raspi.players().get(player);
                    raspiPlayer.sendDebugMessage("Hello World");
                    raspiPlayer.sendDebugMessage("PLEASE USE /playerinfo " + player.getName());
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("player", StringArgumentType.string())
                        .suggests(((context, builder) -> RaspiSuggestions.suggestOfflinePlayer(builder)))
                        .executes(PlayerInfoCommandContainer::targetPlayerInfo)
                        .then(Commands.literal("add").then(Commands.argument("extrainfo", StringArgumentType.string()).executes(PlayerInfoCommandContainer::playerInfoAddInfo)))
                        .then(Commands.literal("remove").then(Commands.argument("extrainfo", StringArgumentType.string()).executes(PlayerInfoCommandContainer::playerInfoRemoveInfo)))
                ).build();
    }

    private static int playerInfoAddInfo(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            return Command.SINGLE_SUCCESS;
        }
        String info = StringArgumentType.getString(context, "extrainfo");
        info = info.replace(" ", "@");
        ExtraInfos extraInfos = new ExtraInfos(player);
        extraInfos.add(Bukkit.getOfflinePlayer(StringArgumentType.getString(context, "player")), info, false);
        Raspi.players().get(player).sendMessage(String.format("<gray>Die %s<aqua><u>info</hover> <gray>wurde gespeichert.", String.format("<hover:show_text:'<green>Extra Info: <gray>%s'>", info.replace("@", " "))), true);
        return Command.SINGLE_SUCCESS;
    }


    private static int playerInfoRemoveInfo(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            return Command.SINGLE_SUCCESS;
        }

        String extraInfoID = StringArgumentType.getString(context, "extrainfo");
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(StringArgumentType.getString(context, "player"));

        ExtraInfos extraInfos = new ExtraInfos(player);
        extraInfos.remove(offlinePlayer, extraInfoID, false);
        Raspi.players().get(player).sendMessage(String.format("<gray>Du hast die %s<aqua><u>info</hover> <gray>entfernt", String.format("<hover:show_text:'<aqua>ID: <gray>%s'>", extraInfoID)), true);
        return Command.SINGLE_SUCCESS;
    }


    private static int targetPlayerInfo(CommandContext<CommandSourceStack> context) {
        McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
        if (!(context.getSource().getSender() instanceof Player player)) {
            return Command.SINGLE_SUCCESS;
        }
        RaspiPlayer raspiPlayer = Raspi.players().get(player);
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(StringArgumentType.getString(context, "player"));
        if (!plugin.getDatabaseManager().userExistInTable(offlinePlayer.getUniqueId(), DatabaseTables.USER_DATA)) {
            raspiPlayer.sendDebugMessage("<red>Häää, der Spieler hat hier noch keine Zeit verschwendet.");
            return Command.SINGLE_SUCCESS;
        }
        Raspi.players().getRaspiOfflinePlayer(offlinePlayer).thenAcceptAsync(raspiOfflinePlayer -> {
            if (raspiOfflinePlayer == null) {
                raspiPlayer.sendMessage("<red>❌ Spieler nicht gefunden", true);
                return;
            }
            player.showDialog(new PlayerInfo(raspiOfflinePlayer).buildPlayerInfosDialog(offlinePlayer, player));
        }, runnable -> Bukkit.getScheduler().runTask(plugin, runnable));
        return Command.SINGLE_SUCCESS;
    }

}
