package eu.goodyfx.system.reise;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.SystemTemplate;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RaspiReiseSystem implements SystemTemplate {

    protected final McRaspiSystem plugin;
    private boolean enabled = false;

    private final List<Listener> registeredEvents = new ArrayList<>();

    public RaspiReiseSystem(McRaspiSystem plugin) {
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
        if (!registeredEvents.contains(listener)) {
            registeredEvents.add(listener);
        }
    }

    @Override
    public void onDisable() {
        setEnabled(false);
        for (Listener event : registeredEvents) {
            HandlerList.unregisterAll(event);
        }
        registeredEvents.clear();
    }
}
