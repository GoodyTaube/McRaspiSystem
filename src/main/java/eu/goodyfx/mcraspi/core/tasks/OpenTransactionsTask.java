package eu.goodyfx.mcraspi.core.tasks;

import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.utils.Transaction;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class OpenTransactionsTask extends BukkitRunnable {

    public OpenTransactionsTask(McRaspiSystem plugin) {
        this.runTaskTimerAsynchronously(plugin, 0, 20 * 60L);
    }


    @Getter
    private static final List<Transaction> open_transactions = new ArrayList<>();

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!open_transactions.isEmpty()) {
                player.sendPlayerListFooter(MiniMessage.miniMessage().deserialize(String.format("<gray>Offene Transaktionen: <aqua>%s", open_transactions.size())));
            } else {
                player.sendPlayerListFooter(Component.empty());
            }
        });

    }
}
