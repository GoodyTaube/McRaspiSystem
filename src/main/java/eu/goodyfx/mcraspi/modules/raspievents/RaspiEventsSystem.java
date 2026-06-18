package eu.goodyfx.mcraspi.modules.raspievents;

import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.utils.RaspiSubSystem;
import eu.goodyfx.mcraspi.core.utils.RaspiSubSystems;
import eu.goodyfx.mcraspi.modules.raspievents.events.CraftingEventListeners;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class RaspiEventsSystem implements RaspiSubSystem {

    protected final McRaspiSystem plugin;
    private boolean enabled = false;
    private final List<Listener> events = new ArrayList<>();
    public RaspiEventsSystem(McRaspiSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public String systemKey() {
        return RaspiSubSystems.EVENTS.getName();
    }

    @Override
    public boolean enabled() {
        return this.enabled;
    }

    @Override
    public void onEnabled() {
        if (enabled) {
            init();
            //new CanabolaCraftging(plugin);


        }
    }

    @Override
    public void init() {
        commands();
        events();
    }

    @Override
    public void events() {
        new CraftingEventListeners();
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
    }

}
