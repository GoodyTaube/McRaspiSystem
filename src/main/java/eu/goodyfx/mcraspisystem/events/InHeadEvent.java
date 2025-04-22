package eu.goodyfx.mcraspisystem.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class InHeadEvent extends Event {
    private final HandlerList handlerList = new HandlerList();

    private final Player player;
    private final Player target;

    public InHeadEvent(Player player, Player target){
        this.player = player;
        this.target = target;
    }



    @Override
    public @NotNull HandlerList getHandlers() {
        return this.handlerList;
    }

}
