package eu.goodyfx.mcraspisystem.commands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.managers.RequestManager;
import eu.goodyfx.mcraspisystem.managers.UserManager;
import eu.goodyfx.mcraspisystem.utils.RaspiMessages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class RequestCommand implements CommandExecutor, TabCompleter {

    private final McRaspiSystem plugin;
    private final UserManager userManager;
    private final RequestManager requestManager;
    private final RaspiMessages data;

    public RequestCommand(McRaspiSystem plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getModule().getUserManager();
        this.requestManager = plugin.getModule().getRequestManager();
        this.data = plugin.getModule().getRaspiMessages();
        plugin.setCommand("request", this, this);
    }

    private final Map<UUID, UUID> userDenyProcess = new HashMap<>();
    protected static final Map<UUID, Integer> userKickAmount = new HashMap<>();
    private static final List<UUID> toBan = new ArrayList<>();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("request")) {
            List<String> result = new ArrayList<>();
            if (args.length == 1) {
                result.add("deny");
                result.add("accept");
                result.add("kick");
                return result;
            } else if (args.length == 3) {
                for (String reason : requestManager.getReasons()) {
                    result.add(reason.replace("@", " "));
                }
                if (!result.isEmpty()) {
                    Collections.sort(result);
                } else result.add("Noch nichts Vorgeschlagen.");
                return result;
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player) {
            if (args.length == 1) {
                player.sendRichMessage(data.getPrefix() + "<red>Bitte gib einen Grund an.");
                return false;

            }
            if (args.length >= 2) {
                try {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                    String groupLabel = plugin.getConfig().getString("Utilities.playerGroup");
                    if (!userManager.userExist(target)) {
                        player.sendRichMessage(data.getPrefix() + "<red>Der Spieler Existiert nicht!");
                        return true;
                    }

                    if (args[0].equalsIgnoreCase("deny")) {
                        //Spieler Ablehnung
                        if (args.length == 2) {
                            player.sendRichMessage(data.getPrefix() + "<red>Bitte verwende einen Grund! <white>[<click:run_command:'/request deny " + target.getName() + " -request_start'><green>Gründe<reset>]");
                            return true;
                        }

                        if (args.length > 2) {
                            //Gründe für "/request Deny"
                            if (args[2].equalsIgnoreCase("-request_start")) {
                                userDenyProcess.put(player.getUniqueId(), target.getUniqueId());
                            }
                        }
                        if (userDenyProcess.containsKey(player.getUniqueId())) {
                            player.sendRichMessage(data.getPrefix() + "<gray>Grund: <white>[<green>" + denyReason(target, "Spieler ist zu jung") + "Alter<reset><white>] <white>[<red>" + denyReason(target, "Spieler hat Beleidigt") + "Beleidigung<reset><white>] <white>[<gold>" + denyReason(target, "Spieler ist Hacker") + "Hacker<reset><white>] <white>[<click:suggest_command:'/request deny " + target.getName() + " '><green><hover:show_text:'<rainbow>Eigener Grund'>+<reset><white>]");
                            userDenyProcess.remove(player.getUniqueId());
                            return true;
                        }


                        if (groupExist("default")) {
                            player.sendRichMessage(data.getPrefix() + "<dark_red>ERROR <gray>// <red>Die Gruppe <yellow>" + groupLabel + " <red>Existiert nicht!");
                            return true;
                        }

                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + target.getName() + " permission unset group." + groupLabel.toLowerCase(Locale.ROOT));
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + target.getName() + " permission set group.default");


                        player.sendRichMessage(data.getPrefix() + "<gray>Du hast <yellow><italic>" + target.getName() + " <red>Abgelehnt!");
                        Bukkit.getOnlinePlayers().forEach(all -> {
                            if (all.isPermissionSet("system.moderator")) {
                                if (all.getName().equalsIgnoreCase(player.getName())) {
                                    return;
                                }


                                all.sendRichMessage(data.getPrefix() + "<aqua><italic>" + target.getName() + " <gray><italic>wurde von " + player.getName() + " <red>Abgelehnt!");
                            }
                        });


                        if (args.length >= 2) {
                            StringBuilder builder = new StringBuilder();
                            for (int i = 2; i < args.length; i++) {
                                builder.append(args[i]).append("@");
                            }
                            builder.setLength(builder.length() - 1);
                            requestManager.set(target, builder.toString(), player);

                            requestManager.reload();
                            requestManager.addReason(builder.toString());
                        }

                        return true;
                    } else if (args[0].equalsIgnoreCase("accept")) {
                        //Spieler Annahme
                        if (groupExist(groupLabel)) {
                            player.sendRichMessage(data.getPrefix() + "<dark_red>ERROR <gray>// <red>Die Gruppe <yellow>" + groupLabel + " <red>Existiert nicht!");
                            return true;
                        }

                        if (!target.isOnline()) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + target.getName() + " permission set group." + groupLabel.toLowerCase(Locale.ROOT));
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + target.getName() + " permission unset group.default");
                        } else {
                            LuckPerms luckPerms = plugin.getHookManager().getLuckPerms();
                            luckPerms.getUserManager().modifyUser(target.getUniqueId(), user1 -> {
                                user1.data().add(Node.builder("group.spieler").build());
                                user1.setPrimaryGroup("group.spieler");
                            });

                            luckPerms.getUserManager().modifyUser(target.getUniqueId(), user1 -> user1.data().remove(Node.builder("group.default").build()));

                            Bukkit.getLogger().info("LuckPerms value Changed for " + target.getName());

                        }

                        if (requestManager.isBlocked(target)) {
                            requestManager.remove(target);
                        }
                        requestManager.allow(target);
                        player.sendRichMessage(data.getPrefix() + "<gray>Du hast <yellow><italic>" + target.getName() + " <green>Erlaubt!");
                        Bukkit.getOnlinePlayers().forEach(all -> {

                            if (all.isPermissionSet("system.moderator")) {
                                if (all.getName().equalsIgnoreCase(player.getName())) {
                                    return;
                                }
                                all.sendRichMessage(data.getPrefix() + "<aqua><italic>" + target.getName() + " <gray><italic>wurde von " + player.getName() + " <green>Erlaubt!");
                            }
                        });

                        return true;
                    }
                    if (args[0].equalsIgnoreCase("kick")) {
                        //Player Moderator Kick
                        Player targetPlayer = Bukkit.getPlayer(args[1]);
                        if (targetPlayer != null) {
                            int amount = 0;
                            if (userKickAmount.containsKey(targetPlayer.getUniqueId())) {
                                amount = userKickAmount.get(targetPlayer.getUniqueId()) + 1;
                            } else {
                                userKickAmount.put(targetPlayer.getUniqueId(), 1);
                                amount = amount + 1;
                            }

                            userKickAmount.put(targetPlayer.getUniqueId(), amount);
                            if (amount == plugin.getConfig().getInt("Utilities.tempban.kick_times")) {
                                toBan.add(targetPlayer.getUniqueId());
                                return true;
                            }

                            if (targetPlayer.isPermissionSet("group.default")) {
                                targetPlayer.kick(Component.text("MCRaspi - Disconnect").color(NamedTextColor.GOLD).append(Component.newline()).append(Component.newline()).append(Component.text(data.getKickMessage())));
                            } else {
                                player.sendRichMessage(data.getPrefix() + "<red>Der Spieler ist nicht als Abgelehnt Deklariert.");
                            }
                        } else player.sendRichMessage(data.playerNotOnline(args[1]));
                        return true;
                    }

                } catch (IllegalArgumentException | NullPointerException e) {
                    player.sendRichMessage(data.getPrefix() + "<red>Der Spieler Existiert nicht.");
                    return true;
                }
            }
        }
        return false;
    }

    private String denyReason(OfflinePlayer target, String reason) {
        return "<click:run_command:'/request deny " + target.getName() + " " + reason + "'>";
    }

    private boolean groupExist(String groupName) {
        Group group = plugin.getHookManager().getLuckPerms().getGroupManager().getGroup(groupName);
        return group == null;
    }

    public static boolean freeToBan(UUID uuid) {
        return toBan.contains(uuid);
    }


}
