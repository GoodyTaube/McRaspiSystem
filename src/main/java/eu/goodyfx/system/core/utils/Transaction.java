package eu.goodyfx.system.core.utils;

import eu.goodyfx.system.McRaspiSystem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class Transaction {

    private final UUID sender;
    private final UUID receiver;
    private final long amount;
    private final AtomicBoolean canceled = new AtomicBoolean(false);
    private final int fee_percent;
    private final long cost;

    public Transaction(@NotNull UUID sender, @NotNull UUID receiver, long amount, int fee_percent) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.fee_percent = fee_percent;
        this.cost = Math.max(1, amount * fee_percent / 100);
    }

    public void complete() {
        McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);
        Raspi.players().getRaspiOfflinePlayer(Bukkit.getOfflinePlayer(sender)).thenCompose(sender -> {
            if (sender == null) {
                canceled.set(true);
                return CompletableFuture.completedFuture(null);
            }
            long senderCoins = sender.getRaspiUser().getCoins();
            if (senderCoins < (amount + cost)) {
                canceled.set(true);
                return CompletableFuture.completedFuture(null);
            }
            sender.getRaspiUser().setCoins(senderCoins - (amount + cost));
            return Raspi.players().getRaspiOfflinePlayer(Bukkit.getOfflinePlayer(receiver));
        }).thenAccept(receiver -> {
            if (canceled.get() || receiver == null) {
                return;
            }
            long coins = receiver.getRaspiUser().getCoins();
            receiver.getRaspiUser().setCoins(coins + amount);
        });
    }


}
