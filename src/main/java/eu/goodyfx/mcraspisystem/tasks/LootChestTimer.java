package eu.goodyfx.mcraspisystem.tasks;

import eu.goodyfx.mcraspisystem.McRaspiSystem;
import eu.goodyfx.mcraspisystem.utils.LootChest;
import eu.goodyfx.mcraspisystem.utils.RaspiTimes;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class LootChestTimer extends BukkitRunnable {

    private static final int TIMER_MAX_MINUTES = 5; // Maximale Zeit in Minuten
    private static final long TICKS_PER_SECOND = 20L;
    private static final long SECONDS_PER_MINUTE = 60L;

    private final McRaspiSystem plugin;
    private final Random random = new Random();

    private List<LootChest> lootChestDisplay = new ArrayList<>();

    private long remainingTicks = 0;
    private boolean isInitialized = false;
    private boolean lootChestReady = false;

    public LootChestTimer(McRaspiSystem plugin) {
        this.plugin = plugin;
        this.runTaskTimerAsynchronously(plugin, 0, TICKS_PER_SECOND); // Läuft alle 20 Ticks (1 Sekunde)
    }

    @Override
    public void run() {
        if (lootChestReady) {
            return; // Timer beendet, keine weitere Aktion nötig
        }

        if (!isInitialized) {
            initializeTimer();
            return;
        }

        if (remainingTicks <= 0) {
            triggerLootChestReady();
        } else {
            remainingTicks--;
        }

        textUpdate();

    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        lootChestDisplay.forEach(LootChest::killAll);
        super.cancel();
    }

    private void textUpdate() {
        for (LootChest lootChest : lootChestDisplay) {

            lootChest.getTimeDisplay().text(MiniMessage.miniMessage().deserialize("<green>" + remainingTicks + " Sekunde(n)"));
        }
    }

    private void initializeTimer() {
        plugin.getLogger().info("Initialisiere LootChest-Timer.");
        resetTimer();
        isInitialized = true;
    }

    public void resetTimer() {
        remainingTicks = calculateRandomTicks();
        logNextChestTime(remainingTicks);
        notifyPlayers(remainingTicks);
        lootChestReady = false;
    }

    private void triggerLootChestReady() {
        plugin.getLogger().info("LootChest ist bereit zum Öffnen.");
        for (LootChest lootChest : lootChestDisplay) {
            lootChest.getTimeDisplay().text(MiniMessage.miniMessage().deserialize("<green>Öffne Mich!"));
        }
        lootChestReady = true;
    }

    private long calculateRandomTicks() {
        return SECONDS_PER_MINUTE * random.nextInt(TIMER_MAX_MINUTES);
    }

    private void logNextChestTime(long ticks) {
        String timeMessage = String.format("DEBUGGER: Nächste LootChest in %s.",
                RaspiTimes.Ticks.getTimeUnit(ticks * 20));
        plugin.getLogger().info(timeMessage);
    }

    private void notifyPlayers(long ticks) {
        String timeMessage = RaspiTimes.Ticks.getTimeUnit(ticks * 20);
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendRichMessage("<green>LootChest aktiviert!");
            player.sendRichMessage("<gray>Nächste LootChest in " + timeMessage + ".");
        });
    }

    public boolean isLootChestReady() {
        return this.lootChestReady;
    }

    public List<LootChest> getLootChestDisplay() {
        return this.lootChestDisplay;
    }

}