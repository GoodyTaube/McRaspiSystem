package eu.goodyfx.system.core.utils;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.database.RaspiPlayer;
import eu.goodyfx.system.core.tasks.OpenTransactionsTask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
        OpenTransactionsTask.getOpen_transactions().add(this);
    }

    public void complete() {
        McRaspiSystem plugin = JavaPlugin.getPlugin(McRaspiSystem.class);

        OfflinePlayer senderOff = Bukkit.getOfflinePlayer(sender);
        OfflinePlayer receiverOff = Bukkit.getOfflinePlayer(receiver);
        if (senderOff.isOnline()) {
            RaspiPlayer senderRaspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(senderOff.getPlayer());
            final long[] senderCoins = {senderRaspiPlayer.userData().getCoins()};

            if (senderCoins[0] < (amount + cost)) {
                Raspi.debugger().debug(senderRaspiPlayer.getColorName() + " hat nicht genug coins um seine Transaktion zu beenden.");
                senderRaspiPlayer.sendMessage("<red>Du hast nicht genug Coins für deine Transaktion an " + receiverOff.getName(), true);
                canceled.set(true);
                return;
            }

            if (!receiverOff.isOnline()) {
                Raspi.playerLifeCycleService().getRaspiOffPlayer(receiverOff).thenAccept(account -> {
                    if (!receiverOff.hasPlayedBefore()) {
                        canceled.set(true);
                        return;
                    }
                    long receiverCoins = account.getRaspiUser().getCoins();
                    senderCoins[0] = senderCoins[0] - cost - amount;
                    receiverCoins = receiverCoins + amount;
                    account.getRaspiUser().setCoins(receiverCoins);
                    senderRaspiPlayer.userData().setCoins(senderCoins[0]);
                    senderRaspiPlayer.sendMessage(String.format("<green>Deine Transaktion an <aqua>%s <green>in höhe von <aqua>%s ist nun beendet.", account.getRaspiUser().getUsername(), amount));
                });
                return;
            }
            RaspiPlayer receiverRaspiPlayer = Raspi.playerLifeCycleService().getRaspiPlayer(receiverOff.getPlayer());
            long receiverCoins = receiverRaspiPlayer.userData().getCoins();
            senderCoins[0] = senderCoins[0] - cost - amount;
            receiverCoins = receiverCoins + amount;
            receiverRaspiPlayer.userData().setCoins(receiverCoins);
            senderRaspiPlayer.userData().setCoins(senderCoins[0]);
            receiverRaspiPlayer.sendMessage(String.format("<green>Du hast <aqua>%s RC <green>von <aqua>%s <green>erhalten.", amount, senderRaspiPlayer.getDisplayName()), true);
            senderRaspiPlayer.sendMessage(String.format("<green>Deine Transaktion an <aqua>%s <green>in höhe von <aqua>%s ist nun beendet.", receiverRaspiPlayer.getDisplayName(), amount));
            return;
        }


        Raspi.playerLifeCycleService().getRaspiOffPlayer(senderOff).thenCompose(sender -> {
            long senderCoins = sender.getRaspiUser().getCoins();
            if (senderCoins < (amount + cost)) {
                canceled.set(true);
                return CompletableFuture.completedFuture(null);
            }
            sender.getRaspiUser().setCoins(senderCoins - (amount + cost));
            return Raspi.playerLifeCycleService().getRaspiOffPlayer(receiverOff);
        }).thenAccept(receiver -> {
            if (canceled.get() || receiver == null) {
                return;
            }
            long coins = receiver.getRaspiUser().getCoins();
            receiver.getRaspiUser().setCoins(coins + amount);
        });

    }


}
