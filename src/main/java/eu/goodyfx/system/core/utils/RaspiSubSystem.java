package eu.goodyfx.system.core.utils;

import org.bukkit.event.Listener;

public interface RaspiSubSystem {

    String systemKey();
    boolean enabled();

    void onEnabled();

    void init();

    void events();

    void commands();

    void setEnabled(boolean enabled);

    void setEvents(Listener listener);

    void onDisable();


}
