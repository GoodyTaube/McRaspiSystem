package eu.goodyfx.mcraspisystem.events;

import eu.goodyfx.goodysutilities.utils.PlayerValues;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class PlayerAFKEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final List<UUID> afkContainer = new ArrayList<>();

    public PlayerAFKEvent(RaspiPlayer player) {
        if (player.userManager().hasPersistantValue(player.getPlayer(), PlayerValues.AFK)) {
            afkContainer.add(player.getUUID());
        } else {
            afkContainer.remove(player.getUUID());
        }
    }

    public List<UUID> getAfkContainer() {
        return this.afkContainer;
    }

    public boolean isAFK(Player player) {
        return this.afkContainer.contains(player.getUniqueId());
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
