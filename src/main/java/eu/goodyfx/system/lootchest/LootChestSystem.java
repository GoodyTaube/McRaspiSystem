package eu.goodyfx.system.lootchest;

import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.lootchest.events.LootChestListeners;
import eu.goodyfx.system.lootchest.events.LootConsumeEvents;
import eu.goodyfx.system.lootchest.events.LootSpongeEvents;
import eu.goodyfx.system.core.utils.SystemTemplate;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LootChestSystem implements SystemTemplate {

    protected final McRaspiSystem plugin;
    private boolean enabled = false;

    private final List<Listener> events = new ArrayList<>();
    private final List<LiteralCommandNode<CommandSourceStack>> commandNodes = new ArrayList<>();

    public LootChestSystem(McRaspiSystem plugin) {
        this.plugin = plugin;
        onEnabled();
    }


    @Override
    public boolean enabled() {
        return this.enabled;
    }

    @Override
    public void onEnabled() {
        init();
    }

    @Override
    public void init() {
        commands();
        events();
    }

    @Override
    public void events() {
        setEvents(new LootChestListeners(plugin));
        setEvents(new LootSpongeEvents(plugin));
        setEvents(new LootConsumeEvents(plugin));
    }

    @Override
    public void commands() {

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
    }
}
