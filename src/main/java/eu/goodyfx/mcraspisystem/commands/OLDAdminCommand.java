package eu.goodyfx.mcraspisystem.commands;

import eu.goodyfx.goodysutilities.GoodysUtilities;
import eu.goodyfx.goodysutilities.managers.PlayerSettingsManager;
import eu.goodyfx.goodysutilities.managers.UserManager;
import eu.goodyfx.goodysutilities.utils.ItemBuilder;
import eu.goodyfx.goodysutilities.utils.OldColors;
import eu.goodyfx.goodysutilities.utils.PlayerNameController;
import eu.goodyfx.goodysutilities.utils.Settings;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings({"unused", "deprecated"})
public class OLDAdminCommand implements CommandExecutor, TabCompleter {

    private final PlayerSettingsManager playerSettingsManager;
    private final PlayerNameController playerNameController;
    private final GoodysUtilities plugin;
    private final UserManager userManager;

    public OLDAdminCommand(GoodysUtilities plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
        this.playerNameController = plugin.getPlayerNameController();
        this.playerSettingsManager = plugin.getPlayerSettingsManager();
        plugin.setCommand("admin", this, this);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("admin")) {
            List<String> entry = new ArrayList<>();
            if (args.length == 1) {
                entry.add("new-stop");
                entry.add("reload");
                entry.add("debugg-user");
                entry.add("prefix");
            }

            if (args.length == 2) {
                if (args[0].equals("prefix")) {
                    return userManager.getAllUsers();
                }
            }

            Collections.sort(entry);
            return entry;

        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {


            if (args.length == 0) {
                player.sendPlainMessage(" ");
                player.sendPlainMessage("§7Admin-Commands");
                player.sendPlainMessage("§e/admin new-stop §f:: §cDe§7/§aAktiviere §7den beitritt von Neuen Spielern.");
                player.sendPlainMessage("§e/admin debugg-user §f:: §7Holt alle werte aus der §aUserDatenbank");
                player.sendPlainMessage(" ");
            }

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("new-stop")) {
                    if (plugin.getConfig().getBoolean("Utilities.kickNewbies")) {
                        plugin.getConfig().set("Utilities.kickNewbies", false);
                        player.sendPlainMessage("§aNewbies werden nun Automatisch erlaubt!");
                    } else if (!plugin.getConfig().getBoolean("Utilities.kickNewbies")) {
                        plugin.getConfig().set("Utilities.kickNewbies", true);
                        player.sendPlainMessage("§aNewbies werden nun Automatisch geblockt");
                    }
                    plugin.saveConfig();
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    plugin.reloadConfig();
                    plugin.getUserManager().reloadFile();
                    player.sendPlainMessage("§aConfig.yml, UserDB.yml wurden neu Geladen.");
                    return true;
                }


            }


            if (args[0].equalsIgnoreCase("ct")) {
                if (args.length == 2) {


                    Player player1 = Bukkit.getPlayer(args[1]);
                    if (player1 == null) {
                        player.sendRichMessage(plugin.getData().playerNotOnline(args[1]));
                        return true;
                    }

                    Objective objective1 = null;
                    for (Objective objective2 : player1.getScoreboard().getObjectives()) {
                        if (objective2.getName().startsWith("On")) {
                            objective1 = objective2;
                            break;
                        }
                    }

                    if (objective1 != null) {
                        int hours = objective1.getScore(Bukkit.getOfflinePlayer(player1.getUniqueId())).getScore();

                        hours = hours * 20 * 60 * 60;

                        long old = player1.getStatistic(Statistic.PLAY_ONE_MINUTE);
                        player.sendRichMessage("OLD: " + old + " SCORE: " + hours);

                        if (hours != 0 && hours != old) {
                            player1.setStatistic(Statistic.PLAY_ONE_MINUTE, hours);
                            player.sendRichMessage(plugin.getData().getPrefix() + "Repaired!<br>Old: " + (old / 20 / 60 / 60) + "h to " + (hours / 20 / 60 / 60) + "h");
                        } else {
                            player.sendRichMessage(plugin.getData().getPrefix() + "Nothing Changed!" + "<br>Old: " + (old / 20 / 60 / 60) + "h to " + (hours / 20 / 60 / 60) + "h");
                        }
                        Bukkit.getLogger().info("Stats repair for " + player1.getName());
                    }

                }
                return true;

            }

            if (args[0].equalsIgnoreCase("get")) {
                if (args.length == 1) {
                    Inventory inventory = Bukkit.createInventory(null, 9, MiniMessage.miniMessage().deserialize("Bernds Spezial Items"));

                    ItemStack debug_Stick = new ItemBuilder(Material.STICK).setModelID(777).addLore(MiniMessage.miniMessage().deserialize("<green>Beim Rechts Klick bekommt der Spieler<br>Einen Demo Screen Angezeigt")).displayName(MiniMessage.miniMessage().deserialize("<red>Debug Stick")).build();
                    inventory.addItem(debug_Stick);
                    player.openInventory(inventory);
                }
            }

            if (args[0].equalsIgnoreCase("disable")) {
                if (args.length == 2) {
                    Plugin select = Bukkit.getPluginManager().getPlugin(args[1]);
                    if (select == null) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize("Plugin not Found (" + args[0] + ")"));
                        return true;
                    }
                    Bukkit.getPluginManager().disablePlugin(select);
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Das Plugin : " + select.getName() + " wurde Deaktiviert."));
                }
            }

            if (args[0].equalsIgnoreCase("spawn")) {
                if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("Lazaros")) {

                        Villager villager = (Villager) Objects.requireNonNull(Bukkit.getWorld(player.getWorld().getName())).spawnEntity(player.getLocation(), EntityType.VILLAGER);

                        villager.setAI(false);
                        villager.setProfession(Villager.Profession.BUTCHER);

                        villager.customName(MiniMessage.miniMessage().deserialize("<green>Lazaros"));
                        villager.setCustomNameVisible(true);
                    }
                }
            }

            if (args[0].equals("prefix")) {
                if (args.length > 2) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                    if (userManager.userExist(target)) {
                        StringBuilder prefix = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            prefix.append(args[i]).append("@");
                        }

                        prefix.setLength(prefix.length() - 1);

                        userManager.set(target, "prefix", prefix.toString());
                        plugin.getPrefixManager().updateUser(target);
                        player.sendRichMessage("<green>Der Prefix von <gray>" + target.getName() + " <green>ist nun: <reset>" + prefix.toString().replace("@", " "));

                        plugin.getPrefixManager().updateUser(target);
                    } else player.sendPlainMessage("§cDer Spieler Existiert nicht bei uns!");
                    return true;
                }
            }

        } else {
            if (args.length == 2) {
                if (sender instanceof BlockCommandSender commandSender) {
                    CommandBlock block = (CommandBlock) commandSender.getBlock().getState();
                    Location location = commandSender.getBlock().getLocation();
                    Collection<Entity> entities = location.getNearbyEntities(3, 4, 3);
                    String commandString = block.getCommand();


                    for (Entity entity : entities) {
                        if (entity instanceof Player player) {
                            if (commandString.contains("@p")) {
                                block.setSuccessCount(block.getSuccessCount() + 1);
                                block.update();
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandString.replaceAll("@p", player.getName()));
                                break;
                            }
                        }
                    }


                    return true;
                }


                Player player = Bukkit.getPlayer(args[0]);
                String location = args[1];

                if (player == null) {
                    Bukkit.getLogger().severe("[" + args[0] + "] is not a valid Player! See Command Usage for more Information.");
                    return true;
                }

                Bukkit.getOnlinePlayers().forEach(all -> {
                    if (!playerSettingsManager.contains(Settings.MESSAGES, all)) {
                        all.sendRichMessage(OldColors.convert(plugin.getData().getPrefix().replace("§", "&")) +
                                playerNameController.getName(Objects.requireNonNull(player)) + " hat Teleport " + location + " benutzt");
                    }
                });
                Bukkit.getLogger().info("Raspi-Teleport: " + player.getName() + " hat Teleport " + location + " benutzt.");

                return true;

            }
        }
        return false;
    }

}
