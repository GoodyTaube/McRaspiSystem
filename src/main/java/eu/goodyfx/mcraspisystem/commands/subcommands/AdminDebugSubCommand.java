package eu.goodyfx.mcraspisystem.commands.subcommands;


import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.SubCommand;
import eu.goodyfx.mcraspisystem.utils.ItemBuilder;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import eu.goodyfx.mcraspisystem.utils.Settings;
import io.papermc.paper.datapack.Datapack;
import io.papermc.paper.datapack.DatapackManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class AdminDebugSubCommand extends SubCommand {


    private final McRaspiSystem plugin;

    public AdminDebugSubCommand(McRaspiSystem utilities) {
        this.plugin = utilities;
        register("settings", this::settings);
    }

    @Override
    public String getLabel() {
        return "debug";
    }

    @Override
    public String getDescription() {
        return "Ein Debug Command für Mistergoody zum Testen.";
    }

    @Override
    public String getSyntax() {
        return "/admin debug <DEBUG_FUNCTION>";
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        invoke("settings", player, args);
        invoke("test", player, args);
        return true;
    }

    public void settings(RaspiPlayer player, String[] args) {
        if (args.length == 3 && args[1].equalsIgnoreCase("settings")) {
            Player target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                player.sendMessage(plugin.getModule().getRaspiMessages().playerNotOnline(args[2]));
                return;
            }
            player.sendMessage(target.getName() + " Settings:");
            for (Settings setting : Settings.values()) {
                player.sendMessage(setting.getLabel() + ": " + plugin.getModule().getPlayerSettingsManager().contains(setting, target));
            }
        }
    }

    public void test(RaspiPlayer player, String[] args){
        if(args.length == 2 && args[1].equalsIgnoreCase("test")){
            player.getPlayer().getInventory().addItem(new ItemBuilder(Material.CRAFTING_TABLE).displayName("TEST").build());
        }
    }

}
