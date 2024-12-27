package eu.goodyfx.mcraspisystem.commands.subcommands;


import eu.goodyfx.mcraspisystem.commands.SubCommand;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AdminAuaSubCommand extends SubCommand {
    @Override
    public String getLabel() {
        return "aua";
    }

    @Override
    public String getDescription() {
        return "Fügt kurzzeitig Schaden zu.";
    }

    @Override
    public String getSyntax() {
        return "/admin aua [targetPlayer]";
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if (args.length == 1) {
            player.getPlayer().setFoodLevel(10);
            player.getPlayer().damage(10f);
        } else if (args.length == 2) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                target.setFoodLevel(10);
                target.damage(10f);
            }
        }
        return true;
    }
}
