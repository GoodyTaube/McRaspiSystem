package eu.goodyfx.system.lootchest;

import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.RaspiSubSystem;
import eu.goodyfx.system.lootchest.commands.LootChestCommand;
import eu.goodyfx.system.lootchest.commands.RaspiItemsCommand;
import eu.goodyfx.system.lootchest.events.LootChestListeners;
import eu.goodyfx.system.lootchest.events.LootConsumeEvents;
import eu.goodyfx.system.lootchest.events.LootSpongeEvents;
import eu.goodyfx.system.lootchest.tasks.AnimationBlockDisplay;
import eu.goodyfx.system.lootchest.tasks.LootChestTimer;
import eu.goodyfx.system.lootchest.tasks.RaspiItemsTimer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LootChestSystem implements RaspiSubSystem {

    protected final McRaspiSystem plugin;
    private boolean enabled = false;

    private BukkitRunnable animationBlockDisplay;
    private BukkitRunnable lootChestTimer;
    private BukkitRunnable raspiItemsTimer;

    private final List<Listener> events = new ArrayList<>();
    private final List<LiteralCommandNode<CommandSourceStack>> commandNodes = new ArrayList<>();

    public LootChestSystem(McRaspiSystem plugin) {
        this.plugin = plugin;
    }


    @Override
    public String systemKey() {
        return "raspiLoot";
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
    }

    private void tasks() {
        this.animationBlockDisplay = new AnimationBlockDisplay(plugin);
        this.lootChestTimer = new LootChestTimer(plugin);
        this.raspiItemsTimer = new RaspiItemsTimer(plugin);
    }

    @Override
    public void events() {
        setEvents(new LootChestListeners(plugin));
        setEvents(new LootSpongeEvents(plugin));
        setEvents(new LootConsumeEvents(plugin));
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
        this.raspiItemsTimer.cancel();
        this.lootChestTimer.cancel();
    }
}
