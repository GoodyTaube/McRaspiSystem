package eu.goodyfx.system.trader;

import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.RaspiSubSystem;
import eu.goodyfx.system.trader.commands.TraderCommand;
import eu.goodyfx.system.trader.events.TraderListeners;
import org.bukkit.event.Listener;

public class TraderSystem implements RaspiSubSystem {

    protected final McRaspiSystem plugin;
    private boolean enabled = false;

    public TraderSystem(McRaspiSystem plugin) {
        this.plugin = plugin;
    }


    @Override
    public String systemKey() {
        return "raspiTrader";
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
    public void onDisable() {
        setEnabled(false);
    }

    @Override
    public void events() {
        new TraderListeners();
    }

    @Override
    public void commands() {
        new TraderCommand();
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
