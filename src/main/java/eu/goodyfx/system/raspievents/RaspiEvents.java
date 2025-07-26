package eu.goodyfx.system.raspievents;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.SystemTemplate;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class RaspiEvents implements SystemTemplate {

    protected final McRaspiSystem plugin;
    private boolean enabled = false;
    private final List<Listener> events = new ArrayList<>();

    public RaspiEvents(McRaspiSystem plugin) {
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
