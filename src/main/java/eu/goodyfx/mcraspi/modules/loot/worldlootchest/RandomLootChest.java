package eu.goodyfx.mcraspi.modules.loot.worldlootchest;

import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.api.Raspi;
import eu.goodyfx.mcraspi.core.utils.RaspiSubSystem;
import eu.goodyfx.mcraspi.core.utils.RaspiSubSystems;
import eu.goodyfx.mcraspi.modules.loot.worldlootchest.commands.RandomLootChestCommandContainer;
import eu.goodyfx.mcraspi.modules.loot.worldlootchest.events.ItemAdderGUI;
import eu.goodyfx.mcraspi.modules.loot.worldlootchest.events.RandomLootChestPlayerEvents;
import eu.goodyfx.mcraspi.modules.loot.worldlootchest.managers.DatabaseManager;
import eu.goodyfx.mcraspi.modules.loot.worldlootchest.managers.RLCConfigManager;
import eu.goodyfx.mcraspi.modules.loot.worldlootchest.tasks.RLCTasks;
import eu.goodyfx.mcraspi.modules.loot.worldlootchest.utils.LoadChances;
import eu.goodyfx.mcraspi.modules.loot.worldlootchest.utils.OpenLootInventory;
import eu.goodyfx.mcraspi.modules.loot.worldlootchest.utils.RLCChest;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

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
        new RandomLootChestCommandContainer(this);
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
