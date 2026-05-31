package eu.goodyfx.system.trader;

import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.goodyfx.system.McRaspiSystem;
import eu.goodyfx.system.core.utils.RaspiSubSystem;
import eu.goodyfx.system.core.utils.RaspiSubSystems;
import eu.goodyfx.system.trader.commands.TraderCommandContainer;
import eu.goodyfx.system.trader.events.TraderListeners;
import eu.goodyfx.system.trader.managers.TraderDB;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;

public class TraderSubSystem implements RaspiSubSystem {

    protected final McRaspiSystem plugin;
    @Getter
    private TraderDB traderDB;
    @Getter
    private final NamespacedKey traderKey;
    private boolean enabled = false;

    public TraderSubSystem(McRaspiSystem plugin) {
        this.plugin = plugin;
        this.traderKey = plugin.getNameSpaced("traderKey");
    }


    @Override
    public String systemKey() {
        return RaspiSubSystems.TRADER.getName();
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
        this.traderDB = new TraderDB();
        commands();
        events();
    }

    public void addCommand(LiteralCommandNode<CommandSourceStack> command) {
        plugin.commandContainer.add(command);
    }

    @Override
    public void onDisable() {
        setEnabled(false);
    }

    @Override
    public void events() {
        new TraderListeners(this);
    }

    @Override
    public void commands() {
        new TraderCommandContainer(this);
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
