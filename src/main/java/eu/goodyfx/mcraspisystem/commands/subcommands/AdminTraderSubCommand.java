package eu.goodyfx.mcraspisystem.commands.subcommands;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.commands.SubCommand;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class AdminTraderSubCommand extends SubCommand {

    private final McRaspiSystem plugin;

    public AdminTraderSubCommand(McRaspiSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getLabel() {
        return "trader";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return "/admin trader";
    }

    @Override
    public int length() {
        return 0;
    }
    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if (args.length == 1) {
            player.sendMessage(getSyntax());
            return true;
        }
        villager(args, player.getPlayer().getLocation());
        return false;
    }

    /**
     * Spawns Villager with JOB to interact.
     */
    private void villager(String[] args, Location location) {
        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.setAI(false);
        villager.setCustomNameVisible(true);
        villager.customName(MiniMessage.miniMessage().deserialize("<green>Johan"));
        PersistentDataContainer container = villager.getPersistentDataContainer();
        container.set(new NamespacedKey(plugin, "special"), PersistentDataType.INTEGER, 1);

    }
}
