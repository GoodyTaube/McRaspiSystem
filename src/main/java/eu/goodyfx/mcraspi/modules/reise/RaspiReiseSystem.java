package eu.goodyfx.mcraspi.modules.reise;

import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.utils.RaspiSubSystem;
import eu.goodyfx.mcraspi.core.utils.RaspiSubSystems;
import eu.goodyfx.mcraspi.modules.reise.commands.RBSucheCommandContainer;
import eu.goodyfx.mcraspi.modules.reise.commands.reise.ReiseCommandContainer;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RaspiReiseSystem implements RaspiSubSystem {

    protected final McRaspiSystem plugin;
    private boolean enabled = false;

    private final List<Listener> registeredEvents = new ArrayList<>();

    public RaspiReiseSystem(McRaspiSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public String systemKey() {
        return RaspiSubSystems.REISE.getName();
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
    }

    @Override
    public void events() {
    }

    @Override
    public void commands() {
        new ReiseCommandContainer(this);
        new RBSucheCommandContainer(this);
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
