package eu.goodyfx.system.trader;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.SystemTemplate;
import org.bukkit.event.Listener;

public class TraderSystem implements SystemTemplate {

    protected final McRaspiSystem plugin;
    private boolean enabled = false;

    public TraderSystem(McRaspiSystem plugin) {
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
    public void onDisable() {
        setEnabled(false);
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
        plugin.setListeners(listener);
    }
}
