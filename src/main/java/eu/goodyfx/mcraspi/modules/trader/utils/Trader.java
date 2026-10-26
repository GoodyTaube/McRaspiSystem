package eu.goodyfx.mcraspi.modules.trader.utils;

import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import eu.goodyfx.mcraspi.core.utils.RaspiSounds;
import eu.goodyfx.mcraspi.modules.trader.TraderSubSystem;
import eu.goodyfx.mcraspi.modules.trader.inventories.RecipeEditor;
import eu.goodyfx.mcraspi.modules.trader.inventories.TraderSettings;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Trader {

    private String uid;
    private final String name;
    private transient net.kyori.adventure.text.Component displayName = null;
    public final Map<Integer, TraderRecipe> recipes = new ConcurrentHashMap<>();
    private transient final Map<Integer, RecipeEditor> recipeEditorCache = new ConcurrentHashMap<>();
    private transient TraderSubSystem system;
    private transient TraderSettings traderSettings;
    @Setter
    private boolean random = false;

    public Trader(TraderSubSystem system, String name, String uid) {
        this.name = name;
        this.uid = uid;
        this.displayName = MiniMessage.miniMessage().deserialize("<green>" + name);
        this.system = system;
        this.traderSettings = new TraderSettings(system.getPlugin(), this);
    }

    public Trader() {
        this.name = null;
        this.uid = null;
        this.displayName = null;
        this.system = null;
    }

    public void init(TraderSubSystem system) {
        this.system = system;
        this.traderSettings = new TraderSettings(system.getPlugin(), this);
    }

    public Component displayName() {
        return displayName != null ? displayName : MiniMessage.miniMessage().deserialize(String.format("<green>%s", name));
    }

    public void setRecipe(TraderRecipe traderRecipe) {
        recipes.put(traderRecipe.getId(), traderRecipe);
    }

    public void removeRecipe(int id) {
        recipes.remove(id);
    }

    public TraderRecipe getRecipeByID(Integer id) {
        return id != null ? recipes.get(id) : null;
    }

    public Collection<TraderRecipe> getRecipes() {
        return recipes.values();
    }

    public Villager spawnTraderDummy(Plugin plugin, Location location) {
        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.customName(displayName());
        PersistentDataContainer container = villager.getPersistentDataContainer();
        container.set(system.getTraderKey(), PersistentDataType.STRING, this.uid);
        return configureDefaultTrader(villager);
    }

    private Villager configureDefaultTrader(Villager villager) {
        villager.setAI(false);
        villager.setCustomNameVisible(true);
        villager.setGravity(false);
        villager.setCollidable(false);
        villager.setInvulnerable(true);
        return villager;
    }

    public Inventory recipeEditor(Integer id) {
        Inventory inventory;
        if (id != null && recipeEditorCache.containsKey(id)) {
            inventory = recipeEditorCache.get(id).getInventory();
        } else {
            RecipeEditor editor = new RecipeEditor(system, this, id);
            if (id == null) {
                id = 0;
            }
            recipeEditorCache.put(id, editor);
            inventory = editor.getInventory();
        }
        return inventory;
    }

    public void openTraderSettings(RaspiPlayer raspiPlayer) {
        traderSettings.reloadContents();
        raspiPlayer.openInventory(traderSettings.getInventory());
        raspiPlayer.playSound(RaspiSounds.SUCCESS);
    }

    public int getNextRecipeID() {
        if (recipes.isEmpty()) {
            return 0;
        }
        return recipes.keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
    }

}
