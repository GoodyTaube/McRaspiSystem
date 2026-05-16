package eu.goodyfx.system.randomlootchest;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.utils.RaspiSubSystem;
import eu.goodyfx.system.randomlootchest.commands.RandomLootChestCommandContainer;
import eu.goodyfx.system.randomlootchest.managers.DatabaseManager;
import eu.goodyfx.system.randomlootchest.managers.RLCConfigManager;
import eu.goodyfx.system.randomlootchest.tasks.RLCParticleTask;
import eu.goodyfx.system.randomlootchest.tasks.SpawnTimerTask;
import eu.goodyfx.system.randomlootchest.utils.LoadChances;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class RandomLootChest implements RaspiSubSystem {

    private DatabaseManager databaseManager;
    private RLCConfigManager configManager;
    private LoadChances loadChances;
    private BukkitRunnable task;
    private BukkitRunnable particles;
    private final McRaspiSystem plugin;
    private boolean enabled = true;

    @Getter
    private final Map<Integer, ItemStack> items = new ConcurrentHashMap<>();

    @Getter
    private final NamespacedKey chestKey;

    public RandomLootChest(McRaspiSystem plugin) {
        this.plugin = plugin;
        this.chestKey = new NamespacedKey(plugin, "randomLootChest");
    }


    @Override
    public String systemKey() {
        return "randomLootChest";
    }

    @Override
    public boolean enabled() {
        return this.enabled;
    }

    @Override
    public void onEnabled() {
        if (enabled()) {
            Raspi.debugger().debug("INIT RandomLootChest SubSystem");
            init();
        }
    }

    @Override
    public void init() {
        managers();
        commands();
        events();
    }

    private void managers() {
        this.configManager = new RLCConfigManager(this);
        this.databaseManager = new DatabaseManager(this);
        this.loadChances = new LoadChances(this);

    }

    @Override
    public void events() {
        loadChances.loadItems();
    }

    public void tasks() {
        int time = configManager.getConfig().getInt("SpawnChestPerTime");
        this.task = new SpawnTimerTask(this, time);
        plugin.getTasks().add(task);
        this.particles = new RLCParticleTask(this);
        plugin.getTasks().add(particles);
    }


    @Override
    public void commands() {
        plugin.commandContainer.add(new RandomLootChestCommandContainer().command());
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void setEvents(Listener listener) {

    }

    @Override
    public void onDisable() {
        databaseManager.saveChests();
    }
}
