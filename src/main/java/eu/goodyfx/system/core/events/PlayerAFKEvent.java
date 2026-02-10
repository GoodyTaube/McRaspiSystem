package eu.goodyfx.system.core.events;

import eu.goodyfx.system.core.database.RaspiPlayer;
import eu.goodyfx.system.core.utils.Raspi;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@Getter
public class PlayerAFKEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final RaspiPlayer player;

    public PlayerAFKEvent(RaspiPlayer player) {
        this.player = player;
        if (player.settings().isAfk()) {
            Raspi.players().getAfkContainer().put(player.getUUID(), player.getLocation());
        } else {
            Raspi.players().getAfkContainer().remove(player.getUUID());
        }
    }

    public boolean isAFK() {
        return player.settings().isAfk();
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
