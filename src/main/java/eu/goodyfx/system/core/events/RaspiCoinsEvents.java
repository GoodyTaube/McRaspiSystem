package eu.goodyfx.system.core.events;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.Raspi;
import eu.goodyfx.system.core.utils.RaspiPlayer;
import eu.goodyfx.system.core.utils.Transaction;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RaspiCoinsEvents implements Listener {

    private List<Material> transactionKeys = new ArrayList<>();
    @Getter
    private static List<Transaction> transactions = new ArrayList<>();
    private final McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

    public RaspiCoinsEvents() {
        plugin.setListeners(this);
        addKeys();
    }

    public void addKeys() {
        transactionKeys.add(Material.DIAMOND_ORE);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent breakEvent) {
        Block block = breakEvent.getBlock();
        Player player = breakEvent.getPlayer();
        RaspiPlayer raspiPlayer = Raspi.players().get(player);
        if (transactions.isEmpty()) {
            return;
        }
        int size = transactions.size();
        long provision = 0;

        for (Transaction transaction : transactions) {
            provision = provision + transaction.getCost();
        }
        transactions.clear();
        raspiPlayer.getUser().setCoins(raspiPlayer.getUser().getCoins() + provision);
        raspiPlayer.sendMessage(String.format("<green>Du hast %s Transaktionen beendet und dafür %s RC erhalten.", size, provision));
        raspiPlayer.sendActionBar(String.format("<gray>Neuer Kontostand <white>// <aqua>%s<gray> RC", raspiPlayer.getUser().getCoins()));
    }

}
