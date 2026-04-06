package eu.goodyfx.system.lootchest;

import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.api.Raspi;
import eu.goodyfx.system.core.utils.RaspiSubSystem;
import eu.goodyfx.system.core.utils.RaspiSubSystems;
import eu.goodyfx.system.lootchest.commands.LootChestCommand;
import eu.goodyfx.system.lootchest.commands.RaspiItemsCommand;
import eu.goodyfx.system.lootchest.events.LootChestListeners;
import eu.goodyfx.system.lootchest.events.LootConsumeEvents;
import eu.goodyfx.system.lootchest.events.LootSpongeEvents;
import eu.goodyfx.system.lootchest.events.TeleportListeners;
import eu.goodyfx.system.lootchest.tasks.AnimationBlockDisplay;
import eu.goodyfx.system.lootchest.tasks.LootChestTimer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LootChestSystem implements RaspiSubSystem {

    protected final McRaspiSystem plugin;
    private boolean enabled = false;
    @Getter
    private static LootChestSystem lootChestSubSystem;
    private BukkitRunnable animationBlockDisplay;
    public LootChestTimer lootChestTimer;

    private final List<Listener> events = new ArrayList<>();
    private final List<LiteralCommandNode<CommandSourceStack>> commandNodes = new ArrayList<>();

    public LootChestSystem(McRaspiSystem plugin) {
        this.plugin = plugin;
        lootChestSubSystem = this;
    }


    @Override
    public String systemKey() {
        return RaspiSubSystems.LOOTING.getName();
    }

    @Override
    public boolean enabled() {
        return this.enabled;
    }

    @Override
    public void onEnabled() {
        if (enabled) {
            init();
        }
    }

    @Override
    public void init() {
        commands();
        events();
        tasks();
        for (Listener listener : events) {
            Bukkit.getPluginManager().registerEvents(listener, plugin);
            Raspi.debugger().info("[LOOT:EVENTS] registered :: " + listener.toString());
        }
    }

    private void tasks() {
        this.animationBlockDisplay = new AnimationBlockDisplay(plugin);
        this.lootChestTimer = new LootChestTimer(plugin);
    }

    @Override
    public void events() {
        setEvents(new LootChestListeners(plugin));
        setEvents(new LootSpongeEvents(plugin));
        setEvents(new LootConsumeEvents(plugin));
        setEvents(new TeleportListeners());
    }

    @Override
    public void commands() {
        new LootChestCommand();
        new RaspiItemsCommand(plugin);
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void setEvents(Listener listener) {
        if (!events.contains(listener)) {
            events.add(listener);
        }
    }

    @Override
    public void onDisable() {
        setEnabled(false);
        for (Listener event : events) {
            HandlerList.unregisterAll(event);
        }
        events.clear();
        this.animationBlockDisplay.cancel();
        this.lootChestTimer.cancel();
    }
}
