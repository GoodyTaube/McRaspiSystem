package eu.goodyfx.system.raspievents;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.RaspiSubSystem;
import eu.goodyfx.system.raspievents.craftings.CanabolaCraftging;
import eu.goodyfx.system.raspievents.craftings.EntityGranadeCrafting;
import eu.goodyfx.system.raspievents.events.CraftingEventListeners;
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
        return "raspiEvents";
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
        recipes();
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


    private void recipes() {
        new CanabolaCraftging(plugin);
        new EntityGranadeCrafting();
    }

}
