package eu.goodyfx.system.core.commandsOLD.subcommands;


import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.SubCommand;
import eu.goodyfx.system.core.utils.ItemBuilder;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import eu.goodyfx.system.core.utils.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

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
