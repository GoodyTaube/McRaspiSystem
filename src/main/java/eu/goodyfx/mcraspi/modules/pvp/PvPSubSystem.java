package eu.goodyfx.mcraspi.modules.pvp;

import eu.goodyfx.mcraspi.McRaspiSystem;
import eu.goodyfx.mcraspi.core.utils.RaspiSubSystem;
import eu.goodyfx.mcraspi.core.utils.RaspiSubSystems;
import eu.goodyfx.mcraspi.modules.pvp.commands.PvPCommandContainer;
import eu.goodyfx.mcraspi.modules.pvp.events.PvPEvents;
import eu.goodyfx.mcraspi.modules.pvp.utils.Particles;
import eu.goodyfx.mcraspi.modules.pvp.utils.PvPManager;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;

@Getter
public class PvPSubSystem implements RaspiSubSystem {

    private final McRaspiSystem plugin;
    private boolean enabled = true;
    private final NamespacedKey key;

    private PvPManager pvPManager;


    public PvPSubSystem(McRaspiSystem plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, RaspiSubSystems.PVP.getName());
    }

    @Override
    public String systemKey() {
        return "pvp";
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
        plugin.getTasks().add(new Particles(this));
        this.pvPManager = new PvPManager();
        commands();
        events();
    }

    @Override
    public void events() {
        new PvPEvents(this);
    }

    @Override
    public void commands() {
        new PvPCommandContainer(this);
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void setEvents(Listener listener) {

    }

    @Override
    public void onDisable() {

    }
}
