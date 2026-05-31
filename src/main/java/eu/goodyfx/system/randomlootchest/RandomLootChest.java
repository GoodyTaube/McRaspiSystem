package eu.goodyfx.system.randomlootchest;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.utils.RaspiSubSystem;
import eu.goodyfx.system.core.utils.RaspiSubSystems;
import eu.goodyfx.system.randomlootchest.commands.RandomLootChestCommandContainer;
import eu.goodyfx.system.randomlootchest.events.ItemAdderGUI;
import eu.goodyfx.system.randomlootchest.events.RandomLootChestPlayerEvents;
import eu.goodyfx.system.randomlootchest.managers.DatabaseManager;
import eu.goodyfx.system.randomlootchest.managers.RLCConfigManager;
import eu.goodyfx.system.randomlootchest.tasks.RLCTasks;
import eu.goodyfx.system.randomlootchest.utils.LoadChances;
import eu.goodyfx.system.randomlootchest.utils.OpenLootInventory;
import eu.goodyfx.system.randomlootchest.utils.RLCChest;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class RandomLootChest implements RaspiSubSystem {

    private DatabaseManager databaseManager;
    private RLCConfigManager configManager;
    private LoadChances loadChances;
    private OpenLootInventory openLootInventory;
    private ItemAdderGUI itemAdderGUI;
    private RandomLootChestPlayerEvents lootEvent;
    private RLCTasks rlcTasks;
    private final McRaspiSystem plugin;
    private boolean enabled = true;

    private final Map<Location, RLCChest> chestCache = new ConcurrentHashMap<>();
    private final Map<Integer, ItemStack> items = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> chances = new HashMap<>();
    private final Map<Integer, ItemStack> itemsToAdd = new HashMap<>();
    private final Map<Player, Integer> currentPage = new HashMap<>();
    private final Map<Player, Integer> isEditing = new HashMap<>();
    private final Map<Player, Integer> lastPageNO = new HashMap<>();
    private final List<Player> addItem = new ArrayList<>();


    public RandomLootChest(McRaspiSystem plugin) {
        this.plugin = plugin;
    }


    @Override
    public String systemKey() {
        return RaspiSubSystems.RANDO_LOOT_CHEST.getName();
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
        this.openLootInventory = new OpenLootInventory(this);

        this.rlcTasks = new RLCTasks(this);
    }


    @Override
    public void events() {
        loadChances.loadItems();
        itemAdderGUI = new ItemAdderGUI(this);
        lootEvent = new RandomLootChestPlayerEvents(this);
    }

    public void tasks() {

    }


    @Override
    public void commands() {
        plugin.commandContainer.add(new RandomLootChestCommandContainer(this).command());
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void setEvents(Listener listener) {
        plugin.setListeners(listener);
    }

    @Override
    public void onDisable() {
        rlcTasks.cancel = true;
    }
}
